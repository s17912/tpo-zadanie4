package zad1.admin;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import zad1.utils.BaseGUI;
import zad1.utils.Service;
import zad1.utils.TopicModel;

import java.io.IOException;

import static zad1.utils.Config.log;


public class GUIAdmin extends BaseGUI {
    TextField topicTextField = new TextField();
    Button addNewTopicButton = new Button("Add new topic");
    Stage createTopicWindow;
    AdminService service;

    ComboBox<TopicModel> topicSelectionCombo = new ComboBox<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        initialize();

        topicSelectionCombo.setItems(allTopicList);


        String APP_TITLE = "Admin Application";
        Label label = new Label(APP_TITLE);
        label.setFont(Font.font("Cambria", 32));
        borderPane.setTop(label);
        label.setPadding(new Insets(10, 10, 10, 10));


        Button createTopicButton = new Button("Create new topic");


        Button createNewsButton = new Button("Create news");
        topicsPane.getChildren().add(createTopicButton);
        newsPane.getChildren().add(createNewsButton);


        createTopicButton.setOnAction(s -> {
            GridPane gridPane = new GridPane();
            createTopicWindow = new Stage();
            createTopicWindow.setTitle("Topic");
            Scene scene = new Scene(gridPane);
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(25, 25, 25, 25));
            gridPane.add(new Label("Topic: "), 0, 0);

            topicTextField = new TextField();
            gridPane.add(topicTextField, 1, 0);

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(addNewTopicButton);
            gridPane.add(hbox, 1, 4);

            createTopicWindow.setScene(scene);
            createTopicWindow.setX(primaryStage.getX() + 200);
            createTopicWindow.setY(primaryStage.getY() + 100);
            createTopicWindow.show();
        });
        addButtonToTable();

        reloadTopicsButton.setOnAction(s -> {
            try {
                this.service.refreshTopics();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        reloadNewsButton.setOnAction(s -> {
            //TODO: complete handler or remove button
        });

        addNewTopicButton.setOnAction(s -> {
            String topic = this.topicTextField.getText();
            try {
                this.service.registerTopic(topic);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.createTopicWindow.close();
            this.topicTextField.setText("");
            this.reloadTopicsButton.fire();
        });


        createNewsButton.setOnAction(s -> {

            GridPane gridPane = new GridPane();
            Stage createNewsWindow = new Stage();
            createNewsWindow.setTitle("News");
            Scene scene = new Scene(gridPane);

            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(25, 25, 25, 25));

            gridPane.add(new Label("Topic: "), 0, 0);

            gridPane.add(topicSelectionCombo, 1, 0);
            gridPane.add(new Label("News: "), 0, 1);
            TextArea newsTextArea = new TextArea();
            gridPane.add(newsTextArea, 1, 1);

            Button calculateButton = new Button("Add news");
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(calculateButton);
            calculateButton.setOnAction(e->{
                try {
                    service.publishNews(topicSelectionCombo.getValue().toString(), newsTextArea.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            gridPane.add(hbox, 1, 4);

            createNewsWindow.setScene(scene);
            createNewsWindow.setX(primaryStage.getX() + 200);
            createNewsWindow.setY(primaryStage.getY() + 100);
            createNewsWindow.show();

        });
        
        primaryStage.show();

    }




    public void register(ActionEvent actionEvent) throws IOException {
        log("ccç~register " + this.topicTextField.getText());
        this.service.registerTopic(this.topicTextField.getText());
    }


    public void initialize() {
        log("Admin GUIClient initialized");
        this.service = new AdminService(this);
        startAdminClient(this.service);
    }

    private void startAdminClient(Service service) {
        Runnable runnable = () -> {
            try {
                service.startClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread clientServerThread = new Thread(runnable);
        clientServerThread.start();
    }

    public void setMessageForMessageFiled(String msg) {

        //log(msg);
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected void addButtonToTable() {
        Callback<TableColumn<TopicModel, Void>, TableCell<TopicModel, Void>> cellFactory = new Callback<TableColumn<TopicModel, Void>, TableCell<TopicModel, Void>>() {
            public TableCell<TopicModel, Void> call(final TableColumn<TopicModel, Void> param) {
                final TableCell<TopicModel, Void> cell = new TableCell<TopicModel, Void>() {

                    private final Button btn = new Button("Remove");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            TopicModel data = getTableView().getItems().get(getIndex());
                            try {
                                service.deregisterTopic(data.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            reloadTopicsButton.fire();
                            System.out.println("selectedData: " + data);
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

}