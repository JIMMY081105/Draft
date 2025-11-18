# Tetris Project - Basic Maintenance Refactoring Summary

## Overview
This document summarizes the three basic maintenance tasks performed on the Tetris project:
1. Meaningful Package Organisation
2. Basic Maintenance: Remove Magic Numbers
3. Basic Maintenance: Remove Duplicated Code

## 1. Meaningful Package Organisation

### New Package Structure
All Java files have been reorganized from the root `com.comp2042` package into a clean Model-View-Controller (MVC) structure:

- **com.comp2042.model** - Game logic and data models
  - Contains: `Board`, `SimpleBoard`, `Score`, `BrickRotator`
  - Sub-package: `com.comp2042.model.brick` - All brick-related classes
  
- **com.comp2042.view** - UI components
  - Contains: `GuiController`, `GameOverPanel`, `NotificationPanel`
  
- **com.comp2042.controller** - Input handling and coordination
  - Contains: `GameController`
  
- **com.comp2042.data** - Data Transfer Objects (DTOs)
  - Contains: `ClearRow`, `DownData`, `ViewData`
  
- **com.comp2042.util** - Utility classes
  - Contains: `MatrixOperations`, `GameConstants`
  
- **com.comp2042.event** - Event handling
  - Contains: `EventSource`, `EventType`, `MoveEvent`, `InputEventListener`

### Package Migration Details

**Model Package (com.comp2042.model):**
- `com.comp2042.logic.Board` → `com.comp2042.model.Board`
- `com.comp2042.logic.SimpleBoard` → `com.comp2042.model.SimpleBoard`
- `com.comp2042.logic.Score` → `com.comp2042.model.Score`
- `com.comp2042.logic.BrickRotator` → `com.comp2042.model.BrickRotator`

**Model.Brick Sub-package (com.comp2042.model.brick):**
- `com.comp2042.brick.*` → `com.comp2042.model.brick.*`
  - All 7 brick classes (IBrick, JBrick, LBrick, OBrick, SBrick, TBrick, ZBrick)
  - `Brick`, `BrickGenerator`, `RandomBrickGenerator`, `NextShapeInfo`

**View Package (com.comp2042.view):**
- `com.comp2042.controller.GuiController` → `com.comp2042.view.GuiController`
- `com.comp2042.ui.GameOverPanel` → `com.comp2042.view.GameOverPanel`
- `com.comp2042.ui.NotificationPanel` → `com.comp2042.view.NotificationPanel`

**Controller Package (com.comp2042.controller):**
- `com.comp2042.controller.GameController` → `com.comp2042.controller.GameController` (stays in controller)

**Data Package (com.comp2042.data):**
- `com.comp2042.dto.ClearRow` → `com.comp2042.data.ClearRow`
- `com.comp2042.dto.DownData` → `com.comp2042.data.DownData`
- `com.comp2042.dto.ViewData` → `com.comp2042.data.ViewData`

**Util Package (com.comp2042.util):**
- `com.comp2042.logic.MatrixOperations` → `com.comp2042.util.MatrixOperations`
- New: `com.comp2042.util.GameConstants`

**Event Package (com.comp2042.event):**
- All event classes remain in `com.comp2042.event` (no change)

**Main Class:**
- `com.comp2042.Main` remains in root package (as required)

### Import Statement Updates
All import statements in every file have been updated to reflect the new package structure.

## 2. Basic Maintenance: Remove Magic Numbers

### New File Created
**com.comp2042.util.GameConstants.java**
- Contains all named constants replacing magic numbers throughout the project

### Constants Defined
- `BOARD_HEIGHT = 25` - Board height dimension
- `BOARD_WIDTH = 10` - Board width dimension
- `GAME_TICK_MS = 400` - Game animation tick duration in milliseconds
- `BRICK_SIZE = 20` - Size of each brick cell in pixels
- `BRICK_PANEL_Y_OFFSET = -42` - Vertical offset for brick panel
- `SCORE_PER_LINE = 50` - Base score multiplier per cleared line
- `SPAWN_X = 4` - X coordinate for brick spawn position
- `SPAWN_Y = 0` - Y coordinate for brick spawn position
- `HIDDEN_BUFFER_ROWS = 2` - Number of hidden buffer rows at top of board
- `BRICK_ARC_SIZE = 9` - Arc size for rounded brick rectangles
- `MANUAL_DOWN_SCORE = 1` - Score points for manual down movement

