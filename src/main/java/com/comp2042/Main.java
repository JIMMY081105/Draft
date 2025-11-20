package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;
import com.comp2042.util.GameConstants;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // 1. Initialize Model
        Board board = new SimpleBoard(GameConstants.BOARD_WIDTH, GameConstants.BOARD_HEIGHT);
        
        // 2. Load View
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController guiController = fxmlLoader.getController();

        // 3. Calculate Window Size dynamically
        // This ensures the window grows when you change BRICK_SIZE in GameConstants
        int windowWidth = GameConstants.BOARD_WIDTH * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_X;
        int windowHeight = (GameConstants.BOARD_HEIGHT - GameConstants.HIDDEN_BUFFER_ROWS) * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_Y;

        primaryStage.setTitle("TetrisJFX");
        // Use the calculated sizes instead of 300, 510
        Scene scene = new Scene(root, windowWidth, windowHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // 4. Wire Controller
        GameController gameController = new GameController(board);
        guiController.setEventListener(gameController);
        guiController.bind(board);
    }

    public static void main(String[] args) {
        launch(args);
    }
}