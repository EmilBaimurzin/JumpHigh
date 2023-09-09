package com.jump.game.ui.jump

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jump.game.core.library.GameViewModel
import com.jump.game.core.library.XYIMpl
import com.jump.game.core.library.random
import com.jump.game.domain.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

class JumpViewModel : GameViewModel() {
    private val _platforms = MutableLiveData<List<Platform>>(emptyList())
    val platforms: LiveData<List<Platform>> = _platforms

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    var speed = 5L

    private var jumpScope = CoroutineScope(Dispatchers.Default)

    var isGoingDown = true
    var isGoingLeft = false
    var isGoingRight = false
    var isGoingUp = false
    var isStopped = true
    var canUpdate = false
    var isUpdating = false

    init {
        _playerXY.postValue(XYIMpl(0f, 0f))
    }

    private fun increaseSpeed() {
        gameScope.launch {
            while (true) {
                delay(3000)
                speed += 1
            }
        }
    }

    fun initPlatforms(
        platform1Y: Float,
        platform2Y: Float,
        platform3Y: Float,
        platform4Y: Float,
        platform5Y: Float,
        platform6Y: Float,
        maxX: Int,
        playerHeight: Int,
        playerWidth: Int
    ) {
        val newList = mutableListOf<Platform>()
        repeat(6) {
            newList.add(
                Platform(
                    (0 random maxX).toFloat(),
                    when (it + 1) {
                        1 -> platform1Y
                        2 -> platform2Y
                        3 -> platform3Y
                        4 -> platform4Y
                        5 -> platform5Y
                        else -> platform6Y
                    },
                    1 random 3,
                    (it + 1) % 2 == 0
                )
            )
            newList.first().isInitial = true
            val player = _playerXY.value!!
            player.x = ((maxX - playerWidth) / 2).toFloat()
            player.y = newList.first().y - playerHeight
            _playerXY.postValue(player)
            _platforms.postValue(newList)
        }
    }

    fun stopAnother() {
        jumpScope.cancel()
    }

    fun start(
        smallPlatformLength: Int,
        mediumPlatformLength: Int,
        largePlatformLength: Int,
        maxX: Int,
        platformHeight: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)

