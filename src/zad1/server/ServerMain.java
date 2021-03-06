package zad1.server;

import zad1.utils.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static zad1.utils.Config.log;


public class ServerMain {

    private static final int MAIN_SERVER_PORT = Config.MAIN_SERVER_PORT;
    private static final String MAIN_SERVER_HOST = Config.MAIN_SERVER_HOSTNAME;

    private static Map<String, List<ClientSubscriber>> topicSubscribers = new HashMap<>();
    private static Map<String, String> newsOnTopic = new HashMap<>();
    private static Set<String> allRegisteredTopics = new HashSet<>();

    public static void main(String[] args) throws IOException {
        startMainServer();
    }

    static void startMainServer() throws IOException {
        Selector selector = Selector.open();
        log("** Selector is open for making connection: " + selector.isOpen());

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(MAIN_SERVER_HOST, MAIN_SERVER_PORT));
        serverSocketChannel.configureBlocking(false);

        int ops = serverSocketChannel.validOps();
        serverSocketChannel.register(selector, ops, null);

        while (true) {
            log("** Waiting for the select operation...");

            int noOfKeys = selector.select();
            log("** The Number of selected keys are: " + noOfKeys);

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    log("** key is acceptable - someone connected with this server");

                    SocketChannel client = serverSocketChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    log("** the new connection is accepted from the client: " + client);

                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();

                    String output = readMessage(client);
                    log("Message read from client: " + output);


                    if (output.startsWith("#SUB_")) {
                        String[] subWithTopic = output.split("_");
                        if (subWithTopic.length != 2) {
                            log("Client wants to subscribe to empty or invalid topic!");
                        }

                        String topic = subWithTopic[1];
                        ClientSubscriber sub = new ClientSubscriber(topic, key);

                        List<ClientSubscriber> subscribers;
                        if (topicSubscribers.containsKey(topic)) {
                            subscribers = topicSubscribers.get(topic);
                        } else {
                            subscribers = new LinkedList<>();
                        }
                        subscribers.add(sub);
                        topicSubscribers.put(topic, subscribers);

                    } else if (output.startsWith("#UNSUB_")) {
                        handleClientUnsubscribeFromTopic(output, key);
                    } else if (output.startsWith("#TOPICS")) {
                        TopicInfoSubscriber topicInfoSubscriber = new TopicInfoSubscriber("client", key);
                        client.register(selector, SelectionKey.OP_WRITE, topicInfoSubscriber);
                    }

                    if (output.startsWith("*NEWS_")) {
                        passNewsFromAdminToClients(output, selector, key);
                    } else if (output.startsWith("*TOPICS")) {
                        TopicInfoSubscriber topicInfoSubscriber = new TopicInfoSubscriber("admin", key);
                        client.register(selector, SelectionKey.OP_WRITE, topicInfoSubscriber);
                    } else if (output.startsWith("*REG_")) {
                        log("Messages from admin "+output);
                        registerNewTopic(output);
                    } else if (output.startsWith("*DEL_")) {
                        log(output);
                        deleteExistingTopic(output);
                        deleteSubscribersOfTopic(output);
                        deleteNewsOnTopic(output);
                    }

                } else if (key.isWritable()) {
                    log("** Key is writable");

                    SocketChannel clientChannel = (SocketChannel) key.channel();

                    String msg = createMessageToSend(key);

                    byte[] message = msg.getBytes();
                    ByteBuffer buff = ByteBuffer.wrap(message);
                    clientChannel.write(buff);
                    buff.clear();

                    clientChannel.register(selector, SelectionKey.OP_READ);

                } else if (key.isConnectable()) {
                    log("** key is connectable");
                }

                iterator.remove();
            }
        }
    }

    private static void handleClientUnsubscribeFromTopic(String output, SelectionKey ky) {
        if (output.length() == 7) {
            log("------ client send empty topic");
            return; // client send empty topic
        }

        String topic = output.split("_")[1];
        if (!topicSubscribers.containsKey(topic)) {
            log("---- topicsubscribers dont have key " + topic);
            return;
        }
        List<ClientSubscriber> subscribers = topicSubscribers.get(topic);
        ClientSubscriber subscriberToRemove = null;
        for (ClientSubscriber s: subscribers) {
            if (s.selectionKey.equals(ky)) {
                System.out.println("client unsubscribed from topic found");
                subscriberToRemove = s;
            }
        }
        if (subscriberToRemove == null) {
            log("****** client never subscribed to topic " + topic);
        } else {
            log("***** removing client from subscribers ******");
            subscribers.remove(subscriberToRemove);
            topicSubscribers.put(topic, subscribers);
        }
    }

    private static void deleteExistingTopic(String output) {
        allRegisteredTopics.remove(output.substring(5));
    }

    private static void deleteSubscribersOfTopic(String output) {
        topicSubscribers.remove(output.substring(5));
    }

    private static void deleteNewsOnTopic(String output) {
        newsOnTopic.remove(output.substring(5));
    }

    private static void registerNewTopic(String output) {
        log("registerNewTopic "+ output);
        allRegisteredTopics.add(output.substring(5));
    }

    private static String readMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        channel.read(buffer);
        return new String(buffer.array()).trim();
    }

    private static String createMessageToSend(SelectionKey ky) throws ExportException {
        if (ky.attachment() == null) {
            throw new ExportException("This shouldn't happen. Null attachment");
        }

        String msg = "";
        if (ky.attachment() instanceof ClientSubscriber) {

            String topic = ((ClientSubscriber) ky.attachment()).topic;
            System.out.println("Sending news on topic: " + topic);
            msg = "#NEWS_" + topic + "_" + newsOnTopic.get(topic);

        } else if (ky.attachment() instanceof TopicInfoSubscriber) {

            if (((TopicInfoSubscriber) ky.attachment()).identification.equals("admin")) {
                msg = "*TOPICS: " + getPossibleNewsTopics();
            } else {
                msg = "#TOPICS: " + getPossibleNewsTopics();
            }

        }

        return msg;
    }

    private static String getPossibleNewsTopics() {
        StringBuilder topics = new StringBuilder();
        for (String it : allRegisteredTopics) {
            topics.append(it).append(" ");
        }
        return topics.toString();
    }

    private static void passNewsFromAdminToClients(String output, Selector selector, SelectionKey ky) throws ClosedChannelException {
        String[] topicWithNews = output.split("_");

        if (topicWithNews.length != 3) {
            System.out.println("Wrong length of mesage");
            return;
        }

        String topic = topicWithNews[1];
        String news = topicWithNews[2];

        if (!topicSubscribers.containsKey(topic)) {
            System.out.println("Topic subscribers doesnt have key " + topic);
            return;
        }

        // update current news for topic
        System.out.println("Updating current news on topic " + topic);
        newsOnTopic.put(topic, news);

        List<ClientSubscriber> subscribers = topicSubscribers.get(topic);
        for (ClientSubscriber clientSubscriber : subscribers) {
            SocketChannel cl = (SocketChannel) clientSubscriber.selectionKey.channel();
            cl.register(selector, SelectionKey.OP_WRITE, clientSubscriber);
            ky.attach(clientSubscriber);
        }
    }

}


