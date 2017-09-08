package com.fgl.sudoku;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * This class sets up main UI, which provides option
 * for selecting four pre-defined sudoku puzzles or
 * entering your own puzzles, and option for selecting
 * backtracking strategy. It also provides option
 * for solving the puzzle, during which child thread
 * will be spawned and result will be returned.
 */

/**
 * Library requirement: JDK8
 */
public class SudokuApplication extends Application{

    private static final Logger LOGGER = Logger.getLogger(SudokuApplication.class.getName());
    private static Properties properties = new Properties();
    private static SudokuViewCtrl stateViewCtrl;
    private static Label label;

    private static AppComponent appComponent;

    // mutex for recording if there's task running
    private static AtomicBoolean solving = new AtomicBoolean();
    // task for solving sudoku
    private static Task task = null;
    private static BKStrategy selectedStrategy;
    private static Level selectedLevel = Level.Blank;

    /**
     * Load sudoku puzzles stored in local file
     */
    @Override
    public void
    init() {
        try(InputStream inputStream = SudokuApplication.class.getResourceAsStream("/application.properties")){
            properties.load(inputStream);
        } catch (IOException e) {
             LOGGER.warning("File not found: application.properties." );
        }
    }

    /**
     * Start the application UI
     */
    @Override
    public void
    start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Simple Sudoku App");
        BorderPane root = new BorderPane();
        stateViewCtrl = new SudokuViewCtrl(root);
        createMenu(primaryStage, root);
        label = createLabel(root);
        primaryStage.setScene(new Scene(root, 600, 500, Color.WHITE));
        primaryStage.show();
    }

    public static void
    main(String[] args) {
        launch(args);
    }

    private void
    createMenu(Stage stage, BorderPane root){
        MenuBar menuBar = new MenuBar();
        root.setTop(menuBar);
        // puzzle menu
        Menu menuPuzzle = new Menu("_Puzzle");
        menuPuzzle.setMnemonicParsing(true);
        ToggleGroup toggleLevel = new ToggleGroup();
        for (Level level: Level.values()){
            RadioMenuItem item = new RadioMenuItem(level.toString());
            item.setUserData(level);
            item.setToggleGroup(toggleLevel);
            menuPuzzle.getItems().add(item);
        }
        MenuItem menuItemExit = new MenuItem("Exit");
        menuItemExit.setOnAction(event -> Platform.exit());
        menuPuzzle.getItems().addAll(new SeparatorMenuItem(), menuItemExit);
        menuBar.getMenus().add(menuPuzzle);
        toggleLevel.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleLevel.getSelectedToggle() != null){
                selectedLevel = (Level) toggleLevel.getSelectedToggle().getUserData();
                stateViewCtrl.setBoard(properties.getProperty(selectedLevel.toString()));
            }
        });

        // backtracking strategy
        Menu menuBK = new Menu("BT Strategy");
        ToggleGroup toggleStrategy = new ToggleGroup();
        for (int i = 0; i < BKStrategy.values().length; i ++){
            BKStrategy strategy = BKStrategy.values()[i];
            RadioMenuItem item = new RadioMenuItem(strategy.toString());
            if(i == 0){
                item.setSelected(true);
                selectedStrategy = strategy;
            }
            item.setUserData(strategy);
            item.setToggleGroup(toggleStrategy);
            menuBK.getItems().add(item);
        }
        menuBar.getMenus().add(menuBK);
        toggleStrategy.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleStrategy.getSelectedToggle() != null){
                selectedStrategy = (BKStrategy) toggleStrategy.getSelectedToggle().getUserData();
            }
        });

        // solve menu
        Menu menuSolve = new Menu("Solve");
        MenuItem menuItemSolve = new MenuItem("Solve");
        MenuItem menuItemCancel = new MenuItem("Cancel");
        menuItemSolve.setOnAction(this::handleSolveMenuItemAction);
        menuItemCancel.setOnAction(this::handleCancelMenuItemAction);
        menuSolve.getItems().addAll(menuItemSolve, menuItemCancel);
        menuBar.getMenus().add(menuSolve);

        // instruction menu
        Label label = new Label("Instruction");
        Menu menuInstruction = new Menu();
        menuInstruction.setGraphic(label);
        label.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Instruction");
            alert.setHeaderText(null);
            alert.setContentText(properties.getProperty("Instruction"));
            alert.showAndWait();
        });
        menuBar.getMenus().add(menuInstruction);
    }

    private Label
    createLabel(BorderPane root){
        FlowPane flowPane = new FlowPane();
        root.setBottom(flowPane);
        Label label = new Label("");
        flowPane.getChildren().add(label);
        flowPane.maxWidthProperty().bind(root.widthProperty().subtract(20));
        flowPane.maxHeightProperty().bind(root.heightProperty().subtract(10));

        flowPane.setPadding(new Insets(10,20,10,20));
        flowPane.setPrefHeight(10);
        flowPane.setAlignment(Pos.CENTER_RIGHT);
        return label;
    }

    private void
    handleSolveMenuItemAction(ActionEvent event){
        if(!solving.getAndSet(true)){
            String board = stateViewCtrl.getBoard();
            task = new SudokuSolver(board, solving, selectedStrategy);
            label.textProperty().bind(task.messageProperty());
            task.setOnSucceeded(succeedEvent -> {
                label.textProperty().unbind();
                ((Optional)task.getValue()).ifPresent(o -> {
                    stateViewCtrl.setBoard(((Optional)task.getValue()).get().toString());
                });
            });
            new Thread(task).start();
        }
    }

    private void
    handleCancelMenuItemAction(ActionEvent event){
        if(task != null && solving.get()){
            task.cancel(true);
            label.textProperty().unbind();
            label.setText("Task Cancelled.");
        }
    }

    /**
     * initialize injector
     */
    public static AppComponent getAppComponent() {
        if(appComponent == null){
            appComponent = DaggerAppComponent.builder().appModule(new AppModule()).build();
        }
        return appComponent;
    }
}
