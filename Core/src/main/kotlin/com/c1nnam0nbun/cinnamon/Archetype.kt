package com.c1nnam0nbun.cinnamon

import kotlin.math.max

internal class Archetypes {
    private val archetypes = mutableMapOf<Int, Archetype>()

    fun addArchetype(archetype: Archetype) {
        val key = hash(archetype.components)
        check (archetypes[key] == null) { "Hash key must be unique, we might need to find another algorithm" }
        archetypes[key] = archetype
    }

    fun getArchetype(components: List<Int>): Archetype? {
        val key = hash(components)
        return archetypes[key]
    }

    private fun hash(components: List<Int>): Int {
        if (components.isEmpty()) return 1
        return components.reduce { acc, id -> max(1, acc) * (1779033703 + 2 * id) }
    }
}

class Archetype internal constructor(
    val components: List<Int> = emptyList()
) {
    private val entities = mutableListOf<Entity>()
    private val edges = SparseSet<Edge>()

    internal operator fun plusAssign(entity: Entity) {
        entities += entity
    }

    internal operator fun minusAssign(entity: Entity) {
        entities -= entity
    }

    internal fun getNextAdd(component: Int, archetypes: Archetypes): Archetype {
        val edge = edges.get(component)
        if (edge == null) {
            val nextComponents = components + component
            var next = archetypes.getArchetype(nextComponents)
            if (next == null) {
                next = Archetype(nextComponents)
                archetypes.addArchetype(next)
            }
            edges.insert(component, Edge(add = next, remove = null))
            val nextEdge = next.edges.get(component)
            if (nextEdge == null) {
                next.edges.insert(component, Edge(add = null, remove = this))
            } else {
                next.edges.insert(component, Edge(add = nextEdge.add, remove = this))
            }
            return next
        }
        return edge.add!!
    }

    internal fun getNextRemove(component: Int, archetypes: Archetypes): Archetype {
        val edge = edges.get(component)
        if (edge == null) {
            val nextComponents = components - component
            var next = archetypes.getArchetype(nextComponents)
            if (next == null) {
                next = Archetype(nextComponents)
                archetypes.addArchetype(next)
            }
            edges.insert(component, Edge(add = null, remove = next))
            val nextEdge = next.edges.get(component)
            if (nextEdge == null) {
                next.edges.insert(component, Edge(add = this, remove = null))
            } else {
                next.edges.insert(component, Edge(add = this, remove = nextEdge.remove))
            }
            return next
        }
        return edge.remove!!
    }
}

private class Edge(
    val add: Archetype?,
    val remove: Archetype?
)