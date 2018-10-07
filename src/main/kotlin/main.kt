package ru.hse.spb

fun main(args: Array<String>) {
    println(document {
        usepackage("lol")

        math("1 + 2")
        itemize {
            item {
                frame("fds") {

                }
            }
        }
        alignment(Alignment.CENTER) {

        }
    }.toString())
}