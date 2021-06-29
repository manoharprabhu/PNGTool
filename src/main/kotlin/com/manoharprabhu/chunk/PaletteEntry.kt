package com.manoharprabhu.chunk

class PaletteEntry(val red: Byte, val green: Byte, val blue: Byte) {
    override fun toString(): String {
        return "Color ${red}:${green}:${blue}"
    }
}