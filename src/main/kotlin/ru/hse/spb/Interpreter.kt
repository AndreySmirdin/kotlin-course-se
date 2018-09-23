package ru.hse.spb

import org.antlr.v4.runtime.tree.TerminalNode
import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class Interpreter : ExpBaseVisitor<Int?>() {

    class Scope(outer: Scope?) {
        var outer: Scope? = outer
        private val current = HashMap<String, Int>()

        fun getVar(name: String): Int? = if (current.containsKey(name)) current[name] else outer?.getVar(name)
        fun addValue(name: String, value: Int) {
            current[name] = value
        }

        fun setValue(name: String, value: Int) {
            if (current.containsKey(name)) {
                current[name] = value
            } else if (outer != null) {
                outer!!.setValue(name, value)
            } else {
                throw RuntimeException("Undefined name ${name}")
            }
        }
    }

    private var scope: Scope = Scope(null)

    override fun visitFile(ctx: ExpParser.FileContext): Int? {
        scope = Scope(null)
        return visitBlock(ctx.block())
    }

    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        var result: Int? = 0
        for (statement in ctx.statement()) {
            result = visitStatement(statement)
        }
        return result
    }

    override fun visitBlockWithBraces(ctx: ExpParser.BlockWithBracesContext): Int? {
        scope = Scope(scope)
        val result = visitBlock(ctx.block())
        scope = scope.outer!!
        // copy change
        return result
    }

    override fun visitStatement(ctx: ExpParser.StatementContext): Int? {
        val statement = ctx.getChild(0)
        return when (statement) {
            is ExpParser.FunctionContext -> visitFunction(statement)
            is ExpParser.VariableContext -> visitVariable(statement)
            is ExpParser.ExpressionContext -> visitExpression(statement)
            is ExpParser.While_Context -> visitWhile_(statement)
            is ExpParser.If_Context -> visitIf_(statement)
            is ExpParser.AssignmentContext -> visitAssignment(statement)
            is ExpParser.Return_Context -> visitReturn_(statement)
            else -> throw RuntimeException("Undefined command")
        }
    }


    override fun visitFunction(ctx: ExpParser.FunctionContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVariable(ctx: ExpParser.VariableContext): Int {
        val name = ctx.Identifier().toString()
        val previous = scope.getVar(name)
        if (previous != null) {
            throw RuntimeException("Error: name ${name} was previosly declared.")
        }
        scope.addValue(ctx.Identifier().toString(), visitExpression(ctx.expression()))
        return 0
    }

    override fun visitParameterNames(ctx: ExpParser.ParameterNamesContext?): Int {
        visit()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitWhile_(ctx: ExpParser.While_Context): Int {
        var expr = visit(ctx.expression())
        while (expr != 0) {
            visitBlockWithBraces(ctx.blockWithBraces())
            expr = visit(ctx.expression())
        }
        return 0
    }

    override fun visitIf_(ctx: ExpParser.If_Context): Int? {
        val expr = visitExpression(ctx.expression())
        if (expr != 0) {
            return visitBlockWithBraces(ctx.blockWithBraces()[0])
        } else if (ctx.Else() != null) {
            return visitBlockWithBraces(ctx.blockWithBraces()[1])
        }
        return 0
    }

    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        scope.setValue(getName(ctx.Identifier()), visitExpression(ctx.expression()))
        return null
    }

    private fun getName(identifier: TerminalNode): String {
        return identifier.text
    }

    override fun visitReturn_(ctx: ExpParser.Return_Context): Int? {
        return visitExpression(ctx.expression())
    }

    override fun visitExpression(ctx: ExpParser.ExpressionContext): Int {
        val expr = ctx.getChild(0)
        return when (expr) {
            is ExpParser.FunctionCallContext -> visitFunctionCall(expr)
            is ExpParser.BinaryExpressionContext -> visitBinaryExpression(expr)
            is TerminalNode -> 3
            else -> visitExpression(ctx.getChild(1) as ExpParser.ExpressionContext)
        }
    }

    override fun visitFunctionCall(ctx: ExpParser.FunctionCallContext): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitBinaryExpression(ctx: ExpParser.BinaryExpressionContext): Int {
        return visitLogicalExp(ctx.logicalExp())
    }

    override fun visitLogicalExp(ctx: ExpParser.LogicalExpContext): Int {
        var result = visitComparisonExp(ctx.getChild(0) as ExpParser.ComparisonExpContext).toBool()
        val oparator = ctx.getChild(1)
        for (i in 3..ctx.childCount step 2) {
            val current = visitComparisonExp(ctx.getChild(i) as ExpParser.ComparisonExpContext).toBool()
            when (oparator.text) {
                "&&" -> result = result && current
                "||" -> result = result || current
            }
        }
        return result.toInt()
    }

    override fun visitComparisonExp(ctx: ExpParser.ComparisonExpContext): Int {
        var left = visitAdditionExp(ctx.getChild(0) as ExpParser.AdditionExpContext)
        val oparator = ctx.getChild(1)
        var result: Boolean? = null
        if (oparator != null) {
            val right = visitAdditionExp(ctx.getChild(2) as ExpParser.AdditionExpContext)
            when (oparator.text) {
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
        var result = visitMultiplyExp(ctx.getChild(0) as ExpParser.MultiplyExpContext)
        val oparator = ctx.getChild(1)
        for (i in 3..ctx.childCount step 2) {
            val current = visitMultiplyExp(ctx.getChild(i) as ExpParser.MultiplyExpContext)
            when (oparator.text) {
                "+" -> result += current
                "-" -> result -= current
            }
        }
        return result
    }

    override fun visitMultiplyExp(ctx: ExpParser.MultiplyExpContext): Int {
        return visitBaseArithmeticExpression()
    }

    override fun visitArguments(ctx: ExpParser.ArgumentsContext): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

private fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

private fun Int?.toBool(): Boolean {
    return (this != 0)
}
