package zad1.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static zad1.utils.Config.log;

public abstract class Service {

    private static final int MAIN_SERVER_PORT = Config.MAIN_SERVER_PORT;
    private static final String MAIN_SERVER_HOST = Config.MAIN_SERVER_HOSTNAME;




    public void startClient() throws IOException {
        Selector clientSelector = Selector.open();
        setClient(SocketChannel.open(new InetSocketAddress(MAIN_SERVER_HOST, MAIN_SERVER_PORT)));
        getClient().configureBlocking(false);

        SelectionKey selectionKey = getClient().register(clientSelector, SelectionKey.OP_READ);

        while (true) {
            log("*** " + this.getClass().getName() + " " + "Waiting for the select operation...");
            int readyChannels = clientSelector.select();
            if (readyChannels == 0)
                continue;

            Set<SelectionKey> selectedKeys = clientSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    log("*** " + this.getClass().getName() + " key is acceptable");

                } else if (key.isConnectable()) {
                    log("*** " + this.getClass().getName() + " key is connectable");

                } else if (key.isReadable()) {
                    log("*** " + this.getClass().getName() + " key is readable");

                    SocketChannel serverChannel = (SocketChannel) key.channel();

                    String messageFromServer = readMessage(serverChannel);
                    log("Message read from server: " + messageFromServer);

                    handleReceivedMessage(messageFromServer);

                    serverChannel.register(clientSelector, SelectionKey.OP_READ);

                } else if (key.isWritable()) {
                    log("*** " + this.getClass().getName() + " key is writable");
                }

                keyIterator.remove();
            }
        }
    }

    protected abstract String readMessage(SocketChannel serverChannel) throws IOException ;
    protected abstract void handleReceivedMessage(String receivedMessage);
    protected abstract SocketChannel getClient();
    protected abstract void setClient(SocketChannel socketChannel);
    public abstract void refreshTopics()throws IOException ;

}
