package com.jump.game.domain

import com.jump.game.core.library.XY

data class Platform (
    override var x: Float,
    override var y: Float,
    val platformLength: Int,
    val isMovingLeft: Boolean,
    var isInitial: Boolean = false
): XY