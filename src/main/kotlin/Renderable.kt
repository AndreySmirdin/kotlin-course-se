package ru.hse.spb

interface Renderable {
    fun render(builder: StringBuilder, indent: String)
}