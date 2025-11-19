# Design Patterns and SOLID Refactoring - Complete Documentation

## Overview
This document details all changes made to implement Design Patterns and fix SOLID violations in the Tetris JavaFX application. The refactoring focuses on Dependency Inversion Principle (DIP), Observer Pattern, and Open/Closed Principle (OCP).

---

## 1. Dependency Inversion Principle (DIP) Fix

### Problem
`GameController` was directly instantiating `SimpleBoard`, violating the Dependency Inversion Principle. High-level modules (Controller) should depend on abstractions (interfaces), not concrete implementations.

### Solution
Applied Dependency Injection pattern - `GameController` now receives a `Board` interface through its constructor.

### Changes in GameController.java

**Before:**
```java
private Board board = new SimpleBoard(GameConstants.BOARD_HEIGHT, GameConstants.BOARD_WIDTH);

public GameController(GuiController c) {
    viewGuiController = c;
    // ...
}
```

**After:**
```java
private final Board board;

public GameController(Board board) {
    this.board = board;
    board.createNewBrick();
}
```

**Key Changes:**
- Removed direct instantiation of `SimpleBoard`
- Constructor now accepts `Board` interface (dependency injection)
- Removed `GuiController` field entirely (decoupled from view)
- Removed all view manipulation calls (`viewGuiController.refreshGameBackground()`, `viewGuiController.gameOver()`)
- Controller now only updates the Model, not the View

**Benefits:**
- Controller depends on abstraction (`Board` interface), not concrete class
- Easy to swap implementations (e.g., for testing with mock boards)
- Controller has no knowledge of view layer
- Follows Dependency Inversion Principle

---

## 2. Observer Pattern Implementation

### Problem
Tight coupling between Controller and View. Controller was directly calling view methods, creating a bidirectional dependency.

### Solution
Implemented Observer Pattern using JavaFX Properties. The Model (SimpleBoard) is now observable, and the View (GuiController) observes it automatically.

### Changes in SimpleBoard.java

**Added JavaFX Properties:**
```java
private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
private final ObjectProperty<int[][]> boardMatrix = new SimpleObjectProperty<>();
```

**Added Property Accessors:**
```java
public BooleanProperty isGameOverProperty() {
    return isGameOver;
}

public ObjectProperty<int[][]> boardMatrixProperty() {
    return boardMatrix;
}

public IntegerProperty scoreProperty() {
    return score.scoreProperty();
}
```

**Updated State Change Methods:**

1. **createNewBrick()** - Now sets `isGameOver` property:
```java
boolean gameOver = MatrixOperations.intersect(...);
if (gameOver) {
    isGameOver.set(true);
}
```

2. **mergeBrickToBackground()** - Notifies observers of board matrix change:
```java
currentGameMatrix = MatrixOperations.merge(...);
boardMatrix.set(currentGameMatrix);
```

3. **clearRows()** - Notifies observers after clearing rows:
```java
currentGameMatrix = clearRow.getNewMatrix();
boardMatrix.set(currentGameMatrix);
```

4. **newGame()** - Resets game over state and notifies observers:
```java
isGameOver.set(false);
createNewBrick();
boardMatrix.set(currentGameMatrix);
```

**Benefits:**
- Model is now observable without knowing about views
- Automatic view updates when model state changes
- Loose coupling - Model doesn't depend on View
- Multiple views can observe the same model

### Changes in Board.java Interface

**Added Property Methods:**
```java
BooleanProperty isGameOverProperty();
ObjectProperty<int[][]> boardMatrixProperty();
IntegerProperty scoreProperty();
```

This ensures any `Board` implementation must provide observable properties.

### Changes in GuiController.java

**Removed:**
- Direct calls to view methods from controller (handled automatically via observers)

**Added bind() Method:**
```java
public void bind(Board board) {
    this.board = board;
    
    ChangeListener<int[][]> boardMatrixListener = (obs, oldVal, newVal) -> {
        if (newVal != null) {
            refreshGameBackground(newVal);
        }
    };
    
    ChangeListener<Boolean> gameOverListener = (obs, oldVal, newVal) -> {
        if (newVal != null && newVal) {
            gameOver();
        } else {
            gameOverPanel.setVisible(false);
            isGameOver.setValue(Boolean.FALSE);
        }
    };
    
    board.boardMatrixProperty().addListener(boardMatrixListener);
    board.isGameOverProperty().addListener(gameOverListener);
    
    if (board.getBoardMatrix() != null && board.getViewData() != null) {
        initGameView(board.getBoardMatrix(), board.getViewData());
    }
}
```

