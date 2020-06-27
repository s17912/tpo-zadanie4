//package zad1.utils;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import zad1.client.ClientService;
//
//import java.io.IOException;
//
//import static zad1.utils.Config.log;
//
//public class ClientGuiController {
//
//    private ClientService clientService;
//
//    @FXML
//    private TextField registerTopicTxtField;
//
//    @FXML
//    private Button subscribeBttn;
//
//    @FXML
//    private TextField removeTopicTxtField;
//
//    @FXML
//    private Button unsubscribeBttn;
//
//    @FXML
//    private TextArea newsTxtArea;
//
//    @FXML
//    private TextArea topicsTextArea;
//
//    @FXML
//    private Button clearAllBttn;
//
//    @FXML
//    private Button refreshTopicsBttn;
//
//    @FXML
//    private TextField msgTxtField;
//
//    public void initialize() throws IOException {
//        log("Client GUIClient initialized");
//        this.clientService = new ClientService(this);
//        startClient(this.clientService);
//    }
//
//    private void startClient(ClientService clientService) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    clientService.startClient();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        Thread clientServerThread = new Thread(runnable);
//        clientServerThread.start();
//    }
//
//    public void subscribe(ActionEvent actionEvent) throws IOException {
//        String topic = this.registerTopicTxtField.getText();
//        setMessageForMessageFiled("Subscribed to topic");
//        this.clientService.subscribeToTopic(topic);
//    }
//
//
//    public void unsubscribe(ActionEvent actionEvent) throws IOException {
//        String topic = this.removeTopicTxtField.getText();
//        setMessageForMessageFiled("Unsubscribed from topic");
//        this.clientService.unsubscribeFromTopic(topic);
//    }
//
//    public void refreshTopics(ActionEvent actionEvent) throws IOException {
//        setMessageForMessageFiled("Refreshed topics");
//        this.clientService.refreshTopics();
//    }
//
//    public void handleRefreshedTopics(String message) {
//        this.topicsTextArea.setText("");
//        String[] topics = message.split(" ");
//        for (String s: topics) {
//            this.topicsTextArea.appendText(s + "\n");
//        }
//    }
//
//    public void handleNews(String message) {
//        this.newsTxtArea.appendText(message.substring(6) + "\n");
//    }
//
//    public void clearAll(ActionEvent actionEvent) {
//        setMessageForMessageFiled("Cleared all");
//        this.newsTxtArea.setText("");
//        this.topicsTextArea.setText("");
//        this.registerTopicTxtField.setText("");
//        this.removeTopicTxtField.setText("");
//    }
//
//    private void setMessageForMessageFiled(String msg) {
//        this.msgTxtField.setText(msg);
//    }
//
//}
