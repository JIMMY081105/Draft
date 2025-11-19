package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;
import com.comp2042.util.GameConstants;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
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

        Board board = new SimpleBoard(GameConstants.BOARD_HEIGHT, GameConstants.BOARD_WIDTH);
        
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController guiController = fxmlLoader.getController();
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        
        double titleBarHeightRatio = 0.04;
        double titleBarHeight = screenHeight * titleBarHeightRatio;
        double availableHeight = screenHeight - titleBarHeight;
        
        double idealWidth = 300.0;  
        double idealHeight = 510.0; 
        
        double scale = availableHeight / idealHeight;
        
        if (root instanceof Pane) {
            Pane rootPane = (Pane) root;
            rootPane.setPrefSize(idealWidth, idealHeight);
            rootPane.getStyleClass().add("pane-root");
        }
        
        Scale scaleTransform = new Scale(scale, scale);
        root.getTransforms().add(scaleTransform);
        
        Group scaledRoot = new Group(root);
        scaledRoot.setLayoutX((screenWidth - idealWidth * scale) / 2);
        scaledRoot.setLayoutY(0);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(scaledRoot, screenWidth, screenHeight);
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
