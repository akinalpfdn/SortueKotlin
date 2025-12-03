package com.akinalpfdn.sortue.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akinalpfdn.sortue.models.Corners
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.RGBData
import com.akinalpfdn.sortue.models.Tile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("sortue_prefs", Context.MODE_PRIVATE)

    private val _tiles = MutableStateFlow<List<Tile>>(emptyList())
    val tiles: StateFlow<List<Tile>> = _tiles.asStateFlow()

    private val _status = MutableStateFlow(GameStatus.PREVIEW)
    val status: StateFlow<GameStatus> = _status.asStateFlow()

    private val _gridDimension = MutableStateFlow(4)
    val gridDimension: StateFlow<Int> = _gridDimension.asStateFlow()

    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> = _moves.asStateFlow()

    private val _selectedTileId = MutableStateFlow<Int?>(null)
    val selectedTileId: StateFlow<Int?> = _selectedTileId.asStateFlow()

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private var shuffleJob: Job? = null
    private var winJob: Job? = null

    private var currentCorners: Corners? = null

    init {
        startNewGame()
    }

    fun startNewGame(dimension: Int? = null, preserveColors: Boolean = false) {
        dimension?.let { _gridDimension.value = it }
        val dim = _gridDimension.value

        // Update level for the current dimension
        val savedLevel = prefs.getInt("level_count_$dim", 0)
        _currentLevel.value = savedLevel + 1

        shuffleJob?.cancel()
        winJob?.cancel()
        _status.value = GameStatus.PREVIEW
        _moves.value = 0
        _selectedTileId.value = null

        val w = dim
        val h = dim

        // 1. Generate corners
        val corners = if (preserveColors && currentCorners != null) {
            currentCorners!!
        } else {
            Corners(
                tl = RGBData.random,
                tr = RGBData.random,
                bl = RGBData.random,
                br = RGBData.random
            ).also { currentCorners = it }
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

        // 3. Schedule Shuffle
        shuffleJob = viewModelScope.launch {
            delay(2500)
            shuffleBoard()
        }
    }

    private fun shuffleBoard() {
        val currentTiles = _tiles.value
        val w = _gridDimension.value
        val h = _gridDimension.value

        // Separate mutable (movable) tiles from fixed corners
        val mutableTiles = currentTiles.filter { !it.isFixed }.toMutableList()
        val fixedTiles = currentTiles.filter { it.isFixed }

        // Shuffle only the middle parts
        mutableTiles.shuffle()

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

        _tiles.value = finalGrid.filterNotNull()
        _status.value = GameStatus.PLAYING
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

    private fun swapTiles(id1: Int, id2: Int) {
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
            checkWinCondition()
        }
    }

    fun useHint() {
        if (_status.value != GameStatus.PLAYING) return

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
            val key = "level_count_$dim"
            val currentWins = prefs.getInt(key, 0)
            prefs.edit().putInt(key, currentWins + 1).apply()

            winJob = viewModelScope.launch {
                delay(2000)
                _status.value = GameStatus.WON
            }
        }
    }
}
