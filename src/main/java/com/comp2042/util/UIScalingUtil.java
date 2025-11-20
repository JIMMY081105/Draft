package com.comp2042.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * Utility class for calculating responsive UI scaling that fits any device screen
 * while maintaining the correct aspect ratio.
 * 
 * Follows Single Responsibility Principle: This class is solely responsible for
 * UI dimension calculations and scaling logic.
 */
public final class UIScalingUtil {
    
    private UIScalingUtil() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Calculates the ideal window dimensions based on game constants.
     * 
     * @return a DimensionResult containing ideal width and height
     */
    public static DimensionResult calculateIdealDimensions() {
        double idealWidth = GameConstants.BOARD_WIDTH * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_X;
        double idealHeight = (GameConstants.BOARD_HEIGHT - GameConstants.HIDDEN_BUFFER_ROWS) * GameConstants.BRICK_SIZE + GameConstants.WINDOW_PADDING_Y;
        
        return new DimensionResult(idealWidth, idealHeight);
    }
    
    /**
     * Gets the primary screen dimensions.
     * 
     * @return a DimensionResult containing screen width and height
     */
    public static DimensionResult getScreenDimensions() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        
        return new DimensionResult(screenWidth, screenHeight);
    }
    
    /**
     * Calculates the optimal scale factor that fits the screen while maintaining
     * the correct aspect ratio. This prevents horizontal distortion.
     * 
     * @return the scale factor to apply (1.0 = no scaling, < 1.0 = scale down, > 1.0 = scale up)
     */
    public static double calculateScaleFactor() {
        DimensionResult ideal = calculateIdealDimensions();
        DimensionResult screen = getScreenDimensions();
        
        // Calculate scale factors for both dimensions
        double widthScale = screen.getWidth() / ideal.getWidth();
        double heightScale = screen.getHeight() / ideal.getHeight();
        
        // Use the smaller scale factor to ensure the window fits on screen
        // while maintaining the correct aspect ratio (prevents distortion)
        double scaleFactor = Math.min(widthScale, heightScale);
        
        // Apply a maximum scale limit (e.g., don't scale beyond 1.0 to prevent oversized windows)
        // and a minimum scale to ensure readability
        scaleFactor = Math.min(scaleFactor, 1.0);
        scaleFactor = Math.max(scaleFactor, 0.5);
        
        return scaleFactor;
    }
    
    /**
     * Calculates the optimal window dimensions that fit the screen while maintaining
     * the correct aspect ratio. This prevents horizontal distortion.
     * 
     * @return a DimensionResult containing the scaled width and height
     */
    public static DimensionResult calculateScaledDimensions() {
        DimensionResult ideal = calculateIdealDimensions();
        double scaleFactor = calculateScaleFactor();
        
        // Calculate final dimensions maintaining aspect ratio
        double scaledWidth = ideal.getWidth() * scaleFactor;
        double scaledHeight = ideal.getHeight() * scaleFactor;
        
        return new DimensionResult(scaledWidth, scaledHeight);
    }
    
    /**
     * Immutable data class to hold width and height dimensions.
     */
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

