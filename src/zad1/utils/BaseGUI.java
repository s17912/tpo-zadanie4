package zad1.utils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class BaseGUI extends Application {

    final int SCENE_WIDTH = 1100;
    final int SCENE_HEIGHT = 500;
    protected BorderPane borderPane = new BorderPane();
    protected TableView<TopicModel> topicsView = new TableView<>();
    protected VBox topicsPane = new VBox();
    protected VBox newsPane = new VBox();
    protected Scene scene;
    protected TableColumn<TopicModel, Void> colBtn = new TableColumn<>("Button Column");
    public ObservableList<TopicModel> allTopicList = FXCollections.observableArrayList();
    public Button reloadTopicsButton = new Button("Reload Topics");
    public Button reloadNewsButton = new Button("Reload News");
    protected TextArea newsTextArea = new TextArea();
    @Override
    public void start(Stage primaryStage) throws Exception {


        borderPane.setLeft(topicsPane);
        borderPane.setRight(newsPane);
        borderPane.setPadding(new Insets(25));

        //  borderPane.getTop().prefWidth(300);


        TableColumn<TopicModel,String> titleCol = new TableColumn<>("Topic");
        titleCol.setMinWidth((topicsView.getWidth()) / 100 * 70);
        titleCol.setCellValueFactory(new PropertyValueFactory<>("name"));


        topicsView.getColumns().add(titleCol);
        topicsView.setPrefWidth(450);
        topicsView.setPrefHeight(300);
        topicsView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        topicsView.setItems(this.allTopicList);
        topicsPane.setSpacing(10);
        topicsPane.setPadding(new Insets(0, 10, 10, 10));
        topicsPane.getChildren().addAll(reloadTopicsButton, topicsView);

        newsTextArea.setStyle("-fx-font-family: monospace");
        newsTextArea.setPrefHeight(topicsView.getPrefHeight());
        newsPane.getChildren().addAll(reloadNewsButton, newsTextArea);
        newsPane.setSpacing(10);

        scene = new Scene(new Group(borderPane), SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setScene(scene);


        primaryStage.setResizable(true);

    }




}
