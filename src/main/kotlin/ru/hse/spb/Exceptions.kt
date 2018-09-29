package ru.hse.spb

import org.antlr.v4.runtime.Token

open class InterpreterException(private val token: Token) : RuntimeException() {
    fun getErrorPos() = "Line ${token.line}:${token.startIndex}: "
}

class UndefinedVariableException(private val name: String, token: Token) : InterpreterException(token) {
    override val message: String?
        get() = getErrorPos() + "Undefined variable $name."
}

class UndefinedFunctionException(private val name: String, token: Token) : InterpreterException(token) {
    override val message: String?
        get() = getErrorPos() + "Call of undefined function $name."
}

class InvalidArgumentsNumber(private val name: String, token: Token) : InterpreterException(token) {
    override val message: String?
        get() = getErrorPos() + "Invalid number of arguments for function $name."
}

class DoubleVariableDeclarationException(private val name: String, token: Token) : InterpreterException(token) {
    override val message: String?
        get() = getErrorPos() + "Variable $name was previously declared."
}

class DoubleFunctionDeclarationException(private val name: String, token: Token) : InterpreterException(token) {
    override val message: String?
        get() = getErrorPos() + "Function $name was previously declared."
}