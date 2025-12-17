# SortueKotlin Project Documentation

## Overview

SortueKotlin is a color-based tile sorting puzzle game built with Jetpack Compose for Android. The game challenges players to unscramble color gradients by swapping tiles to recreate the original gradient pattern.

**Version**: 2.3
**Package**: `com.akinalpfdn.sortue`
**Build Configuration**: Gradle with Kotlin DSL
**Target SDK**: 36, Min SDK**: 26

## Architecture

### Project Structure
```
app/src/main/java/com/akinalpfdn/sortue/
├── MainActivity.kt              # Main activity entry point
├── SortueApp.kt                 # Root Composable app component
├── models/                      # Data models and enums
│   ├── GameMode.kt              # Game difficulty modes
│   ├── GameModels.kt            # Core game data structures
│   └── GameModels.kt            # Tile, RGBData, and game state
├── ui/
│   ├── components/              # Reusable UI components
│   │   ├── TileView.kt          # Individual tile rendering
│   │   ├── AmbientBackground.kt # Background effects
│   │   └── *Overlay.kt          # Various overlay screens
│   └── views/                   # Main screen composables
│       ├── GameView.kt          # Primary game interface
│       ├── LandingView.kt       # Welcome screen
│       └── ModeSelectionView.kt # Mode selection
├── viewmodels/                  # ViewModels for state management
│   └── GameViewModel.kt         # Core game logic
├── utils/                       # Utility classes
│   ├── AudioManager.kt          # Background music management
│   ├── RateManager.kt           # App rating prompts
│   └── WinMessages.kt           # Victory message generator
└── ui/theme/                    # Material 3 theme
    ├── Color.kt, Theme.kt, Type.kt
```

## Core Components

### Data Models

#### GameModels.kt

**RGBData**
```kotlin
data class RGBData(
    val r: Double,    // Red channel (0.0-1.0)
    val g: Double,    // Green channel (0.0-1.0)
    val b: Double     // Blue channel (0.0-1.0)
) {
    val color: Color              // Compose Color conversion
    fun isSimilar(other: RGBData): Boolean
    fun distance(other: RGBData): Double

    companion object {
        val random: RGBData                    // Random color generator
        fun fromHSB(h: Double, s: Double, b: Double): RGBData
        fun interpolated(x, y, width, height, corners): RGBData
    }
}
```

**Tile**
```kotlin
data class Tile(
    val id: Int,              // Unique identifier
    val correctId: Int,       // Correct position index
    val rgb: RGBData,         // Color data
    val isFixed: Boolean,     // Corner anchor tile
    var currentIdx: Int       // Current position helper
)
```

**GameStatus**
```kotlin
enum class GameStatus {
    MENU, PREVIEW, PLAYING, ANIMATING, WON, GAME_OVER
}
```

#### GameMode.kt
```kotlin
enum class GameMode {
    CASUAL,      // Unlimited time, hints available
    PRECISION,   // Move limits, no hints
    PURE         // No solution preview, no hints
}
```

### GameViewModel

**Core Responsibilities:**
- Game state management with StateFlow
- Tile shuffling and win condition checking
- Timer and statistics tracking
- Save/load game state persistence
- Multi-mode game progression

**Key Properties:**
- `tiles: StateFlow<List<Tile>>` - Current tile arrangement
- `status: StateFlow<GameStatus>` - Current game phase
- `moves: StateFlow<Int>` - Move counter
- `gridDimension: StateFlow<Int>` - Grid size (4-12)
- `gameMode: StateFlow<GameMode>` - Active game mode

**Key Methods:**
- `startNewGame()` - Initializes new puzzle
- `swapTiles(id1, id2)` - Exchanges tile positions
- `useHint()` - Auto-corrects one tile with penalty
- `checkWinCondition()` - Validates puzzle completion
- `calculateMinMoves()` - Determines optimal solution

### UI Components

#### GameView.kt
Main game interface featuring:
- **Drag & Drop**: Touch-based tile swapping with visual feedback
- **Grid Rendering**: LazyVerticalGrid with animated item placement
- **Header**: Level info, statistics, control buttons
- **Interactive Elements**: Solution preview, hints, shuffle
- **Game State**: Real-time status updates and win animations

#### TileView.kt
Individual tile component with:
- **Visual States**: Selected, won, locked, normal
- **Animations**: Scale and position transitions
- **Haptic Feedback**: Vibration on tile locking
- **Icons**: Fixed corner dots, checkmarks for locked tiles
- **Accessibility**: Proper content descriptions

#### Game Mode Features

**CASUAL Mode:**
- Unlimited time and moves
- Hint system with time/move penalties
- Solution preview capability
- Best time/moves tracking
- Adjustable grid sizes (4-12)

**PRECISION Mode:**
- Move limits based on grid complexity
- No hints or solution preview
- Progressive difficulty scaling
- Fixed level progression

**PURE Mode:**
- No assistance features
- No solution preview
- Maximum challenge experience
- All core mechanics enabled

### Utility Systems

#### AudioManager.kt
Singleton background music manager:
```kotlin
class AudioManager private constructor(context: Context) {
    var isMusicEnabled: Boolean    // Persistent setting
    var musicVolume: Float         // Volume control (0.0-1.0)

    fun playBackgroundMusic()
    fun pauseBackgroundMusic()
    fun stopBackgroundMusic()

    companion object {
        fun getInstance(context: Context): AudioManager
    }
}
```