        letPlatformsMove(
            smallPlatformLength,
            mediumPlatformLength,
            largePlatformLength,
            maxX,
            platformHeight,
            playerWidth,
            playerHeight
        )
        increaseSpeed()
    }

    private fun letPlatformsMove(
        smallPlatformLength: Int,
        mediumPlatformLength: Int,
        largePlatformLength: Int,
        maxX: Int,
        platformHeight: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isUpdating) {
                    val currentList = _platforms.value!!.toMutableList()
                    val newList = mutableListOf<Platform>()
                    var shouldSet = true
                    currentList.forEach { platform ->

                        val platformLength = when (platform.platformLength) {
                            1 -> smallPlatformLength
                            2 -> mediumPlatformLength
                            else -> largePlatformLength
                        }

                        val platformX = platform.x.toInt()..(platform.x + platformLength).toInt()
                        val platformY =
                            platform.y.toInt()..(platform.y + platformHeight / 5).toInt()

                        val playerXY2 = _playerXY.value!!

                        val playerX = playerXY2.x.toInt()..(playerXY2.x + playerWidth).toInt()
                        val playerY =
                            (playerXY2.y.toInt() + playerHeight - playerHeight / 3)..(playerXY2.y + playerHeight).toInt()

                        if (platform.isMovingLeft && (platform.x + platformLength <= 0)) {
                            val x = maxX.toFloat()
                            if (playerX.any { it in platformX } && playerY.any { it in platformY } && isGoingDown) {
                                val distance = _playerXY.value!!.x - platform.x
                                _playerXY.postValue(XYIMpl(x + distance, _playerXY.value!!.y))
                            }
                            delay(2)
                            platform.x = x
                        } else if (!platform.isMovingLeft && (platform.x >= maxX)) {
                            val x = -platformLength.toFloat()
                            if (playerX.any { it in platformX } && playerY.any { it in platformY } && isGoingDown) {
                                val distance = _playerXY.value!!.x - platform.x
                                _playerXY.postValue(XYIMpl(x + distance, _playerXY.value!!.y))
                            }
                            delay(2)
                            platform.x = x
                        } else {
                            if (platform.isMovingLeft) {
                                platform.x = platform.x - speed
                            } else {
                                platform.x = platform.x + speed
                            }
                        }

                        if (platform.isInitial) {
                            platform.x = ((maxX - platformLength) / 2).toFloat()
                        }
                        ////////////

                        if (playerX.any { it in platformX } && playerY.any { it in platformY } && isGoingDown) {
                            jumpScope.cancel()
                            isStopped = true
                            val xy = _playerXY.value!!
                            if (!platform.isInitial) {
                                if (platform.isMovingLeft) xy.x = xy.x - speed else xy.x =
                                    xy.x + speed
                            }
                            _playerXY.postValue(xy)
                            if (canUpdate) {
                                updateList(platform.y, maxX, platformHeight)
                                shouldSet = false
                            }
                        }

                        ////////////
                        newList.add(platform)
                    }
                    if (shouldSet) {
                        _platforms.postValue(newList)
                    }
                }
            }
        }
    }

    private fun updateList(y: Float, maxX: Int, playerHeight: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            canUpdate = false
            isUpdating = true
            val currentList = _platforms.value!!.toMutableList()
            val listOfY = mutableListOf<Float>()
            currentList.forEach {
                listOfY.add(it.y)
            }
            val item = currentList.find { it.y == y }
            if (item != null) {
                var newList = mutableListOf<Platform>()
                val itemIndex = currentList.indexOf(item)
                repeat(itemIndex) {
                    currentList.removeAt(0)
                }
                newList = currentList

                if (6 - currentList.size != 0) {
                    _scores.postValue(_scores.value!! + 5 * (6 - currentList.size))
                }

                repeat(6 - currentList.size) {
                    newList.add(
                        Platform(
                            (0 random maxX).toFloat(),
                            0f,
                            1 random 3,
                            Random().nextBoolean()
                        )
                    )
                }
                newList.mapIndexed { index, value ->
                    value.y = listOfY[index]
                }
                _platforms.postValue(newList)
                _playerXY.postValue(
                    XYIMpl(
                        _playerXY.value!!.x,
                        listOfY[0] - playerHeight - playerHeight / 3
                    )
                )
            }
            isUpdating = false
        }
    }


    fun jump() {
        jumpScope = CoroutineScope(Dispatchers.Default)
        isStopped = false
        isGoingUp = true
        isGoingDown = false
        canUpdate = true
        jumpScope.launch {
            repeat(10) { r ->
                repeat(5) {
                    delay(16)
                    val xy = _playerXY.value!!
                    xy.y = xy.y - (10 - r + 1)
                    if (isGoingLeft) {
                        xy.x = xy.x - 5
                    }
                    if (isGoingRight) {
                        xy.x = xy.x + 5
                    }
                    _playerXY.postValue(xy)
                }
            }

            isGoingDown = true

            repeat(10) { r ->
                repeat(5) {
                    delay(16)
                    val xy = _playerXY.value!!
                    xy.y = xy.y + (r + 1)
                    if (isGoingLeft) {
                        xy.x = xy.x - 5
                    }
                    if (isGoingRight) {
                        xy.x = xy.x + 5
                    }
                    _playerXY.postValue(xy)
                }
            }

            while (true) {
                delay(16)
                val xy = _playerXY.value!!
                xy.y = xy.y + (10)
                if (isGoingLeft) {
                    xy.x = xy.x - 5
                }
                if (isGoingRight) {
                    xy.x = xy.x + 5
                }
                _playerXY.postValue(xy)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        stopAnother()
    }
}