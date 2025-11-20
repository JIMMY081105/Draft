package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;
import com.comp2042.util.GameConstants;
import com.comp2042.util.UIScalingUtil;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main application class that initializes the Tetris game.
 * 
 * Follows MVC architecture:
 * - Model: Board (SimpleBoard)
 * - View: GuiController (loaded from FXML)
 * - Controller: GameController
 */
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

        // 3. Calculate responsive window size with aspect ratio preservation
        // Uses UIScalingUtil to maintain Single Responsibility Principle
        UIScalingUtil.DimensionResult idealDimensions = UIScalingUtil.calculateIdealDimensions();
        double scaleFactor = UIScalingUtil.calculateScaleFactor();
        
        // Use ideal dimensions for the Scene (maintains correct aspect ratio)
        double windowWidth = idealDimensions.getWidth();
        double windowHeight = idealDimensions.getHeight();
        
        // Apply scale transform to root node to scale everything proportionally
        // This ensures the game board scales correctly without distortion
        Scale scale = new Scale(scaleFactor, scaleFactor);
        scale.setPivotX(0);
        scale.setPivotY(0);
        root.getTransforms().add(scale);
        
        // Adjust window size to account for scaling
        double scaledWidth = windowWidth * scaleFactor;
        double scaledHeight = windowHeight * scaleFactor;

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, scaledWidth, scaledHeight);
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