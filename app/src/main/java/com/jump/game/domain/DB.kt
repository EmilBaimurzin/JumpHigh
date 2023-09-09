package com.jump.game.domain

import android.content.Context

class DB(private val context: Context) {
    private val sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE)

    fun getSelectedSymbol(): Int = sp.getInt("SELECTED", 1)
    fun selectSymbol(symbol: Int) = sp.edit().putInt("SELECTED", symbol).apply()
}