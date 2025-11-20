package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;
import com.comp2042.util.GameConstants;
import com.comp2042.util.UIScalingUtil;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
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
        Parent root = fxmlLoader.load();
        GuiController guiController = fxmlLoader.getController();

        UIScalingUtil.DimensionResult screenDimensions = UIScalingUtil.getScreenDimensions();
        UIScalingUtil.DimensionResult idealDimensions = UIScalingUtil.calculateIdealDimensions();
        double scaleFactor = UIScalingUtil.calculateScaleFactor();
        
        Scale scale = new Scale(scaleFactor, scaleFactor);
        double scaledWidth = idealDimensions.getWidth() * scaleFactor;
        double scaledHeight = idealDimensions.getHeight() * scaleFactor;
        
        scale.setPivotX(scaledWidth / 2.0);
        scale.setPivotY(scaledHeight / 2.0);
        root.getTransforms().add(scale);
        
        double centerX = (screenDimensions.getWidth() - scaledWidth) / 2.0;
        double centerY = (screenDimensions.getHeight() - scaledHeight) / 2.0;
        
        Group scaledRoot = new Group(root);
        scaledRoot.setLayoutX(centerX);
        scaledRoot.setLayoutY(centerY);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(scaledRoot, screenDimensions.getWidth(), screenDimensions.getHeight());
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