package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class Interpreter : ExpBaseVisitor<Int?>() {
    private var varScope = Scope<Int>(null)

    // For each functionParams name storing Block to do and argument names.
    private var funScope = Scope<ExpParser.FunctionContext>(null)

    private var inReturn = false

    override fun visitFile(ctx: ExpParser.FileContext): Int? {
        varScope = Scope(null)
        return visitBlock(ctx.block())
    }

    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        for (statement in ctx.statement()) {
            val result = visit(statement.getChild(0))
            if (inReturn || statement.children.first() is ExpParser.Return_Context) {
                inReturn = true
                if (ctx.parent.parent is ExpParser.FunctionContext) // First parent should be BlockWithBraces, second -- function.
                    inReturn = false
                return result
            }
        }
        return null
    }

    override fun visitBlockWithBraces(ctx: ExpParser.BlockWithBracesContext): Int? {
        varScope = Scope(varScope)
        funScope = Scope(funScope)
        val result = visit(ctx.block())
        varScope = varScope.outer!!
        funScope = funScope.outer!!
        return result
    }


    override fun visitStatement(ctx: ExpParser.StatementContext): Int? {
        return visit(ctx.children.first())
    }


    override fun visitFunction(ctx: ExpParser.FunctionContext): Int? {
        val name = ctx.Identifier().toString()
        val previous = funScope.getValue(name)
        if (previous != null) {
            throw DoubleFunctionDeclarationException(name, ctx.start)
        }
        funScope.addValue(name, ctx)
        return null
    }

    override fun visitVariable(ctx: ExpParser.VariableContext): Int? {
        val name = ctx.Identifier().toString()
        val previous = varScope.getValue(name)
        if (previous != null) {
            throw DoubleVariableDeclarationException(name, ctx.start)
        }
        varScope.addValue(name, visitExpression(ctx.expression()))
        return null
    }

    override fun visitWhile_(ctx: ExpParser.While_Context): Int? {
        var expr = visit(ctx.expression())
        while (expr != 0) {
            visitBlockWithBraces(ctx.blockWithBraces())
            expr = visit(ctx.expression())
        }
        return null
    }

    override fun visitIf_(ctx: ExpParser.If_Context): Int? {
        val expr = visitExpression(ctx.expression())
        if (expr != 0) {
            return visitBlockWithBraces(ctx.blockWithBraces().first())
        } else if (ctx.Else() != null) {
            return visitBlockWithBraces(ctx.blockWithBraces().last())
        }
        return null
    }

    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        varScope.setValue(getName(ctx.Identifier()), visitExpression(ctx.expression()))
        return null
    }


    override fun visitReturn_(ctx: ExpParser.Return_Context): Int? {
        return visitExpression(ctx.expression())
    }

    override fun visitExpression(ctx: ExpParser.ExpressionContext): Int {
        return visit(ctx.functionCall() ?: ctx.expression() ?: ctx.children.first())!!
    }

    override fun visitFunctionCall(ctx: ExpParser.FunctionCallContext): Int {
        val name = ctx.Identifier().text

        if (name == "println") {
            return doPrintln(ctx.arguments().expression().map { expr -> visit(expr)!! })
        }

        val func = funScope.getValue(name) ?: throw UndefinedFunctionException(name, ctx.start)
        val paramNames = func.parameterNames().Identifier()
        if (paramNames.size != ctx.arguments().expression().size)
            throw InvalidArgumentsNumber(name, ctx.start)
        varScope = Scope(varScope)
        paramNames.indices.forEach { varScope.addValue(paramNames[it].text, visit(ctx.arguments().expression()[it])!!) }
        val ans = visit(func.blockWithBraces()) ?: 0
        varScope = varScope.outer!!
        return ans
    }

    private fun doPrintln(params: List<Int>): Int {
        println(params.joinToString(" "))
        return 0
    }

    override fun visitBinaryExpression(ctx: ExpParser.BinaryExpressionContext): Int {
        return visitLogicalExp(ctx.logicalExp())
    }

    override fun visitLogicalExp(ctx: ExpParser.LogicalExpContext): Int {
        val left = visit(ctx.getChild(0))!!
        var result = left.toBool()

        for (i in 2..ctx.childCount step 2) {
            val operator = ctx.getChild(i - 1)
            val current = visit(ctx.getChild(i))!!.toBool()
            when (operator.text) {
                "&&" -> result = result && current
                "||" -> result = result || current
            }
        }
        return if (ctx.childCount != 1) result.toInt() else left
    }

    override fun visitComparisonExp(ctx: ExpParser.ComparisonExpContext): Int {
        val left = visit(ctx.getChild(0))!!
        val operator = ctx.getChild(1)
        var result: Boolean? = null
        if (operator != null) {
            val right = visit(ctx.getChild(2))!!
            when (operator.text) {
                "!=" -> result = left != right
                "==" -> result = left == right
                "<=" -> result = left <= right
                ">=" -> result = left >= right
                "<" -> result = left < right
                ">" -> result = left > right
            }
        }
        return result?.toInt() ?: left
    }

    override fun visitAdditionExp(ctx: ExpParser.AdditionExpContext): Int {
        return visitBaseArithmeticOperations(ctx)
    }

    override fun visitMultiplyExp(ctx: ExpParser.MultiplyExpContext): Int {
        return visitBaseArithmeticOperations(ctx)
    }

    private fun visitBaseArithmeticOperations(ctx: ParserRuleContext): Int {
        var result = visit(ctx.getChild(0))!!
        for (i in 2..ctx.childCount step 2) {
            val operator = ctx.getChild(i - 1)
            val current = visit(ctx.getChild(i))!!
            when (operator.text) {
                "+" -> result += current
                "-" -> result -= current

                "*" -> result *= current
                "/" -> result /= current
                "%" -> result %= current
            }
        }
        return result
    }

    override fun visitAtomExp(ctx: ExpParser.AtomExpContext): Int {
        return visit(ctx.functionCall() ?: ctx.expression() ?: ctx.getChild(0))!!
    }

    override fun visitTerminal(node: TerminalNode): Int {
        val name = getName(node)
        return name.toIntOrNull() ?: varScope.getValue(name)
        ?: throw UndefinedVariableException(name, node.symbol)
    }


    private fun getName(identifier: TerminalNode): String {
        return identifier.text
    }
}

private fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

private fun Int.toBool(): Boolean {
    return (this != 0)
}