Features:
- Looping background track (`R.raw.soundtrack`)
- Error recovery and robust playback
- Volume persistence
- Context-safe singleton implementation

#### RateManager.kt
App rating and review management:
- Launch count tracking
- Review prompt scheduling
- Landing page display management

#### Color Generation System

**HarmonyProfile System**: Generates aesthetically pleasing gradients
```kotlin
enum class HarmonyProfile {
    SUNSET,    // Warm yellows/oranges to purples
    OCEAN,     // Cool cyan gradient to deep navy
    FOREST,    // Fresh greens to emerald teals
    BERRY,     // Pink to red to purple
    AURORA,    // Green to blue to purple
    CITRUS,    // Yellow to orange to lime
    MIDNIGHT   // Grey to blue to violet
}
```

**Interpolation Algorithm**: Bilinear interpolation for smooth gradients:
```kotlin
fun interpolated(x, y, width, height, corners): RGBData
```

### Game Mechanics

#### Shuffling Algorithm
Deterministic shuffling using seeded random numbers:
1. **Corner Preservation**: Corner tiles remain fixed
2. **Shuffled Center**: Only movable tiles are shuffled
3. **Deterministic**: Same seed produces same shuffle
4. **Solvable Guarantee**: All shuffles are mathematically solvable

#### Win Condition Checking
Optimized validation using permutation analysis:
```kotlin
private fun calculateMinMoves(tiles: List<Tile>): Int {
    // Analyzes permutation cycles
    // Returns minimum moves required to solve
    // Complexity: O(n) where n = tile count
}
```

#### State Persistence
Comprehensive save system:
```kotlin
private data class GameState(
    val tiles: List<Tile>,
    val status: GameStatus,
    val gridDimension: Int,
    val moves: Int,
    val corners: Corners?,
    val minMoves: Int?,
    val moveLimit: Int?,
    val gameMode: GameMode?,
    val elapsedTime: Long?
)
```

Save features:
- **Mode-specific saves**: Separate progress per game mode
- **Auto-save**: State saved on every move
- **Resume capability**: Seamless game continuation
- **Best stats tracking**: Time and move records

## Technical Implementation

### Dependencies

**Core Android & Compose:**
```gradle
implementation("androidx.core:core-ktx")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
implementation("androidx.activity:activity-compose")
implementation(platform("androidx.compose:compose-bom"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
```

**Additional Libraries:**
```gradle
implementation("androidx.compose.foundation:foundation:1.7.5")
implementation("androidx.compose.ui:ui-text-google-fonts:1.7.5")
implementation("com.google.code.gson:gson:2.10.1")
implementation("androidx.compose.material:material-icons-extended:1.7.5")
```

### Performance Optimizations

**Animation System:**
- Staggered tile animations for smooth transitions
- Efficient recomposition with proper state hoisting
- GPU-accelerated animations using Compose's animation APIs

**Memory Management:**
- Efficient StateFlow usage to prevent unnecessary recompositions
- Proper MediaPlayer lifecycle management
- Singleton pattern for audio system

**Rendering:**
- LazyVerticalGrid for efficient grid rendering
- Composable-level optimization with remember callbacks
- Minimal view hierarchy depth

### Code Quality Patterns

**MVVM Architecture:**
- Clear separation between UI and business logic
- Reactive state management with StateFlow
- Testable ViewModel architecture

**Compose Best Practices:**
- State hoisting for predictable UI behavior
- Proper key usage in LazyVerticalGrid
- Efficient recomposition patterns
- Material 3 design system adherence

**Error Handling:**
- Robust audio playback with error recovery
- Graceful state persistence failure handling
- Safe navigation between game states

## Usage Examples

### Basic Game Setup
```kotlin
@Composable
fun GameScreen() {
    val viewModel: GameViewModel = viewModel()
    val tiles by viewModel.tiles.collectAsState()
    val status by viewModel.status.collectAsState()

    GameView(vm = viewModel)
}
```

### Starting New Game
```kotlin
// Start casual game with 5x5 grid
viewModel.startNewGame(dimension = 5, mode = GameMode.CASUAL)

// Resume saved game
viewModel.playOrResumeGame(mode = GameMode.PRECISION, size = 4)
```

### Custom Color Generation
```kotlin
val corners = Corners(
    tl = RGBData.fromHSB(0.1, 0.3, 1.0),    // Light yellow
    tr = RGBData.fromHSB(0.05, 0.8, 1.0),   // Orange
    bl = RGBData.fromHSB(0.8, 0.5, 0.8),    // Purple
    br = RGBData.fromHSB(0.9, 0.9, 0.3)     // Deep magenta
)
```

## Development Notes

### Building the Project
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test
```

### Key Architectural Decisions

1. **Deterministic Gameplay**: Seeded random ensures reproducible puzzles
2. **StateFlow Integration**: Reactive state management with Compose
3. **Mode Separation**: Independent save states per game mode
4. **Accessibility**: Built-in haptic feedback and visual indicators
5. **Performance**: Optimized rendering and animation systems

### Extension Points

The architecture supports easy extension:
- **New Game Modes**: Add to GameMode enum and implement mode-specific logic
- **Color Themes**: Extend HarmonyProfile with new gradient algorithms
- **Difficulty Scaling**: Modify level progression in GameViewModel
- **Sound Effects**: Extend AudioManager with effect playback
- **Analytics**: Add tracking to GameViewModel for user behavior insights

---

This documentation provides a comprehensive overview of the SortueKotlin codebase. For specific implementation details, refer to the individual Kotlin files and their inline documentation.