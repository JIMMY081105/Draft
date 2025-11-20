package com.comp2042.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class UIScalingUtil {
    
    private UIScalingUtil() {
    }
    
    public static DimensionResult calculateIdealDimensions() {
        double idealWidth = GameConstants.BOARD_WIDTH * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_X;
        double idealHeight = (GameConstants.BOARD_HEIGHT - GameConstants.HIDDEN_BUFFER_ROWS) * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_Y;
        
        return new DimensionResult(idealWidth, idealHeight);
    }
    
    public static DimensionResult getScreenDimensions() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        
        return new DimensionResult(screenWidth, screenHeight);
    }
    
    public static double calculateScaleFactor() {
        DimensionResult ideal = calculateIdealDimensions();
        DimensionResult screen = getScreenDimensions();
        
        double widthScale = screen.getWidth() / ideal.getWidth();
        double heightScale = screen.getHeight() / ideal.getHeight();
        
        double scaleFactor = Math.min(widthScale, heightScale);
        scaleFactor = Math.max(scaleFactor, 1.0);
        
        return scaleFactor;
    }
    
    public static DimensionResult calculateScaledDimensions() {
        DimensionResult ideal = calculateIdealDimensions();
        double scaleFactor = calculateScaleFactor();
        
        double scaledWidth = ideal.getWidth() * scaleFactor;
        double scaledHeight = ideal.getHeight() * scaleFactor;
        
        return new DimensionResult(scaledWidth, scaledHeight);
    }
    
    public static class DimensionResult {
        private final double width;
        private final double height;
        
        public DimensionResult(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        public double getWidth() {
            return width;
        }
        
        public double getHeight() {
            return height;
        }
    }
}