**How It Works:**
1. View receives `Board` instance via `bind()` method
2. View registers listeners on board's properties
3. When model state changes, properties fire change events
4. Listeners automatically update the view
5. No manual refresh calls needed

**Benefits:**
- View automatically updates when model changes
- Controller doesn't need to know about view
- True separation of concerns
- Follows Observer Pattern

---

## 3. Open/Closed Principle (OCP) Fix

### Problem
`getFillColor()` method in `GuiController` used a hard-coded switch statement. Adding new brick colors required modifying the method, violating Open/Closed Principle (open for modification, closed for extension).

### Solution
Created `ColorMapper` utility class using a `Map<Integer, Paint>` lookup. New colors can be added by modifying the map initialization, not the logic.

### New File: ColorMapper.java

```java
public final class ColorMapper {
    private static final Map<Integer, Paint> COLOR_MAP = new HashMap<>();
    
    static {
        COLOR_MAP.put(0, Color.TRANSPARENT);
        COLOR_MAP.put(1, Color.AQUA);
        COLOR_MAP.put(2, Color.BLUEVIOLET);
        COLOR_MAP.put(3, Color.DARKGREEN);
        COLOR_MAP.put(4, Color.YELLOW);
        COLOR_MAP.put(5, Color.RED);
        COLOR_MAP.put(6, Color.BEIGE);
        COLOR_MAP.put(7, Color.BURLYWOOD);
    }
    
    public static Paint getColor(int colorCode) {
        return COLOR_MAP.getOrDefault(colorCode, Color.WHITE);
    }
}
```

### Changes in GuiController.java

**Before:**
```java
private Paint getFillColor(int i) {
    Paint returnPaint;
    switch (i) {
        case 0:
            returnPaint = Color.TRANSPARENT;
            break;
        case 1:
            returnPaint = Color.AQUA;
            break;
        // ... 6 more cases
        default:
            returnPaint = Color.WHITE;
            break;
    }
    return returnPaint;
}
```

**After:**
```java
private void setRectangleData(int color, Rectangle rectangle) {
    rectangle.setFill(ColorMapper.getColor(color));
    rectangle.setArcHeight(GameConstants.BRICK_ARC_SIZE);
    rectangle.setArcWidth(GameConstants.BRICK_ARC_SIZE);
}
```

**Removed:**
- Entire `getFillColor()` method (replaced with `ColorMapper.getColor()`)

**Benefits:**
- Easy to extend - add new colors by adding entries to the map
- No need to modify existing logic
- Data-driven approach (configuration, not code)
- Follows Open/Closed Principle
- More maintainable and testable

---

## 4. Wiring Everything in Main.java

### Problem
Components were tightly coupled and created in wrong order. Controller was creating its own dependencies.

### Solution
`Main.java` now acts as the assembler, creating all components and wiring them together with proper dependency injection.

### Changes in Main.java

**Before:**
```java
FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
Parent root = fxmlLoader.load();
GuiController c = fxmlLoader.getController();

primaryStage.setTitle("TetrisJFX");
Scene scene = new Scene(root, 300, 510);
primaryStage.setScene(scene);
primaryStage.show();
new GameController(c);
```

**After:**
```java
Board board = new SimpleBoard(GameConstants.BOARD_HEIGHT, GameConstants.BOARD_WIDTH);

URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
ResourceBundle resources = null;
FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
Parent root = fxmlLoader.load();
GuiController guiController = fxmlLoader.getController();

primaryStage.setTitle("TetrisJFX");
Scene scene = new Scene(root, 300, 510);
primaryStage.setScene(scene);
primaryStage.show();

GameController gameController = new GameController(board);
guiController.setEventListener(gameController);
guiController.bind(board);
```

**Key Changes:**
1. **Create Model First:** `Board board = new SimpleBoard(...)`
2. **Load View:** Get `GuiController` from FXML loader
3. **Create Controller:** Pass board via dependency injection
4. **Wire View to Controller:** `guiController.setEventListener(gameController)`
5. **Wire View to Model:** `guiController.bind(board)` - sets up Observer pattern

**Benefits:**
- Clear dependency flow: Model → Controller → View
- All wiring happens in one place (Main)
- Easy to understand component relationships
- Follows Dependency Injection pattern

---

## Summary of All File Changes

### GameController.java
- ✅ Removed `SimpleBoard` instantiation
- ✅ Constructor now accepts `Board` interface (Dependency Injection)
- ✅ Removed `GuiController` field
- ✅ Removed all view method calls (`refreshGameBackground()`, `gameOver()`)
- ✅ Controller now only updates Model

