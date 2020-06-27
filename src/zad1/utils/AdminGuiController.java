//package zad1.utils;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import zad1.admin.AdminService;
//import zad1.utils.Service;
//
//
//import java.io.IOException;
//
//import static zad1.utils.Config.log;
//
//public class AdminGuiController {
//
//    private AdminService service;
//
//    @FXML
//    private TextField registerTopicTxtField;
//
//    @FXML
//    private Button registerTopicBttn;
//
//    @FXML
//    private TextField removeTopicTxtField;
//
//    @FXML
//    private Button removeTopicBttn;
//
//    @FXML
//    private ComboBox<String> chooseTopicCmbBox;
//
//    @FXML
//    private TextArea newsTxtArea;
//
//    @FXML
//    private TextArea topicsTextArea;
//
//    @FXML
//    private Button publishNewsBttn;
//
//    @FXML
//    private Button refreshBttn;
//
//    @FXML
//    private TextField msgTxtField;
//
//    public void initialize() throws IOException {
//        log("Admin GUIClient initialized");
//        this.service =  new AdminService(this);
//        this.chooseTopicCmbBox.getItems().clear();
//        this.chooseTopicCmbBox.setValue("NONE");
//        startAdminClient(this.service);
//    }
//
//    private void startAdminClient(Service service) {
//        Runnable runnable = () -> {
//            try {
//                service.startClient();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        };
//        Thread clientServerThread = new Thread(runnable);
//        clientServerThread.start();
//    }
//
//    public void register(ActionEvent actionEvent) throws IOException {
//        String topic = this.registerTopicTxtField.getText();
//        setMessageForMessageFiled("Topic registered!");
//        this.service.registerTopic(topic);
//    }
//
//    public void deregister(ActionEvent actionEvent) throws IOException {
//        String topic = this.removeTopicTxtField.getText();
//        setMessageForMessageFiled("Topic deregistered!");
//        this.service.deregisterTopic(topic);
//    }
//
//    public void publishNews(ActionEvent actionEvent) throws IOException {
//        String news = this.newsTxtArea.getText();
//        String topic = this.chooseTopicCmbBox.getValue();
//        setMessageForMessageFiled("News published!");
//        this.service.publishNews(topic + "_" + news);
//    }
//
//    public void refreshTopics(ActionEvent actionEvent) throws IOException {
//        setMessageForMessageFiled("Topic refreshed!");
//        this.service.refreshTopics();
//    }
//
//    public void handleRefreshedTopics(String message) {
//        // Set all topics to text field
//        this.topicsTextArea.setText("");
//        String[] topics = message.split(" ");
//        for (String s: topics) {
//            this.topicsTextArea.appendText(s + "\n");
//        }
//
//        //populate combobox with new values
//        this.chooseTopicCmbBox.getItems().clear();
//        this.chooseTopicCmbBox.getItems().addAll(topics);
//        this.chooseTopicCmbBox.getItems().remove(topics[0]); // remove header
//    }
//
//    public void clearAll(ActionEvent actionEvent) {
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
//
//}
