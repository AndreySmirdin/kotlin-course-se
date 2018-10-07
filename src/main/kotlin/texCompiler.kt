package ru.hse.spb

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


fun compile(name: String, document: Document) {
    val texName = "$name.tex"
    document.toOutputStream(File(texName).outputStream())
    val process = Runtime.getRuntime().exec("pdflatex $texName")
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.lines().forEach { println(it) }
    process.waitFor()
}