### Files Modified with Constants

**SimpleBoard.java:**
- `new Point(4, 0)` → `new Point(GameConstants.SPAWN_X, GameConstants.SPAWN_Y)`

**GameController.java:**
- `new SimpleBoard(25, 10)` → `new SimpleBoard(GameConstants.BOARD_HEIGHT, GameConstants.BOARD_WIDTH)`
- `board.getScore().add(1)` → `board.getScore().add(GameConstants.MANUAL_DOWN_SCORE)`

**GuiController.java:**
- `BRICK_SIZE = 20` (removed local constant) → `GameConstants.BRICK_SIZE`
- `Duration.millis(400)` → `Duration.millis(GameConstants.GAME_TICK_MS)`
- `-42` → `GameConstants.BRICK_PANEL_Y_OFFSET`
- `2` (hidden buffer rows) → `GameConstants.HIDDEN_BUFFER_ROWS`
- `9` (arc size) → `GameConstants.BRICK_ARC_SIZE`

**MatrixOperations.java:**
- `50 * clearedRows.size() * clearedRows.size()` → `GameConstants.SCORE_PER_LINE * clearedRows.size() * clearedRows.size()`

## 3. Basic Maintenance: Remove Duplicated Code

### Extract Method Refactoring in SimpleBoard.java

**Before:** Three nearly identical methods:
- `moveBrickDown()` - moved brick down (0, 1)
- `moveBrickLeft()` - moved brick left (-1, 0)
- `moveBrickRight()` - moved brick right (1, 0)

**After:** Single generic helper method:
- `tryMove(int xOffset, int yOffset)` - handles all movement logic
- All three public methods now call `tryMove()` with appropriate offsets:
  - `moveBrickDown()` → `tryMove(0, 1)`
  - `moveBrickLeft()` → `tryMove(-1, 0)`
  - `moveBrickRight()` → `tryMove(1, 0)`

**Benefits:**
- Eliminated code duplication
- Single source of truth for movement logic
- Easier to maintain and modify movement behavior

### Extract Superclass Refactoring for Brick Classes

**New File Created:**
**com.comp2042.model.brick.AbstractBrick.java**
- Abstract base class containing common brick functionality
- Contains protected `brickMatrix` field (List<int[][]>)
- Contains `getShapeMatrix()` method implementation
- Defines abstract `initializeShapes()` method for subclasses

**Before:** All 7 brick classes had identical structure:
- Each had `private final List<int[][]> brickMatrix = new ArrayList<>()`
- Each had identical `getShapeMatrix()` method returning `MatrixOperations.deepCopyList(brickMatrix)`
- Only difference was the shape data in constructors

**After:** All 7 brick classes now extend `AbstractBrick`:
- `IBrick extends AbstractBrick`
- `JBrick extends AbstractBrick`
- `LBrick extends AbstractBrick`
- `OBrick extends AbstractBrick`
- `SBrick extends AbstractBrick`
- `TBrick extends AbstractBrick`
- `ZBrick extends AbstractBrick`

**Changes in Brick Classes:**
- Removed `brickMatrix` field declaration (inherited from AbstractBrick)
- Removed `getShapeMatrix()` method (inherited from AbstractBrick)
- Constructor replaced with `initializeShapes()` method
- Shape data initialization moved to `initializeShapes()` method

**Benefits:**
- Eliminated 7x code duplication
- Common functionality centralized in base class
- Easier to add new brick types
- Consistent behavior across all brick classes

## Files Summary

### New Files Created (2)
1. `com.comp2042.util.GameConstants.java` - Constants file
2. `com.comp2042.model.brick.AbstractBrick.java` - Abstract base class for bricks

### Files Moved/Refactored (29)
- All files moved to new package structure
- All imports updated
- Magic numbers replaced with constants
- Duplicated code eliminated

### Files Unchanged
- `com.comp2042.Main.java` - Remains in root package (as required)
- FXML file updated with new package references

## Testing Recommendations
After this refactoring, verify:
1. All imports resolve correctly
2. Game functionality remains unchanged
3. Constants are used consistently
4. Movement logic works correctly (tested via tryMove method)
5. All brick types function correctly (tested via AbstractBrick inheritance)

