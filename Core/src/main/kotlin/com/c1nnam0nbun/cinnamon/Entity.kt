package com.c1nnam0nbun.cinnamon

@JvmInline
value class Entity(private val identifier: ULong = 0uL) {

    constructor(id: Int, version: Int) : this((id.toULong() shl 32) or version.toULong())

    val id: Int
        get() = (identifier shr 32).toInt()

    val version: Int
        get() = identifier.toInt()

    override fun toString() = "Entity $identifier, id=${id}, version=${version}"

    companion object {
        fun tombstone(): Entity = Entity(ULong.MAX_VALUE)
    }
}

internal class Entities {
    private val entities = mutableListOf<Entity>()
    private var available = 0
    private var next = Entity.tombstone()

    fun create(): Entity {
        if (available > 0) {
            val current = next
            val currentEntity = Entity(next.id, entities[next.id].version)
            next = entities[current.id]
            entities[current.id] = currentEntity
            available -= 1
            return currentEntity
        }

        val entity = Entity(entities.size, 0)
        entities += entity
        return entity
    }

    fun destroy(entity: Entity) {
        if (!isValidEntity(entity)) return
        if (available == 0) {
            next = Entity(entity.id, Int.MAX_VALUE)
            entities[entity.id] = Entity(Int.MAX_VALUE, entity.version + 1)
            available += 1
            return
        }
        val current = next
        next = Entity(entity.id, Int.MAX_VALUE)
        entities[entity.id] = Entity(current.id, entity.version + 1)
        available += 1
    }

    fun isValidEntity(entity: Entity) = entities[entity.id] == entity
}