package com.akinalpfdn.sortue.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akinalpfdn.sortue.models.Corners
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.RGBData
import com.akinalpfdn.sortue.models.Tile
import com.akinalpfdn.sortue.models.GameMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("sortue_prefs", Context.MODE_PRIVATE)

    private val _tiles = MutableStateFlow<List<Tile>>(emptyList())
    val tiles: StateFlow<List<Tile>> = _tiles.asStateFlow()

    private val _status = MutableStateFlow(GameStatus.MENU)

    val status: StateFlow<GameStatus> = _status.asStateFlow()

    private val _gridDimension = MutableStateFlow(4)
    val gridDimension: StateFlow<Int> = _gridDimension.asStateFlow()

    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> = _moves.asStateFlow()

    private val _selectedTileId = MutableStateFlow<Int?>(null)
    val selectedTileId: StateFlow<Int?> = _selectedTileId.asStateFlow()

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _minMoves = MutableStateFlow(0)
    val minMoves: StateFlow<Int> = _minMoves.asStateFlow()

    private val _gameMode = MutableStateFlow(GameMode.CASUAL)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()


    private var shuffleJob: Job? = null
    private var winJob: Job? = null

    private var currentCorners: Corners? = null

    private val gson = com.google.gson.Gson()

    init {
        val lastModeName = prefs.getString("last_active_mode", GameMode.CASUAL.name)
        val lastMode = try { GameMode.valueOf(lastModeName!!) } catch (e: Exception) { GameMode.CASUAL }
        
        if (!loadGameState(lastMode)) {
            // If no save for last mode, start fresh casual
            startNewGame(mode = GameMode.CASUAL, dimension = 4)
        }
    }

    private fun saveGameState() {
        // Save using key specific to current mode
        val mode = _gameMode.value
        val state = GameState(
            tiles = _tiles.value,
            status = _status.value,
            gridDimension = _gridDimension.value,
            moves = _moves.value,
            corners = currentCorners,
            minMoves = _minMoves.value,
            gameMode = mode
        )
        val json = gson.toJson(state)
        prefs.edit()
            .putString("saved_game_state_${mode.name}", json)
            .putString("last_active_mode", mode.name)
            .apply()
    }

    private fun loadGameState(targetMode: GameMode): Boolean {
        val json = prefs.getString("saved_game_state_${targetMode.name}", null) ?: return false
        return try {
            val state = gson.fromJson(json, GameState::class.java)
            _tiles.value = state.tiles
            _status.value = state.status
            _gridDimension.value = state.gridDimension
            _moves.value = state.moves
            currentCorners = state.corners
            _minMoves.value = state.minMoves ?: 0
            _gameMode.value = state.gameMode ?: targetMode

            _gameMode.value = state.gameMode ?: targetMode

            val dim = state.gridDimension
            // Update: Use correct key based on restored GameMode
            val restoredMode = _gameMode.value
            val levelKey = when (restoredMode) {
                GameMode.CASUAL -> "level_count_$dim"
                GameMode.LADDER -> "level_count_LADDER"
                GameMode.CHALLENGE -> "level_count_CHALLENGE"
            }
            val savedLevel = prefs.getInt(levelKey, 0)
            _currentLevel.value = savedLevel + 1

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private data class GameState(
        val tiles: List<Tile>,
        val status: GameStatus,
        val gridDimension: Int,
        val moves: Int,
        val corners: Corners?,
        val minMoves: Int? = 0,
        val gameMode: GameMode? = GameMode.CASUAL
    )

    fun startNewGame(dimension: Int? = null, mode: GameMode? = null, preserveColors: Boolean = false) {
        dimension?.let { _gridDimension.value = it }
        mode?.let { _gameMode.value = it }

        val dim = _gridDimension.value

        // Determine Level Key based on Mode
        val currentMode = _gameMode.value
        val levelKey = when (currentMode) {
            GameMode.CASUAL -> "level_count_$dim" // Preserve existing behavior for Casual
            GameMode.LADDER -> "level_count_LADDER"
            GameMode.CHALLENGE -> "level_count_CHALLENGE"
        }

        val savedLevel = prefs.getInt(levelKey, 0)
        _currentLevel.value = savedLevel + 1

        shuffleJob?.cancel()
        winJob?.cancel()
        _status.value = GameStatus.PREVIEW
        _moves.value = 0
        _minMoves.value = 0
        _selectedTileId.value = null

        val w = dim
        val h = dim

        // DETERMINISTIC SEED GENERATION
        // Seed = (Mode_Ordinal + 1) * 10000 + Level. 
        // Example: Casual(0) Lvl1 -> 10001. Ladder(1) Lvl1 -> 20001.
        // Ensures different modes have different seeds, and levels change (no zero).
        val modeSeedOffset = (_gameMode.value.ordinal + 1) * 10000L
        val levelSeed = modeSeedOffset + _currentLevel.value
        // Use this single Random instance for all generation in this level
        val levelRandom = Random(levelSeed)

        // 1. Generate corners
        val corners = if (preserveColors && currentCorners != null) {
            currentCorners!!
        } else {
            // Use Curated Strategy with Seeded Random
            generateHarmoniousCorners(levelRandom).also { currentCorners = it }
        }

        val newTiles = mutableListOf<Tile>()
        var idCounter = 0

        // 2. Generate Grid
        for (y in 0 until h) {
            for (x in 0 until w) {
                // EXPLICIT Corner Logic
                val isTopLeft = (x == 0 && y == 0)
                val isTopRight = (x == w - 1 && y == 0)
                val isBottomLeft = (x == 0 && y == h - 1)
                val isBottomRight = (x == w - 1 && y == h - 1)

                val isFixed = isTopLeft || isTopRight || isBottomLeft || isBottomRight

                val colorData = RGBData.interpolated(x, y, w, h, corners)

                newTiles.add(
                    Tile(
                        id = idCounter,
                        correctId = idCounter,
                        rgb = colorData,
                        isFixed = isFixed,
                        currentIdx = idCounter
                    )
                )
                idCounter += 1
            }
        }

        _tiles.value = newTiles
        saveGameState()

        // 3. Schedule Shuffle
        // We must pass the seeded random (or a fresh specific one) to ensure deterministic shuffle too.
        // Since Random instance state mutates, we can pass `levelRandom` if we want 'shuffle' to depend on 'corners' generation steps.
        // Or we can create a FRESH deterministic Random for shuffle part:
        // val shuffleRandom = Random(levelSeed + 1) 
        // Let's use `levelRandom` to keep one continuous stream of randomness for the level.
        // We need to pass it to the coroutine.
        shuffleJob = viewModelScope.launch {
            delay(2500)
            shuffleBoard(Random(levelSeed + 999)) // Use a variant of seed for shuffle to ensure it's determined but distinct
        }
    }
    enum class HarmonyProfile {
        SUNSET, OCEAN, FOREST, BERRY, AURORA, CITRUS, MIDNIGHT
    }
    // Generates aesthetically pleasing palettes using Curated Harmony Profiles
    // Seeded Random version
    private fun generateHarmoniousCorners(rng: Random): Corners {

        val profile = HarmonyProfile.values().random(rng) // Use seeded random

        // Helper to randomize slightly within a safe range
        fun rnd(min: Double, max: Double): Double = rng.nextDouble(min, max)

        var h1 = 0.0; var s1 = 0.0; var b1 = 0.0
        var h2 = 0.0; var s2 = 0.0; var b2 = 0.0
        var h3 = 0.0; var s3 = 0.0; var b3 = 0.0
        var h4 = 0.0; var s4 = 0.0; var b4 = 0.0

        when (profile) {
            HarmonyProfile.SUNSET -> {
                // Warm gradient: Yellow/Orange -> Purple/Pink
                h1 = rnd(0.12, 0.16) // Warm Yellow
                s1 = rnd(0.2, 0.4); b1 = rnd(0.95, 1.0) // Light

                h2 = rnd(0.02, 0.08) // Orange/Red
                s2 = rnd(0.7, 0.9); b2 = rnd(0.9, 1.0)

                h3 = rnd(0.85, 0.92) // Magenta
                s3 = rnd(0.4, 0.6); b3 = rnd(0.8, 0.9)

                h4 = rnd(0.75, 0.82) // Deep Purple
                s4 = rnd(0.8, 1.0); b4 = rnd(0.3, 0.5) // Dark
            }
            HarmonyProfile.OCEAN -> {
                // Cool gradient: White/Cyan -> Deep Navy
                h1 = rnd(0.5, 0.55) // Cyan
                s1 = rnd(0.05, 0.2); b1 = rnd(0.95, 1.0) // Almost white

                h2 = rnd(0.55, 0.6) // Sky Blue
                s2 = rnd(0.5, 0.7); b2 = rnd(0.9, 1.0)

                h3 = rnd(0.6, 0.65) // Azure
                s3 = rnd(0.6, 0.8); b3 = rnd(0.6, 0.8)

                h4 = rnd(0.65, 0.7) // Deep Blue
                s4 = rnd(0.9, 1.0); b4 = rnd(0.2, 0.4) // Dark
            }
            HarmonyProfile.FOREST -> {
                // Fresh greens: Lime -> Emerald -> Teal (No Brown!)
                h1 = rnd(0.25, 0.32) // Fresh Green/Lime
                s1 = rnd(0.3, 0.5); b1 = rnd(0.9, 1.0) // Bright

                h2 = rnd(0.35, 0.42) // Green
                s2 = rnd(0.6, 0.8); b2 = rnd(0.8, 0.9)

                h3 = rnd(0.45, 0.5) // Teal Green
                s3 = rnd(0.5, 0.7); b3 = rnd(0.6, 0.8)

                h4 = rnd(0.5, 0.55) // Dark Teal
                s4 = rnd(0.8, 1.0); b4 = rnd(0.2, 0.4) // Dark
            }
            HarmonyProfile.BERRY -> {
                // Pink -> Red -> Purple
                h1 = rnd(0.9, 0.95) // Light Pink
                s1 = rnd(0.2, 0.4); b1 = rnd(0.95, 1.0)

                h2 = rnd(0.95, 1.0) // Red/Pink
                s2 = rnd(0.7, 0.9); b2 = rnd(0.8, 1.0)

                h3 = rnd(0.7, 0.8) // Violet
                s3 = rnd(0.5, 0.7); b3 = rnd(0.6, 0.8)

                h4 = rnd(0.8, 0.9) // Deep Magenta
                s4 = rnd(0.9, 1.0); b4 = rnd(0.2, 0.4)
            }
            HarmonyProfile.AURORA -> {
                // Green -> Blue -> Purple (Classic Northern Lights)
                h1 = rnd(0.3, 0.35) // Green
                s1 = rnd(0.4, 0.6); b1 = rnd(0.9, 1.0)

                h2 = rnd(0.5, 0.55) // Cyan
                s2 = rnd(0.6, 0.8); b2 = rnd(0.8, 0.9)

                h3 = rnd(0.6, 0.65) // Blue
                s3 = rnd(0.5, 0.7); b3 = rnd(0.6, 0.8)

                h4 = rnd(0.75, 0.8) // Purple
                s4 = rnd(0.8, 1.0); b4 = rnd(0.3, 0.5)
            }
            HarmonyProfile.CITRUS -> {
                // Yellow -> Orange -> Lime
                h1 = rnd(0.14, 0.18) // Lemon Yellow
                s1 = rnd(0.2, 0.4); b1 = rnd(0.95, 1.0)

                h2 = rnd(0.08, 0.12) // Orange Yellow
                s2 = rnd(0.6, 0.8); b2 = rnd(0.9, 1.0)

                h3 = rnd(0.25, 0.3) // Lime
                s3 = rnd(0.5, 0.7); b3 = rnd(0.7, 0.9)

                h4 = rnd(0.02, 0.06) // Deep Orange
                s4 = rnd(0.9, 1.0); b4 = rnd(0.4, 0.6)
            }
            HarmonyProfile.MIDNIGHT -> {
                // Grey -> Blue -> Violet
                h1 = rnd(0.6, 0.7) // Blue-ish Grey
                s1 = rnd(0.0, 0.1); b1 = rnd(0.9, 1.0) // White/Grey

                h2 = rnd(0.6, 0.65) // Slate Blue
                s2 = rnd(0.3, 0.5); b2 = rnd(0.6, 0.8)

                h3 = rnd(0.7, 0.75) // Violet Grey
                s3 = rnd(0.4, 0.6); b3 = rnd(0.5, 0.7)

                h4 = rnd(0.65, 0.7) // Midnight Blue
                s4 = rnd(0.8, 1.0); b4 = rnd(0.1, 0.3) // Very Dark
            }
        }

        // Define all 4 color objects
        val c1 = RGBData.fromHSB(h1, s1, b1) // Lightest
        val c4 = RGBData.fromHSB(h4, s4, b4) // Darkest
        val c2 = RGBData.fromHSB(h2, s2, b2) // Mid 1
        val c3 = RGBData.fromHSB(h3, s3, b3) // Mid 2

        // Rotate/Shuffle assignments so "Light" isn't always Top-Left
        val rotation = rng.nextInt(0, 4)

        val tl: RGBData
        val tr: RGBData
        val bl: RGBData
        val br: RGBData

        when (rotation) {
            0 -> { tl = c1; tr = c2; bl = c3; br = c4 } // Original (Light TL -> Dark BR)
            1 -> { tl = c3; tr = c1; bl = c4; br = c2 } // Rotated 90
            2 -> { tl = c4; tr = c3; bl = c2; br = c1 } // Rotated 180
            else -> { tl = c2; tr = c4; bl = c1; br = c3 } // Rotated 270
        }

        return Corners(tl, tr, bl, br)
    }

    private fun shuffleBoard(rng: Random) {
        val currentTiles = _tiles.value
        val w = _gridDimension.value
        val h = _gridDimension.value

        // Separate mutable (movable) tiles from fixed corners
        val mutableTiles = currentTiles.filter { !it.isFixed }.toMutableList()
        val fixedTiles = currentTiles.filter { it.isFixed }

        // Shuffle only the middle parts using Deterministic RNG
        mutableTiles.shuffle(rng)

        // Reconstruct the grid array
        val finalGrid = arrayOfNulls<Tile>(w * h)

        // 1. Put fixed tiles back exactly where they belong
        for (tile in fixedTiles) {
            finalGrid[tile.correctId] = tile
        }

        // 2. Fill the remaining empty slots with the shuffled tiles
        var mutIdx = 0
        for (i in finalGrid.indices) {
            if (finalGrid[i] == null) {
                // Update currentIdx for logic
                val tile = mutableTiles[mutIdx]
                tile.currentIdx = i
                finalGrid[i] = tile
                mutIdx += 1
            }
        }

        val shuffled = finalGrid.filterNotNull()
        _tiles.value = shuffled
        _minMoves.value = calculateMinMoves(shuffled)
        _status.value = GameStatus.PLAYING
        // For Ladder mode, moves reset to 0 in startNewGame, but if we are restarting level?
        // startNewGame calls shuffleBoard 2.5s later. Moves are 0ed in startNewGame.
        saveGameState()
    }

    fun selectTile(tile: Tile) {
        if (_status.value != GameStatus.PLAYING || tile.isFixed) return

        // TODO: Haptic feedback (requires View/Context or callback)

        val currentSelectedId = _selectedTileId.value
        if (currentSelectedId != null) {
            if (currentSelectedId == tile.id) {
                _selectedTileId.value = null
            } else {
                swapTiles(currentSelectedId, tile.id)
                _selectedTileId.value = null
            }
        } else {
            _selectedTileId.value = tile.id
        }
    }

    fun swapTiles(id1: Int, id2: Int) {
        val currentList = _tiles.value.toMutableList()
        val idx1 = currentList.indexOfFirst { it.id == id1 }
        val idx2 = currentList.indexOfFirst { it.id == id2 }

        if (idx1 != -1 && idx2 != -1) {
            Collections.swap(currentList, idx1, idx2)

            // Update currentIdx
            currentList[idx1].currentIdx = idx1
            currentList[idx2].currentIdx = idx2

            _tiles.value = currentList
            _moves.value += 1
            saveGameState()

            if (_gameMode.value == GameMode.LADDER) {
                if (_moves.value >= 200) {
                     // Check win first? Or strict limit? 
                     // Usually check win first.
                     checkWinCondition() // Updates status if won
                     if (_status.value != GameStatus.ANIMATING && _status.value != GameStatus.WON) {
                         // Failed
                         _status.value = GameStatus.GAME_OVER
                         saveGameState()
                     }
                     return
                }
            }
            checkWinCondition()
        }
    }

    fun useHint() {
        if (_status.value != GameStatus.PLAYING) return
        if (_gameMode.value == GameMode.LADDER) return // No hints in Ladder


        val currentList = _tiles.value.toMutableList()

        // Find a tile that is NOT in its correct spot (and is not a corner)
        // In Kotlin list, index is the current position.
        val wrongTileIndex = currentList.indexOfFirst {
            it.correctId != currentList.indexOf(it) && !it.isFixed
        }

        if (wrongTileIndex != -1) {
            val tileToFix = currentList[wrongTileIndex]
            val targetIdx = tileToFix.correctId // This is where it belongs

            // Swap
            Collections.swap(currentList, wrongTileIndex, targetIdx)

            // Update currentIdx
            currentList[wrongTileIndex].currentIdx = wrongTileIndex
            currentList[targetIdx].currentIdx = targetIdx

            _tiles.value = currentList
            saveGameState()
            checkWinCondition()
        }
    }

    private fun checkWinCondition() {
        val currentList = _tiles.value
        val isWin = currentList.withIndex().all { (index, tile) ->
            tile.correctId == index
        }

        if (isWin) {
            _status.value = GameStatus.ANIMATING
            // TODO: Success feedback

            // Increment Level
            val dim = _gridDimension.value
            val currentMode = _gameMode.value
            
            val key = when (currentMode) {
                GameMode.CASUAL -> "level_count_$dim"
                GameMode.LADDER -> "level_count_LADDER"
                GameMode.CHALLENGE -> "level_count_CHALLENGE"
            }
            
            val currentWins = prefs.getInt(key, 0)
            prefs.edit().putInt(key, currentWins + 1).apply()

            winJob = viewModelScope.launch {
                delay(2000)
                _status.value = GameStatus.WON
                saveGameState()
            }
        }
    }

    private fun calculateMinMoves(tiles: List<Tile>): Int {
        val n = tiles.size
        val visited = BooleanArray(n) { false }
        var cycles = 0

        // Permutation P[i] = tiles[i].correctId
        // Trace cycles of this permutation
        for (i in 0 until n) {
            if (visited[i]) continue
            
            // If at correct position, it's a 1-cycle
            if (tiles[i].correctId == i) {
                visited[i] = true
                cycles++
                continue
            }

            var current = i
            while (!visited[current]) {
                visited[current] = true
                // The element that belongs at 'current' index is somewhere? 
                // No. We are tracing position i -> where element at i belongs -> where element at that pos belongs...
                // The element at `current` belongs at `tiles[current].correctId`.
                // Who is at that target position? `tiles[  tiles[current].correctId  ]` ?
                // Let's verify standard algorithm.
                // We want to sort array. A swap (i, j) changes permutation sign.
                // Min Swaps = N - Cycles.
                // Cycle: x -> P[x] -> P[P[x]] ...
                // Here P[i] is "Where element at i belongs". No.
                // P[i] should be "What element is at position i". 
                // Let's take `tiles`. `tiles[i]` is the Tile object at index `i`.
                // `tiles[i].correctId` tells us this Tile belongs at index `correctId`.
                // So if we have [Tile(0), Tile(1)] -> P=[0, 1].
                // If we have [Tile(1), Tile(0)] -> P=[1, 0].
                // Decomposition of [1, 0]: index 0 has 1. 1 belongs to 1. Index 1 has 0. 0 belongs to 0.
                // Cycle: 0 -> has 1 -> go to index 1 -> has 0 -> go to index 0. Closed. (0 1).
                
                val targetPos = tiles[current].correctId
                // We must move to the index `targetPos`.
                current = targetPos
            }
            cycles++
        }
        return n - cycles
    }
    fun goToMenu() {
        if (_status.value != GameStatus.GAME_OVER) {
             _status.value = GameStatus.MENU
        }
    }

    fun resumeGame() {
        if (_tiles.value.isNotEmpty() && _status.value != GameStatus.WON && _status.value != GameStatus.GAME_OVER) {
            _status.value = GameStatus.PLAYING
        }
    }

    fun playOrResumeGame(mode: GameMode, size: Int) {
        // 1. Save current state of the active mode before switching (if applicable)
        if (_tiles.value.isNotEmpty()) {
            saveGameState()
        }

        // 2. Try to load the saved state for the TARGET mode
        if (loadGameState(mode)) {
            // We found a save!
            // In Casual, if "resume" logic is strictly "pick up where left off", 
            // we should NOT overwrite it just because I passed a default 'size' of 4 from the menu.
            // However, if the USER explicitly wanted to change size, they can't do it from Menu anymore.
            // They do it from Settings inside the game.
            // So here, we ALWAYS resume if a save exists.
            
            // Just ensure we are not in MENU state if we loaded data
            if (_status.value == GameStatus.MENU) {
                _status.value = GameStatus.PLAYING // Resume play
            }
            
        } else {
            // No save found for this mode, start fresh
            startNewGame(dimension = size, mode = mode)
        }
    }
}