package com.example;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StartPage {
    private StackPane layout;

    public StartPage(Stage primaryStage){
        Text mainTitle = new Text("Tasker");
        mainTitle.getStyleClass().add("maintitle");

        Text subTitle = new Text("Your Best Schedule Tracker");
        subTitle.getStyleClass().add("subtitle");

        Button loadButton = new Button("Load a schedule");
        loadButton.getStyleClass().add("button");

        Button createButton = new Button("Create a schedule");
        createButton.getStyleClass().add("button");
        createButton.setOnAction(e -> {
            CreatePage createPage = new CreatePage(primaryStage);
            primaryStage.setScene(createPage.getScene());
        });

        // HBOX to put buttons in horizintal:   B1      B2
        HBox buttonBox = new HBox(20, loadButton, createButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Vobx to put title and subtitle under each other
        VBox TextBox = new VBox(10, mainTitle, subTitle, buttonBox);
        TextBox.setAlignment(Pos.CENTER);

        layout = new StackPane(TextBox);
        layout.getStyleClass().add("root"); // general style
    }

    public StackPane getLayout(){
        return layout;
    }
}
