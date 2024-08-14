package com.c1nnam0nbun.cinnamon

const val tombstone = -1

internal class SparseSet<T : Any> {
    private val dense = mutableListOf<T>()
    private val sparse = mutableListOf<Int>()
    private val indices = mutableListOf<Int>()

    val keys get() = indices.toList()

    fun insert(index: Int, value: T) {
        if (index in this) {
            dense[sparse[index]] = value
            return
        }
        if (index >= sparse.size) sparse.addAll(List(index - sparse.size + 1) { tombstone })
        val position = dense.size
        dense += value
        indices += index
        sparse[index] = position
    }

    fun get(index: Int): T? {
        if (index !in this) return null
        return dense[sparse[index]]
    }

    fun remove(index: Int) {
        val last = indices.last()
        indices.swapWithLastAndRemove(sparse[index])
        dense.swapWithLastAndRemove(sparse[index])
        sparse.swap(last, index)
        sparse[index] = tombstone
    }

    operator fun contains(index: Int): Boolean {
        return index < sparse.size && sparse[index] < indices.size && indices[sparse[index]] == index
    }

    private fun<T> MutableList<T>.swapWithLastAndRemove(b: Int) {
        swap(lastIndex, b)
        removeLast()
    }

    private fun<T> MutableList<T>.swap(a: Int, b: Int) {
        val t = this[a]
        this[a] = this[b]
        this[b] = t
    }
}