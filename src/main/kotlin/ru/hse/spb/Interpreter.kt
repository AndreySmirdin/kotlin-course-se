package ru.hse.spb

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class Interpreter : ExpBaseVisitor<Int?>() {
    private var varScope = Scope<Int>(null)

    // For each functionParams name storing Block to do and argument names.
    private var funScope = Scope<Pair<ExpParser.BlockWithBracesContext, ArrayList<String>>>(null)

    private var functionParams = ArrayList<Pair<String, Int>>()

    private var inReturn = false
    override fun visitFile(ctx: ExpParser.FileContext): Int? {
        varScope = Scope(null)
        return visitBlock(ctx.block())
    }

    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        for (statement in ctx.statement()) {
            val result = visit(statement.getChild(0))
            if (inReturn || statement.getChild(0) is ExpParser.Return_Context) {
                inReturn = true
                if (ctx.parent is ExpParser.FunctionContext)
                    inReturn = false
                return result
            }
        }
        return null
    }

    override fun visitBlockWithBraces(ctx: ExpParser.BlockWithBracesContext): Int? {
        varScope = Scope(varScope)
        funScope = Scope(funScope)
        for (p in functionParams) {
            varScope.addValue(p.first, p.second)
        }
        functionParams.clear()
        val result = visitBlock(ctx.block())
        varScope = varScope.outer!!
        funScope = funScope.outer!!
        return result
    }


    override fun visitStatement(ctx: ExpParser.StatementContext): Int? {
        return visit(ctx.getChild(0))
    }


    override fun visitFunction(ctx: ExpParser.FunctionContext): Int? {
        val name = ctx.Identifier().toString()
        val previous = funScope.getValue(name)
        if (previous != null) {
            throw RuntimeException("Error: name ${name} was previosly declared.")
        }

        val params = ArrayList<String>()
        val paramIdentifiers = ctx.parameterNames().children
        paramIdentifiers?.forEach { param -> params.add(param.text) }
//        if (paramIdentifiers != null) {
//            for (param in paramIdentifiers) {
//                params.add(param.text)
//            }
//        }
        funScope.addValue(name, Pair(ctx.blockWithBraces(), params))
        return null
    }

    override fun visitVariable(ctx: ExpParser.VariableContext): Int? {
        val name = ctx.Identifier().toString()
        val previous = varScope.getValue(name)
        if (previous != null) {
            throw RuntimeException("Error: name ${name} was previosly declared.")
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
            return visitBlockWithBraces(ctx.blockWithBraces()[0])
        } else if (ctx.Else() != null) {
            return visitBlockWithBraces(ctx.blockWithBraces()[1])
        }
        return null
    }

    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        varScope.setValue(getName(ctx.Identifier()), visitExpression(ctx.expression()))
        return null
    }

    private fun getName(identifier: TerminalNode): String {
        return identifier.text
    }

    override fun visitReturn_(ctx: ExpParser.Return_Context): Int? {
        return visitExpression(ctx.expression())
    }

    override fun visitExpression(ctx: ExpParser.ExpressionContext): Int {
        return visit(ctx.functionCall() ?: ctx.expression() ?: ctx.getChild(0))!!
    }

    override fun visitFunctionCall(ctx: ExpParser.FunctionCallContext): Int {
        val name = ctx.Identifier().text

        if (name == "println") {
            return doPrintln(ctx.arguments())
        }

        val func = funScope.getValue(name)
        if (func == null) {
            throw RuntimeException("Undefined functionParams: ${func}")
        }
        func.second.forEach { param: String -> functionParams.add(Pair(param, 0)) }
        visit(ctx.arguments())
        return visit(func.first) ?: 0
    }

    private fun doPrintln(arguments: ExpParser.ArgumentsContext): Int {
        for (i in arguments.children.indices.step(2)) {
            print(visit(arguments.getChild(i)))
        }
        println()
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
            val current = visit(ctx.getChild(i)).toBool()
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
        return if (result != null) result.toInt() else left
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
        return node.text.toIntOrNull() ?: varScope.getValue(node.text)
        ?: throw RuntimeException("Undefined variable ${node.text}")
    }

    override fun visitArguments(ctx: ExpParser.ArgumentsContext): Int? {
        if (ctx.childCount != functionParams.size)
            throw RuntimeException("Invalid number of arguments.")
        for (i in functionParams.indices.step(2))
            functionParams[i] = functionParams[i].copy(second = visit(ctx.getChild(i))!!)
        return null
    }

}

private fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

private fun Int?.toBool(): Boolean {
    return (this != 0)
}
