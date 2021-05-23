package de.schnettler.scrobbler.charts.repo.tools

class BaseDaoFake<K, V>(private val keyGen: (V) -> K) {
    private val map: MutableMap<K, V> = mutableMapOf()

    fun insert(value: V?): Long {
        if (value != null) {
            val containedAlready = map.containsKey(keyGen.invoke(value))
            return if (containedAlready) {
                0
            } else {
                forceInsert(value)
            }
        }
        return 0
    }

    fun forceInsert(value: V?): Long {
        return if (value != null) {
            map[keyGen.invoke(value)] = value
            1
        } else {
            0
        }
    }

    fun insertAll(values: List<V?>): List<Long> {
        return values.map { value ->
            if (value != null) {
                insert(value)
            } else {
                0
            }
        }
    }

    fun forceInsertAll(values: List<V>): List<Long> {
        return values.map { value ->
            forceInsert(value)
        }
    }
}