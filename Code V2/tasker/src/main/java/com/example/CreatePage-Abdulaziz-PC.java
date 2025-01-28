package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CreatePage {
    private SplitPane layout;
    private ListView<HBox> taskListView = new ListView<>();
    private GridPane scheduleGrid = new GridPane();
    private TextField taskNameField = new TextField();
    private ComboBox<String> durationBox = new ComboBox<>();
    private ComboBox<String> priorityBox = new ComboBox<>();

    public CreatePage(Stage primaryStage) {
        layout = new SplitPane();
        layout.setDividerPositions(0.3); // Set initial ratio: 30% for left pane, 70% for center pane

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Create a schedule");
        titleLabel.getStyleClass().add("maintitle");

        HBox taskInputBox = new HBox(10);
        taskInputBox.setAlignment(Pos.CENTER);
        taskInputBox.getStyleClass().add("task-input-box");

        taskNameField.setPromptText("Task Name");
        taskNameField.setPrefWidth(150);

        durationBox.getItems().addAll("1 hour", "2 hours");
        durationBox.setPrefWidth(100);

        priorityBox.getItems().addAll("Normal", "Important", "Urgent");
        priorityBox.setPrefWidth(100);

        Button addButton = new Button("Add Task");
        addButton.getStyleClass().add("button");
        addButton.setPrefWidth(100);
        addButton.setOnAction(e -> addTask());

        taskInputBox.getChildren().addAll(taskNameField, durationBox, priorityBox, addButton);

        ScrollPane taskListScrollPane = new ScrollPane(taskListView);
        taskListScrollPane.setFitToWidth(true);
        taskListScrollPane.setPrefHeight(400);

        leftPane.getChildren().addAll(titleLabel, taskInputBox, taskListScrollPane);
        leftPane.setPrefWidth(300); // Set initial preferred width for left pane

        setupScheduleGrid();
        setupDragAndDrop();

        ScrollPane scheduleScrollPane = new ScrollPane(scheduleGrid);
        scheduleScrollPane.setFitToWidth(true);
        scheduleScrollPane.setFitToHeight(true);

        // Add panes to SplitPane
        layout.getItems().addAll(leftPane, scheduleScrollPane);
    }

    public Scene getScene() {
        Scene scene = new Scene(layout, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        return scene;
    }

    private void setupScheduleGrid() {
        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        // Create column constraints for evenly distributing columns
        for (int i = 0; i <= weekdays.length; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / (weekdays.length + 1)); // Equal width for each column
            colConstraints.setFillWidth(true);
            scheduleGrid.getColumnConstraints().add(colConstraints);
        }

        // Create row constraints for evenly distributing rows
        for (int i = 0; i <= 24; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / 25); // Equal height for each row
            rowConstraints.setFillHeight(true);
            scheduleGrid.getRowConstraints().add(rowConstraints);
        }

        // Add weekday headers
        for (int i = 0; i < weekdays.length; i++) {
            Label dayLabel = new Label(weekdays[i]);
            dayLabel.getStyleClass().add("day-label");
            dayLabel.setAlignment(Pos.CENTER);
            scheduleGrid.add(dayLabel, i + 1, 0);
        }

        // Add time labels
        for (int i = 0; i < 24; i++) {
            Label timeLabel = new Label(String.format("%02d:00 - %02d:00", i, (i + 1) % 24));
            timeLabel.getStyleClass().add("time-label");
            timeLabel.setAlignment(Pos.CENTER);
            scheduleGrid.add(timeLabel, 0, i + 1);
        }

        scheduleGrid.getStyleClass().add("schedule-grid");
        scheduleGrid.setPadding(new Insets(10));
        scheduleGrid.setHgap(5);
        scheduleGrid.setVgap(5);
    }

    private void setupDragAndDrop() {
        taskListView.setOnDragDetected(event -> {
            if (taskListView.getSelectionModel().getSelectedItem() == null) return;
            HBox selectedTask = taskListView.getSelectionModel().getSelectedItem();
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedTask.getAccessibleText());
            taskListView.startDragAndDrop(TransferMode.MOVE).setContent(content);
            event.consume();
        });
    
        scheduleGrid.setOnDragOver(event -> {
            if (event.getGestureSource() != scheduleGrid && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
    
        scheduleGrid.setOnDragDropped(event -> {
            if (event.getDragboard().hasString()) {
                String taskDetails = event.getDragboard().getString();
                String durationText = taskDetails.split("\\(")[1].split(",")[0].trim();
                int taskDuration = durationText.equals("1 hour") ? 1 : 2;
    
                double padding = 10; // Padding of the grid
                double gap = 5;      // Gap between cells
                double cellWidth = (scheduleGrid.getWidth() - padding * 2 - gap * 7) / 7;
                double cellHeight = (scheduleGrid.getHeight() - padding * 2 - gap * 24) / 24;
    
                int column = (int) Math.round((event.getX() - padding) / (cellWidth + gap));
                column = Math.max(0, Math.min(column, 6)); // Clamp column index between 0 and 6 (7 days)
    
                int row = (int) Math.round((event.getY() - padding) / (cellHeight + gap)); // Corrected row calculation
    
                // Ensure row and column are within bounds
                if (row >= 1 && row < 25 && column >= 0 && column < 7) {
                    for (int i = 0; i < taskDuration; i++) {
                        if (row + i < 25) {
                            Label taskLabel = new Label(taskDetails);
                            taskLabel.getStyleClass().add("grid-cell");
                            scheduleGrid.add(taskLabel, column + 1, row + i);
                        }
                    }
                    event.setDropCompleted(true);
                }
            }
            event.consume();
        });
    }
    
    

    private void addTask() {
        String taskName = taskNameField.getText();
        String duration = durationBox.getValue();
        String priority = priorityBox.getValue();

        if (taskName.isEmpty() || duration == null || priority == null) {
            showAlert("Please fill in all fields");
            return;
        }

        HBox taskItem = new HBox(10);
        taskItem.setAlignment(Pos.CENTER_LEFT);
        Label taskLabel = new Label(taskName + " (" + duration + ", " + priority + ")");

        switch (priority) {
            case "Important":
                taskLabel.getStyleClass().add("task-important");
                break;
            case "Urgent":
                taskLabel.getStyleClass().add("task-urgent");
                break;
            default:
                taskLabel.getStyleClass().add("task-normal");
                break;
        }

        taskItem.getChildren().add(taskLabel);
        taskItem.setAccessibleText(taskName + " (" + duration + ", " + priority + ")");
        taskListView.getItems().add(taskItem);

        taskNameField.clear();
        durationBox.getSelectionModel().clearSelection();
        priorityBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
