# SortueKotlin

A beautiful and challenging color gradient puzzle game built with Jetpack Compose for Android. Sort scrambled tiles to recreate the original gradient pattern.

![Android Game](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple)
![API](https://img.shields.io/badge/API-26%2B-brightgreen)

## 🎮 Game Overview

Sortue is a relaxing yet challenging puzzle game where you unscramble beautiful color gradients. The game features:

- **Color Gradient Puzzles**: Sort tiles to recreate smooth color transitions
- **Multiple Game Modes**: Casual, Precision, and Pure modes for different play styles
- **Progressive Difficulty**: Grids from 4x4 to 12x12 with increasing complexity
- **Beautiful Visuals**: Aesthetic color themes with smooth animations
- **Haptic Feedback**: Immersive touch interactions
- **Relaxing Soundtrack**: Ambient background music

## 🏗️ Architecture

Built with modern Android development practices:

- **MVVM Architecture**: Clean separation of UI and business logic
- **Jetpack Compose**: Declarative UI with Material 3 design
- **Kotlin Coroutines**: Asynchronous operations and state management
- **StateFlow Integration**: Reactive state management
- **Singleton Pattern**: Efficient resource management

### Key Components

- **GameViewModel**: Core game logic and state management
- **RGBData System**: Mathematical color interpolation
- **Deterministic Shuffling**: Algorithmic puzzle generation
- **Persistent Save System**: Multi-mode game state management

## 🎯 Game Modes

### Casual Mode 🎨
- Unlimited time and moves
- Hint system with penalties
- Adjustable grid sizes (4-12)
- Solution preview capability
- Best time/moves tracking

### Precision Mode ⚡
- Move limits based on grid complexity
- No hints or solution preview
- Progressive difficulty scaling
- Fixed level progression
- Strategic gameplay

### Pure Mode 🔥
- No assistance features
- No solution preview
- Maximum challenge experience
- All core mechanics enabled
- True puzzle mastery

## 🧩 How to Play

1. **Preview**: Memorize the gradient pattern (2.5 seconds)
2. **Sort**: Swap tiles by tapping or dragging
3. **Complete**: Restore the original gradient to win
4. **Progress**: Unlock new levels and challenges

### Controls
- **Tap**: Select and swap tiles
- **Drag**: Direct tile repositioning
- **Hint Button**: Auto-correct one tile (Casual mode only)
- **Preview Button**: Briefly show solution (except Pure mode)
- **Shuffle Button**: Randomize current layout (Casual mode only)

## 🎨 Color System

The game features a sophisticated color generation system with harmonious gradient themes:

- **Sunset**: Warm yellows/oranges to deep purples
- **Ocean**: Cool cyan gradients to deep navy
- **Forest**: Fresh greens to emerald teals
- **Berry**: Pink to red to purple variations
- **Aurora**: Green to blue to purple effects
- **Citrus**: Yellow to orange to lime blends
- **Midnight**: Grey to blue to violet transitions

Each gradient uses bilinear interpolation for smooth, mathematically precise color transitions.

## 🛠️ Technical Features

### Game Mechanics
- **Deterministic Algorithm**: Seeded random ensures reproducible puzzles
- **Optimal Solution Calculation**: Mathematical permutation analysis
- **Win Condition Validation**: Efficient grid state checking
- **Progressive Difficulty**: Adaptive complexity scaling

### Performance
- **Optimized Rendering**: LazyVerticalGrid with efficient animations
- **Memory Management**: Proper StateFlow usage and resource cleanup
- **Smooth Animations**: GPU-accelerated transitions and effects
- **Responsive UI**: 60fps gameplay with minimal recompositions

### State Persistence
- **Multi-Mode Saves**: Separate progress per game mode
- **Auto-Save System**: State preserved on every move
- **Seamless Resume**: Continue games exactly where you left off
- **Statistics Tracking**: Best times and moves records

## 📱 Requirements

- **Android 5.0 (API 26)** or higher
- **RAM**: 2GB recommended for larger grids
- **Storage**: ~50MB including soundtrack
- **Permissions**: None required

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Kotlin 1.9+
- Android Gradle Plugin 8.0+

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/akinalpfdan/SortueKotlin.git
   cd SortueKotlin
   ```

2. **Open in Android Studio**
   - Open the project directory in Android Studio
   - Wait for Gradle synchronization

3. **Build the project**
   ```bash
   # Debug build
   ./gradlew assembleDebug

   # Release build
   ./gradlew assembleRelease
   ```

4. **Run tests**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

5. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

### Dependencies

Key dependencies include:

```kotlin
// Core Android & Compose
implementation("androidx.core:core-ktx")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
implementation("androidx.activity:activity-compose")
implementation(platform("androidx.compose:compose-bom"))

// UI Components
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.foundation:foundation")

// Additional Libraries
implementation("com.google.code.gson:gson:2.10.1")
implementation("androidx.compose.material:material-icons-extended")
```

## 📁 Project Structure

```
app/src/main/java/com/akinalpfdn/sortue/
├── MainActivity.kt              # Main activity
├── SortueApp.kt                 # Root composable
├── models/                      # Data models
│   ├── GameMode.kt              # Game modes enum
│   └── GameModels.kt            # Core data structures
├── ui/
│   ├── components/              # Reusable UI components
│   └── views/                   # Main screen composables
├── viewmodels/                  # State management
│   └── GameViewModel.kt         # Core game logic
├── utils/                       # Utility classes
└── ui/theme/                    # Material 3 theme
```

## 🎮 Controls

### Touch Interactions
- **Single Tap**: Select tile for swapping
- **Double Tap**: Select and swap with previous
- **Drag & Drop**: Direct tile repositioning
- **Long Press**: Trigger haptic feedback

### UI Buttons
- **Settings**: Access game preferences
- **Hint (Casual)**: Auto-correct one tile (+5 moves, +30s time)
- **Preview**: Briefly show solution pattern (2 seconds)
- **Shuffle (Casual)**: Randomize current layout
- **Grid Size Slider**: Adjust puzzle complexity (4-12)

## 🔧 Configuration

### Game Settings
- **Haptics**: Enable/disable vibration feedback
- **Music**: Background music on/off toggle
- **Volume**: Adjustable music volume

### Difficulty Scaling
- **Grid Size**: 4x4 to 12x12 (adjustable in Casual mode)
- **Move Limits**: Calculated based on grid complexity (Precision mode)
- **Time Pressure**: Unlimited in Casual, limited in Precision

## 📊 Statistics Tracking

The game tracks comprehensive statistics:

- **Completion Time**: Fastest solving times per grid size
- **Move Efficiency**: Minimum moves achieved
- **Level Progression**: Completed levels per mode
- **Win Rate**: Success percentage per mode

## 🎯 Development Notes

### Game Algorithm
The shuffling algorithm ensures every puzzle is solvable:
1. **Corner Preservation**: Four corner tiles remain fixed
2. **Permutation Shuffling**: Only movable tiles are shuffled
3. **Deterministic Seeds**: Same level generates identical puzzles
4. **Mathematical Validation**: All shuffles are mathematically solvable

### Color Generation
Each gradient uses the RGBData system:
- **HSB Conversion**: Hue, Saturation, Brightness calculations
- **Bilinear Interpolation**: Smooth color transitions
- **Harmony Profiles**: Aesthetically pleasing color schemes
- **Precise Math**: Double-precision color calculations

### Performance Optimizations
- **Lazy Rendering**: Efficient grid composition
- **State Hoisting**: Minimal recomposition scope
- **Animation Efficiency**: GPU-accelerated transitions
- **Memory Management**: Proper resource cleanup

## 🐛 Troubleshooting

### Common Issues

**Performance Problems on Large Grids**
- Reduce grid size in settings
- Close background applications
- Restart game to clear memory

**Audio Not Playing**
- Check device volume settings
- Ensure music is enabled in game settings
- Restart the application

**Game Not Saving Progress**
- Check device storage space
- Ensure app has storage permissions
- Clear app cache and restart

### Known Limitations
- Grid sizes above 12x12 may impact performance on older devices
- Haptic feedback requires hardware support
- Background music may pause during phone calls

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Ensure all tests pass

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Android Team**: For Jetpack Compose and modern Android tools
- **Material Design**: For comprehensive design guidelines
- **Kotlin Team**: For the elegant programming language
- **Google**: For the Android platform and development tools

## 📞 Contact

- **Developer**: Akın Alp Fidan
- **Email**: akinalpfdn@example.com
- **GitHub**: [github.com/akinalpfdan](https://github.com/akinalpfdan)

---

Enjoy playing Sortue! 🌈✨