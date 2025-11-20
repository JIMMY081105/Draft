package com.comp2042.util;

public final class GameConstants {
    
    public static final int BOARD_HEIGHT = 25;
    public static final int BOARD_WIDTH = 10;
    public static final int GAME_TICK_MS = 400;
    
    // CHANGE 1: Increased Size (Try 30 or 40)
    public static final int BRICK_SIZE = 30; 
    
    // CHANGE 2: Adjusted Offset (Must match approx -2.1 * BRICK_SIZE)
    public static final int BRICK_PANEL_Y_OFFSET = -63; 
    
    // CHANGE 3: Adjusted Arc (Approx half of BRICK_SIZE)
    public static final int BRICK_ARC_SIZE = 12; 
    
    // CHANGE 4: Added Padding for Main.java calculation
    public static final int WINDOW_PADDING_X = 100;
    public static final int WINDOW_PADDING_Y = 100;
    
    public static final int SCORE_PER_LINE = 50;
    public static final int SPAWN_X = 4;
    public static final int SPAWN_Y = 0;
    public static final int HIDDEN_BUFFER_ROWS = 2;
    public static final int MANUAL_DOWN_SCORE = 1;
    
    private GameConstants() {}
}