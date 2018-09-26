package ru.hse.spb

public class Scope<T>(var outer: Scope<T>?) {

    private val current = HashMap<String, T>()

    fun getValue(name: String): T? = if (current.containsKey(name)) current[name] else outer?.getValue(name)
    fun addValue(name: String, value: T) {
        current[name] = value
    }

    fun setValue(name: String, value: T) {
        when {
            current.containsKey(name) -> current[name] = value
            outer != null -> outer!!.setValue(name, value)
            else -> throw RuntimeException("Undefined name ${name}")
        }
    }
}