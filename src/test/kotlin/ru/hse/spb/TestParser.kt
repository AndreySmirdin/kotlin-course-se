package ru.hse.spb

import org.junit.Test

class TestParser {

    @Test
    fun testFile() {
        parseProgram("")
        parseProgram("{}")
    }

    @Test
    fun testFunction() {
        parseProgram("fun foo() {}")
        parseProgram("fun foo() {return 3}")
        parseProgram("fun foo(arg1, arg2, arg3) {}")
    }

    @Test
    fun testVal() {
        parseProgram("var a")
        parseProgram("var a = 5")
        parseProgram("var __VeryStrangeVarName__ = 239")
        parseProgram("var a = (23 * 10) + 9")
        parseProgram("var a = call()")
    }

    @Test
    fun testWhile() {
        parseProgram("while (1) {}")
        parseProgram("while (func() < (a + 3) {}")
        parseProgram("while (0) { var a = 7 }")
    }

    @Test
    fun testIf() {
        parseProgram("if (1) {}")
        parseProgram("if (1) {} else {}")
        parseProgram("if (1 + 7 * a) {} else {}")
        parseProgram("if (1) {var a = 42 call()} else {}")
        parseProgram("if (1) {} else {var a = 42 call()}")
    }

    @Test
    fun testFunctionCall() {
        parseProgram("call()")
        parseProgram("call(arg1, arg2)")
    };

    @Test
    fun testAssignment() {
        parseProgram("a = 7")
        parseProgram("a = a")
        parseProgram("a = call()")
        //parseProgram("longName = (7 + 8) * call()")
    }

    @Test
    fun testBinaryExpressionSimple() {
        parseProgram("2 + 3")
        parseProgram("2 - 3")
        parseProgram("2 * 3")
        parseProgram("2 / 3")
        parseProgram("2 % 3")
        parseProgram("2 || 3")
        parseProgram("2 && 3")
        parseProgram("2 == 3")
        parseProgram("2 != 3")
        parseProgram("2 < 3")
        parseProgram("2 > 3")
        parseProgram("2 <= 3")
        parseProgram("2 >= 3")
    }

    @Test
    fun testBinaryExpressionComplex() {
        parseProgram("2 + 3 * 6 < 3 && 2 || 7")
    }
}