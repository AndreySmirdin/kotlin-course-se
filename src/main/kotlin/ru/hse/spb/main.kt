package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: <name of a file to interpret")
        return
    }
    val lexer = ExpLexer(CharStreams.fromFileName(args[0]))
    val parser = ExpParser(BufferedTokenStream(lexer))
    Interpreter().visitFile(parser.file())
}