### SimpleBoard.java
- ✅ Added `BooleanProperty isGameOver`
- ✅ Added `ObjectProperty<int[][]> boardMatrix`
- ✅ Exposed `IntegerProperty scoreProperty()` (delegates to Score)
- ✅ Updated `createNewBrick()` to set `isGameOver` property
- ✅ Updated `mergeBrickToBackground()` to notify `boardMatrix` property
- ✅ Updated `clearRows()` to notify `boardMatrix` property
- ✅ Updated `newGame()` to reset `isGameOver` and notify `boardMatrix`

### Board.java Interface
- ✅ Added `BooleanProperty isGameOverProperty()`
- ✅ Added `ObjectProperty<int[][]> boardMatrixProperty()`
- ✅ Added `IntegerProperty scoreProperty()`

### GuiController.java
- ✅ Added `bind(Board board)` method
- ✅ Added property change listeners for `boardMatrix` and `isGameOver`
- ✅ Removed `getFillColor()` method
- ✅ Replaced switch statement with `ColorMapper.getColor()`
- ✅ View now observes Model automatically

### Main.java
- ✅ Creates `SimpleBoard` (Model) first
- ✅ Creates `GameController` with board injection
- ✅ Wires view to controller (`setEventListener`)
- ✅ Wires view to model (`bind()` method)

### New File: ColorMapper.java
- ✅ Map-based color lookup
- ✅ Replaces switch statement
- ✅ Easy to extend with new colors

---

## Overall Architecture Changes

### Before Refactoring
```
Main
  └── GameController
        ├── Creates SimpleBoard (violates DIP)
        └── GuiController (tight coupling)
              └── Calls view methods directly
```

### After Refactoring
```
Main (Assembler)
  ├── Creates SimpleBoard (Model)
  ├── Creates GameController (Controller) with Board injection
  └── GuiController (View)
        ├── Receives GameController (for input events)
        └── Binds to Board (observes model properties)
```

### Data Flow

**Input Flow:**
```
User Input → GuiController → GameController → Board (Model)
```

**Update Flow:**
```
Board (Model) → Property Change → GuiController (View) → UI Update
```

---

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- ✅ **Controller**: Only handles input events and delegates to model
- ✅ **View**: Only handles presentation and observes model
- ✅ **Model**: Only manages game state

### Open/Closed Principle (OCP)
- ✅ **ColorMapper**: Open for extension (add colors to map), closed for modification

### Liskov Substitution Principle (LSP)
- ✅ Any `Board` implementation can be substituted (via interface)

### Interface Segregation Principle (ISP)
- ✅ `Board` interface is focused and cohesive

### Dependency Inversion Principle (DIP)
- ✅ **Controller** depends on `Board` interface, not `SimpleBoard` class
- ✅ **View** depends on `Board` interface, not `SimpleBoard` class
- ✅ High-level modules depend on abstractions

---

## Design Patterns Applied

### 1. Dependency Injection
- `GameController` receives `Board` through constructor
- `Main` acts as dependency injector

### 2. Observer Pattern
- `SimpleBoard` (Subject) has observable properties
- `GuiController` (Observer) listens to property changes
- Automatic updates when model state changes

### 3. Strategy Pattern (Implicit)
- `Board` interface allows different board implementations
- Can swap strategies without changing client code

---

## UI/UX Impact

### No Visible Changes
- Gameplay remains identical
- All features work the same
- User experience unchanged

### Internal Improvements
- More responsive (automatic updates via observers)
- Better separation of concerns
- Easier to maintain and extend
- More testable (can mock Board interface)

---

## Testing Benefits

### Before
- Hard to test Controller (creates its own dependencies)
- Hard to test View (tightly coupled to Controller)
- Hard to test Model (no clear interface)

### After
- ✅ Easy to test Controller (inject mock Board)
- ✅ Easy to test View (inject mock Board with test properties)
- ✅ Easy to test Model (clear interface, observable properties)
- ✅ Can test components in isolation

---

## Future Extensibility

### Adding New Brick Colors
1. Add entry to `ColorMapper.COLOR_MAP`
2. No code changes needed

### Adding New Views
1. Create new view class
2. Implement `bind(Board board)` method
3. Register property listeners
4. No changes to Model or Controller needed

### Adding New Board Implementations
1. Implement `Board` interface
2. Provide observable properties
3. Inject into Controller
4. No changes to Controller or View needed

---

## Conclusion

All four refactoring tasks have been completed:

1. ✅ **Dependency Inversion**: Controller uses DI, depends on interface
2. ✅ **Observer Pattern**: Model is observable, View observes automatically
3. ✅ **Open/Closed Principle**: Color mapping is extensible via Map
4. ✅ **Wiring in Main**: All components assembled and wired correctly

The codebase now follows SOLID principles and uses appropriate design patterns, making it more maintainable, testable, and extensible.

