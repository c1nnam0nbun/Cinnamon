package com.c1nnam0nbun.cinnamon

interface Component<T> {
    val type: ComponentID<T>
}

open class ComponentID<T> {
    private val _id = counter++
    val id get() = _id

    companion object {
        private var counter = 0
    }
}

internal typealias Components = SparseSet<SparseSet<Component<*>>>

class World {
    private val archetypes = Archetypes()
    private val components = Components()
    private val entities = Entities()

    private val entityArchetypeMap = SparseSet<Archetype>()
    private val nullArchetype = Archetype()

    init {
        archetypes.addArchetype(nullArchetype)
    }

    fun createEntity(): Entity {
        val entity = entities.create()
        nullArchetype += entity
        entityArchetypeMap.insert(entity.id, nullArchetype)
        return entity
    }

    fun createEntity(vararg components: Component<*>): Entity {
        val entity = entities.create()
        val componentIDs = components.map { it.type.id }.sortedBy { it }.distinct()
        var current = nullArchetype
        for (id in componentIDs) {
            current = current.getNextAdd(id, archetypes)
        }
        val entityComponents = this.components.get(entity.id) ?: run {
            val set = SparseSet<Component<*>>()
            this.components.insert(entity.id, set)
            return@run set
        }

        for (component in components) {
            entityComponents.insert(component.type.id, component)
        }

        current += entity
        entityArchetypeMap.insert(entity.id, current)
        return entity
    }

    fun addComponent(entity: Entity, component: Component<*>) {
        if (!entities.isValidEntity(entity)) return
        val archetype = entityArchetypeMap.get(entity.id)!!
        val next = archetype.getNextAdd(component.type.id, archetypes)
        val entityComponents = this.components.get(entity.id)!!
        entityComponents.insert(component.type.id, component)
        archetype -= entity
        next += entity
        entityArchetypeMap.insert(entity.id, archetype)
    }

    fun removeComponent(entity: Entity, component: Component<*>) = removeComponent(entity, component.type)

    fun removeComponent(entity: Entity, component: ComponentID<*>) {
        if (!entities.isValidEntity(entity)) return
        val archetype = entityArchetypeMap.get(entity.id)!!
        val next = archetype.getNextRemove(component.id, archetypes)
        val entityComponents = this.components.get(entity.id)!!
        entityComponents.remove(component.id)
        archetype -= entity
        next += entity
        entityArchetypeMap.insert(entity.id, archetype)
    }
}