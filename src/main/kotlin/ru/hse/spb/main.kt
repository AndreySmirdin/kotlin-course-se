package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

fun main(args: Array<String>) {
    val expLexer = ExpLexer(CharStreams.fromString("1 < 2"))
    val parser = ExpParser(BufferedTokenStream(expLexer))
    println(parser)
    val visitor = Interpreter().visitFile(parser.file())
}

