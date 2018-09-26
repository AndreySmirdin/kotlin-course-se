package ru.hse.spb

import org.junit.Test

class TestParserWithErrors {

    @Test
    fun testFunction() {
        parseProgram("fun foo {}", 1, 8)
        parseProgram("fun foo(arg1 arg2)", 1, 13)
        parseProgram("fun foo() 1+2", 1, 10)
    }

    @Test
    fun testVar() {
        parseProgram("var", 1, 3)
        parseProgram("var a =", 1, 7)
        parseProgram("var a = b = c", 1, 10)
    }

    @Test
    fun testWhile() {
        parseProgram("while (1)", 1, 9)
        parseProgram("while () {}", 1, 7)
        parseProgram("while {}", 1, 6)
    }

    @Test
    fun testIf() {
        parseProgram("if (1)", 1, 6)
        parseProgram("if () {} else {}", 1, 4)
    }

    @Test
    fun testAssignment() {
        parseProgram("a = b = 3", 1, 6)
    }

}