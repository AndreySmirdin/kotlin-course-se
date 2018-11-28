package ru.hse.spb

import java.io.OutputStream

const val TAB = "    "

interface Renderable {
    fun render(builder: StringBuilder, indent: String)
}

@DslMarker
annotation class TexElementMarker


@TexElementMarker
abstract class Tag(val name: String, vararg val args: Pair<String, String>) : Renderable {

    val children = arrayListOf<Renderable>()

    protected fun <T : Renderable> initTag(tag: T, init: T.() -> Unit) {
        tag.init()
        children.add(tag)
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent\\begin{$name}")
        renderArgs(builder)
        renderChildren(builder, indent)
        builder.appendln("$indent\\end{$name}")
    }

    private fun renderArgs(builder: StringBuilder) {
        args.forEach { builder.append("[${it.first}=${it.second}]") }
        builder.appendln()
    }

    protected fun renderChildren(builder: StringBuilder, indent: String) {
        children.forEach { it.render(builder, "$indent$TAB") }
    }


    override fun toString(): String = buildString { render(this@buildString, "") }

    operator fun String.unaryPlus() {
        children += TextElement(this)
    }

    fun toOutputStream(stream: OutputStream) {
        stream.write(toString().toByteArray())
    }
}

class TextElement(private val text: String) : Renderable {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

abstract class TexTag(name: String, vararg params: Pair<String, String>) : Tag(name, *params) {
    fun frame(frameTitle: String, init: Frame.() -> Unit) = initTag(Frame(frameTitle), init)

    fun itemize(init: Itemize.() -> Unit) = initTag(Itemize(), init)

    fun enumerate(init: Enumerate.() -> Unit) = initTag(Enumerate(), init)

    fun math(expression: String) = children.add(Math(expression))

    fun alignment(type: Alignment, init: CustomTag.() -> Unit) =
            initTag(CustomTag(type.toString().toLowerCase()), init)

    fun customTag(name: String, vararg arg: Pair<String, String>, init: CustomTag.() -> Unit) =
            initTag(CustomTag(name, *arg), init)
}

class CustomTag(name: String, vararg args: Pair<String, String>) : TexTag(name, *args)

enum class Alignment {
    FLUSHLEFT,
    FLUSHRIGHT,
    CENTER;
}


class Document : TexTag("document") {
    private var docClass: String? = null

    private var usedPackages = ArrayList<String>()
    fun documentClass(clazz: String) {
        docClass = clazz
    }

    fun usepackage(vararg packages: String) {
        usedPackages.addAll(packages)
    }

    override fun render(builder: StringBuilder, indent: String) {
        if (docClass != null) {
            builder.appendln("$indent\\documentclass{$docClass}")
        }
        usedPackages.forEach { builder.appendln("\\usepackage{$it}") }
        super.render(builder, indent)
    }
}

class Frame(private val frameTitle: String) : TexTag("frame") {
    override fun render(builder: StringBuilder, indent: String) {
        builder.appendln("$indent\\begin{$name}")
                .appendln("$indent$TAB\\frametitle{$frameTitle}")

        renderChildren(builder, indent)
        builder.appendln("$indent\\end{$name}")
    }
}

abstract class Itemable(name: String) : TexTag(name) {
    fun item(init: Item.() -> Unit) = initTag(Item(), init)
}

class Itemize : Itemable("itemize")
class Enumerate : Itemable("enumerate")

class Item : TexTag("item")

class Math(private val expression: String) : Renderable {
    override fun render(builder: StringBuilder, indent: String) {
        builder.appendln("$indent $ $expression $")
    }
}

fun document(init: Document.() -> Unit): Document = Document().apply(init)