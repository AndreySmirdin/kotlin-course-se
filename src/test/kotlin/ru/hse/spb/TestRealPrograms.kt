package ru.hse.spb

import org.junit.Test

class TestRealPrograms {

    @Test
    fun testReturn() {
        val program = """
            fun foo() {
                return 3 + 4
            }
            println(foo())
        """.trimIndent()
        evalProgram(program, "7\n")
    }


    @Test
    fun testSimpleIf() {
        val program = """
            var a = 10
            var b = 20
            if (a > b) {
                println(228)
            } else {
                println(239)
            }
        """.trimIndent()
        evalProgram(program, "239\n")
    }

    @Test
    fun testFactorial() {
        val program = """
            fun fact(n) {
                if (n == 1) {
                    return 1
                }
                else {
                    return fact(n - 1) * n
                }
            }
            println(fact(5))
        """.trimIndent()
        evalProgram(program, "120\n")
    }


    @Test
    fun testFunInFun() {
        val program = """
            fun foo(n) {
                fun bar(m) {
                    return n + m
                }
                return bar(1)
            }
            println(foo(41))
        """.trimIndent()
        evalProgram(program, "42\n")
    }

    @Test
    fun testBigExpression() {
        val program = """
            var a = 3
            var b = 4
            println(a + b * ((b + a) % 2) - (a != b))
        """.trimIndent()
        evalProgram(program, "6\n")
    }

    @Test
    fun testWhileExpression() {
        val program = """
            var a = 0
            while (a < 5) {
                a = a + 1
            }
            println(a)
        """.trimIndent()
        evalProgram(program, "5\n")
    }

    @Test
    fun testFib() {
        val program = """
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }
            var i = 1
            while (i <= 5) {
                println(i, fib(i))
                i = i + 1
            }
        """.trimIndent()
        evalProgram(program, "1 1\n2 2\n3 3\n4 5\n5 8\n")
    }


    @Test
    fun testFunWithManyParams() {
        val program = """
            fun foo(a, b, c) {
                return a + b * c
            }
            println(foo(2, 3, 4))
        """.trimIndent()
        evalProgram(program, "14\n")
    }

    @Test
    fun testVarAssignments() {
        val program = """
            var a = 3
            a = a + a
            a = a + 1
            println(a)
        """.trimIndent()
        evalProgram(program, "7\n")
    }

    @Test
    fun testComments() {
        val program = """
            // var a = 3
            // println(1)
            // Nothing here
            println(239) // println(30)
        """.trimIndent()
        evalProgram(program, "239\n")
    }

    @Test(expected = UndefinedVariableException::class)
    fun testUndefinedVariableException() {
        val program = """
            var a = 3 + b
        """.trimIndent()
        evalProgram(program, "")
    }

    @Test(expected = UndefinedFunctionException::class)
    fun testUndefinedFunctionException() {
        val program = """
            foo(1, 2)
        """.trimIndent()
        evalProgram(program, "")
    }

    @Test(expected = InvalidArgumentsNumber::class)
    fun testInvalidArgumentsNumberException() {
        val program = """
            fun foo(n) {}
            foo(1, 2)
        """.trimIndent()
        evalProgram(program, "")
    }

    @Test(expected = DoubleVariableDeclarationException::class)
    fun testDoubleVariableDeclarationException() {
        val program = """
            var a = 3
            var a = 4
        """.trimIndent()
        evalProgram(program, "")
    }

    @Test(expected = DoubleFunctionDeclarationException::class)
    fun testDoubleFunctionDeclarationException() {
        val program = """
            fun foo() {}
            fun foo() {}
        """.trimIndent()
        evalProgram(program, "")
    }
}