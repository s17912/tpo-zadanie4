package zad1.admin;

import zad1.utils.Service;
import zad1.utils.TopicModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static zad1.utils.Config.log;

public class AdminService extends Service {

    private final GUIAdmin guiController;

    private SocketChannel client;

    @Override
    public SocketChannel getClient() {
        return this.client;
    }

    @Override
    protected void setClient(SocketChannel socketChannel) {
        this.client = socketChannel;
    }


    public AdminService(GUIAdmin adminGuiController) {
        this.guiController = adminGuiController;
    }


    @Override
    protected String readMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        channel.read(buffer);
        return new String(buffer.array()).trim();
    }

    private void sendMessage(String msg) throws IOException {
        byte[] message = msg.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        getClient().write(buffer);
        buffer.clear();
    }

    void registerTopic(String topic) throws IOException {
        String message = "*REG_" + topic;
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    void deregisterTopic(String topic) throws IOException {
        String message = "*DEL_" + topic;
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    void publishNews(String topic, String news) throws IOException {

        String message = "*NEWS_" + topic+"_"+news;
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    public void refreshTopics() throws IOException {
        String message = "*TOPICS";
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    protected void handleReceivedMessage(String messageFromServer) {
        if (messageFromServer.startsWith("*TOPICS")) {
            passRefreshedTopicsToController(messageFromServer);
        }
    }

    void passRefreshedTopicsToController(String message) {

        this.guiController.allTopicList.clear();
        String[] topics = message.split(" ");

        for (String s: topics) {
            if(s.startsWith("*TOPICS")){continue;}
            this.guiController.allTopicList.add(new TopicModel(s));

        }




    }

}
