package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;
import com.comp2042.util.GameConstants;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Board board = new SimpleBoard(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT);
        
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent gameRoot = fxmlLoader.load();
        GuiController guiController = fxmlLoader.getController();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        
        double idealWidth = GameConstants.BOARD_WIDTH * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_X;
        double idealHeight = (GameConstants.BOARD_HEIGHT - GameConstants.HIDDEN_BUFFER_ROWS) * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_Y;
        
        if (gameRoot instanceof Pane) {
            ((Pane) gameRoot).setPrefSize(idealWidth, idealHeight);
            ((Pane) gameRoot).setMaxSize(idealWidth, idealHeight);
            ((Pane) gameRoot).setMinSize(idealWidth, idealHeight);
        }
        
        Pane backgroundRoot = new Pane();
        backgroundRoot.getChildren().add(gameRoot);
        
        double scale = Math.min(screenWidth / idealWidth, screenHeight / idealHeight);
        
        gameRoot.getTransforms().add(new Scale(scale, scale));
        
        double x = (screenWidth - idealWidth * scale) / 2;
        double y = (screenHeight - idealHeight * scale) / 2;
        gameRoot.setLayoutX(x);
        gameRoot.setLayoutY(y);

        primaryStage.setTitle("Tetris");
        Scene scene = new Scene(backgroundRoot, screenWidth, screenHeight);
        backgroundRoot.setStyle("-fx-background-color: black;");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        GameController gameController = new GameController(board);
        guiController.setEventListener(gameController);
        guiController.bind(board);
    }

    public static void main(String[] args) {
        launch(args);
    }
}