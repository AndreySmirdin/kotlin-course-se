package ru.hse.spb

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sign


class Vector(private val x: Long, private val y: Long, val id: Int) : Comparable<Vector> {
    private val angle = Math.atan2(x.toDouble(), y.toDouble())

    override fun compareTo(other: Vector): Int {
        return angle.compareTo(other.angle)
    }

    fun pseudoscalarProduct(other: Vector) = x * other.y - y * other.x
    fun scalarProduct(other: Vector) = x * other.x + y * other.y
}

class Angle(val v1: Vector, val v2: Vector) : Comparable<Angle> {
    override fun compareTo(other: Angle): Int {
        val scalar1 = v1.scalarProduct(v2)
        val pseudo1 = -v1.pseudoscalarProduct(v2)
        val scalar2 = other.v1.scalarProduct(other.v2)
        val pseudo2 = -other.v1.pseudoscalarProduct(other.v2)
        return (scalar1 * pseudo2 - scalar2 * pseudo1).sign
    }
}

fun addAngle(v1: Vector, v2: Vector, angles: ArrayList<Angle>) {
    val pseudo = v1.pseudoscalarProduct(v2)
    angles.add(if (pseudo < 0) Angle(v1, v2) else Angle(v2, v1))
}

fun findMinAnglePair(vectors: ArrayList<Vector>): Pair<Int, Int> {
    vectors.sort()

    val angles = ArrayList<Angle>()
    for (i in vectors.indices) {
        if (i == vectors.size - 1)
            continue
        addAngle(vectors[i], vectors[i + 1], angles)
    }
    addAngle(vectors.first(), vectors.last(), angles)
    val best = angles.max()
    return Pair(best!!.v1.id, best.v2.id)
}


fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val n = input.nextInt()
    val vectors = ArrayList<Vector>(n)
    var x: Long
    var y: Long

    for (i in 1..n) {
        x = input.nextLong()
        y = input.nextLong()
        vectors.add(Vector(x, y, i))
    }

    val answer = findMinAnglePair(vectors)
    println("${answer.first} ${answer.second}")
}
