package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun parseProgram(s: String, errorLine: Int = -1, errorPosition: Int = -1) {
    val oldStderr = System.out
    val output = ByteArrayOutputStream()
    System.setErr(PrintStream(output))
    val expLexer = ExpLexer(CharStreams.fromString(s))
    val parser = ExpParser(BufferedTokenStream(expLexer))
    parser.file();
    val errors = output.toString()

    if (errors.isEmpty()) {
        assertThat(errorLine, `is`(-1))
    } else {
        val errorInfo = errors.split(' ')[1].split(':')
        assertEquals(errors, errorLine, errorInfo[0].toInt())
        assertEquals(errors, errorPosition, errorInfo[1].toInt())
    }
    System.setErr(oldStderr)
}

