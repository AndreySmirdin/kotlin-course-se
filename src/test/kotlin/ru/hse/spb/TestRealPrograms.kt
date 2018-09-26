package ru.hse.spb

import org.junit.Test

class TestRealPrograms {
    @Test
    fun testFactorial() {
        val program = "fun fact(n) {" +
                "if (n == 1)" +
                "   {return 1}" +
                "else " +
                "   {return fact(n - 1) * n}" +
                "}" +
                "println(fact(5))"
        evalProgram(program, "120\n")
    }

    @Test
    fun testSimpleIf() {
        val program = "var a = 10\n" +
                "var b = 20\n" +
                "if (a > b) {\n" +
                "    println(228)\n" +
                "} else {\n" +
                "    println(239)\n" +
                "}\n"
        evalProgram(program, "239\n")
    }

    @Test
    fun testFunInFun() {
        val program = "fun foo(n) {\n" +
                "    fun bar(m) {\n" +
                "        return m + n\n" +
                "    }\n" +
                "\n" +
                "    return bar(1) // return bar(1)\n" +
                "}\n" +
                "\n" +
                "println(foo(41)) // prints 42"
        evalProgram(program, "42\n")
    }

    @Test
    fun testBigExpression() {
        val program = "var a = 3\n" +
                "var b = 4\n" +
                "println(a + b * ((b + a) % 2) - (a != b))"
        evalProgram(program, "6\n")
    }

    @Test
    fun testWhileExpression() {
        val program = "var a = 0 " +
                "while (a < 5) {" +
                "   a = a + 1" +
                "}" +
                "println(a)"
        evalProgram(program, "5\n")
    }

    @Test
    fun testFib() {
        val program = "fun fib(n) {\n" +
                "    if (n <= 1) {\n" +
                "        return 1\n" +
                "    }\n" +
                "    return fib(n - 1) + fib(n - 2)\n" +
                "}\n" +
                "\n" +
                "println(fib(10))"
//                "var i = 1\n" +
//                "while (i <= 5) {\n" +
//                "    println(i, fib(i))\n" +
//                "    i = i + 1\n" +
//                "}"

        evalProgram(program, "11")
    }

    @Test
    fun testReturn() {
        val program = "fun foo() {" +
                "return 3 + 4" +
                "}" +
                "println(foo())"
        evalProgram(program, "7\n")
    }

    @Test
    fun testFunWithManyParams() {
        val program = "fun foo(a, b, c) {" +
                "return a + b * c" +
                "}" +
                "println(foo(2, 3, 4))"
        evalProgram(program, "14\n")
    }


}