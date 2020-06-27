package zad1.client;

import zad1.utils.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static zad1.utils.Config.log;

public class ClientService extends Service {



    private GUIClient guiController;

    private SocketChannel client;

    public ClientService(GUIClient clientGuiController) {
        this.guiController = clientGuiController;
    }

    @Override
    public SocketChannel getClient() {
        return this.client;
    }

    @Override
    protected void setClient(SocketChannel socketChannel) {
        this.client = socketChannel;
    }



    protected String readMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        channel.read(buffer);
        return new String(buffer.array()).trim();
    }

    private void sendMessage(String msg) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            client.write(buffer);
            buffer.clear();
    }

    void subscribeToTopic(String topic) throws IOException {
        System.out.println("subscribing to topic. client:" + this.client);
        String message = "#SUB_" + topic;
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    void unsubscribeFromTopic(String topic) throws IOException {
        System.out.println("unsubscribing from topic. client:" + this.client);
        String message = "#UNSUB_" + topic;
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    public void refreshTopics() throws IOException {
        System.out.println("Refreshing possible topics. client:" + this.client);
        String message = "#TOPICS";
        log("Sending message " + message + " to client: " + client);
        sendMessage(message);
    }

    protected void handleReceivedMessage(String messageFromServer) {
        if (messageFromServer.startsWith("#NEWS_")) {
            passNewsToController(messageFromServer);
        } else if (messageFromServer.startsWith("#TOPICS")) {
            passRefreshedTopicsToController(messageFromServer);
        }
    }

    void passRefreshedTopicsToController(String message) {
        this.guiController.handleRefreshedTopics(message);
    }

    void passNewsToController(String message) {
        this.guiController.handleNews(message);
    }

}
