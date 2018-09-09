package ru.hse.spb

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TestSource {

    @Test
    fun testFindMinAnglePair() {
        var vectors = mutableListOf(Vector(1, 0, 3), Vector(0, 1, 2)) as ArrayList<Vector>
        assertThat(findMinAnglePair(vectors), anyOf(`is`(Pair(3, 2)), `is`(Pair(2, 3))))

        vectors = mutableListOf(Vector(-1, 0, 1),
                Vector(0, -1, 2),
                Vector(1, 0, 3),
                Vector(1, 1, 4),
                Vector(-4, -5, 5),
                Vector(-4, -6, 6)) as ArrayList<Vector>
        assertThat(findMinAnglePair(vectors), anyOf(`is`(Pair(5, 6)), `is`(Pair(6, 5))))
    }
}