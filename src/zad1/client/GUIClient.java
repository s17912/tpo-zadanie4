package zad1.client;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import zad1.utils.BaseGUI;
import zad1.utils.TopicModel;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static zad1.utils.Config.log;


public class GUIClient extends BaseGUI {
    private ClientService clientService;
    private Set<String> subscribedTopics = new HashSet<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        initialize();
        String APP_TITLE = "Client Application";
        Label label = new Label(APP_TITLE);
        label.setFont(Font.font("Cambria", 32));
        borderPane.setTop(label);
        label.setPadding(new Insets(10, 10, 10, 10));
        addButtonToTable();


        reloadTopicsButton.setOnAction(s -> {
            try {
                this.clientService.refreshTopics();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        primaryStage.show();
    }

    public void initialize() {
        log("Client GUIClient initialized");
        this.clientService = new ClientService(this);
        startClient(this.clientService);
    }

    private void startClient(ClientService clientService) {
        Runnable runnable = () -> {
            try {
                clientService.startClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread clientServerThread = new Thread(runnable);
        clientServerThread.start();
    }

    private void addButtonToTable() {


        Callback<TableColumn<TopicModel, Void>, TableCell<TopicModel, Void>> cellFactory = new Callback<TableColumn<TopicModel, Void>, TableCell<TopicModel, Void>>() {
            public TableCell<TopicModel, Void> call(final TableColumn<TopicModel, Void> param) {
                final TableCell<TopicModel, Void> cell = new TableCell<TopicModel, Void>() {

                    private final Button btn = new Button("Subscribe");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            TopicModel data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                            if(subscribedTopics.contains(data.getName())){
                                try {
                                    clientService.unsubscribeFromTopic(data.getName());
                                    subscribedTopics.remove(data.getName());
                                    btn.setText("Subscribe");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                try {
                                    clientService.subscribeToTopic(data.getName());
                                    subscribedTopics.add(data.getName());
                                    btn.setText("Unsubscribe");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        colBtn.setPrefWidth(100);
        colBtn.setResizable(false);
        topicsView.getColumns().add(colBtn);


    }

    public static void main(String[] args) {
        launch(args);
    }

    public void handleRefreshedTopics(String message) {
        String[] topics = message.split(" ");
        this.allTopicList.clear();
        for (String s : topics) {
            if (s.startsWith("#TOPICS"))
                continue;
            this.allTopicList.add(new TopicModel(s));
        }


    }

    public void handleNews(String message) {
        log("" + message);

        String[] news = message.split("_");
        log(news[0]);
        log(news[1]);
        log(news[2]);

            this.newsTextArea.appendText("<b>"+news[1]+"</b>\n");
            this.newsTextArea.appendText(news[2]+"\n");
            this.newsTextArea.appendText("--------");

    }
}