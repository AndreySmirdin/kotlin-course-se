package ru.hse.spb

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class TestTex {

    @Test
    fun testEmptyDocument() {
        val result =
                document {

                }.toString()
        val expected = """
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testDocumentWithHeader() {
        val result =
                document {
                    documentClass("beamer")
                }.toString()
        val expected = """
            |\documentclass{beamer}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testDocumentWithPackages() {
        val result =
                document {
                    usepackage("first", "second", "third")
                    usepackage("fourth")
                }.toString()
        val expected = """
            |\usepackage{first}
            |\usepackage{second}
            |\usepackage{third}
            |\usepackage{fourth}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testFrame() {
        val frameName = "best frame ever"
        val result =
                document {
                    frame(frameName) {

                    }
                }.toString()
        val expected = """
            |\begin{document}
            |    \begin{frame}
            |        \frametitle{$frameName}
            |    \end{frame}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testItemize() {
        val result =
                document {
                    itemize {
                        for (i in 1..3) {
                            item { +"Item $i" }
                        }
                    }
                }.toString()
        val expected = """
            |\begin{document}
            |    \begin{itemize}
            |        \begin{item}
            |            Item 1
            |        \end{item}
            |        \begin{item}
            |            Item 2
            |        \end{item}
            |        \begin{item}
            |            Item 3
            |        \end{item}
            |    \end{itemize}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testEnumeration() {
        val result =
                document {
                    enumerate {
                        for (i in 1..3) {
                            item { +"Item $i" }
                        }
                    }
                }.toString()
        val expected = """
            |\begin{document}
            |    \begin{enumerate}
            |        \begin{item}
            |            Item 1
            |        \end{item}
            |        \begin{item}
            |            Item 2
            |        \end{item}
            |        \begin{item}
            |            Item 3
            |        \end{item}
            |    \end{enumerate}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testMath() {
        val result =
                document {
                    math("a^2 + b^2 = c^2")
                }.toString()
        val expected = """
            |\begin{document}
            |     $ a^2 + b^2 = c^2 $
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testAlignment() {
        val result =
                document {
                    alignment(Alignment.CENTER) {
                        +"1"
                    }
                    alignment(Alignment.FLUSHLEFT) {
                        +"2"
                    }
                    alignment(Alignment.FLUSHRIGHT) {
                        +"3"
                    }
                }.toString()
        val expected = """
            |\begin{document}
            |    \begin{center}
            |        1
            |    \end{center}
            |    \begin{flushleft}
            |        2
            |    \end{flushleft}
            |    \begin{flushright}
            |        3
            |    \end{flushright}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testCustomTag() {
        val result =
                document {
                    customTag("pyglist", "language" to "kotlin") {
                        +"val a = 1"
                    }
                }.toString()
        val expected = """
            |\begin{document}
            |    \begin{pyglist}[language=kotlin]
            |        val a = 1
            |    \end{pyglist}
            |\end{document}
            |
        """.trimMargin()
        assertThat(result, `is`(expected))
    }

    @Test
    fun testAllFunctions() {
        val result =
                document {
                    documentClass("beamer")
                    usepackage("babel", "russian")
                    frame("frametitle") {
                        itemize {
                            val rows = arrayListOf<String>("1+1", "2+2")
                            for (row in rows) {

                                item {
                                    alignment(Alignment.CENTER)
                                    {
                                        math(row)
                                    }
                                }

                            }

                        }
                    }
                }
        val expected = """
            |\documentClass{beamer}
            |\usepackage{babel}
            |\usepackage{russian}
            |\begin{document}
            |    \frametitle{frametitle}
            |    \begin{itemize}
            |       \begin{item}
            |           \begin{center}
            |               $ 1+1 $
            |           \end{center}
            |       \end{item}
            |       \begin{item}
            |           \begin{center}
            |               $ 2+2 $
            |           \end{center}
            |       \end{item}
            |    \end{itemize}
            |\end{document}
        """.trimMargin()
    }
}