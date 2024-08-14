package com.c1nnam0nbun.cinnamon

annotation class SystemConfig(
    val stage: String = "",
    val runOnce: Boolean = false,
    val before: Array<String> = [],
    val after: Array<String> = [],
)

class Commands

@SystemConfig
fun system(commands: Commands) {

}

@SystemConfig
fun immediateSystem(world: World) {

}

class ComponentA : Component<ComponentA> {
    override val type: ComponentID<ComponentA> = ComponentA
    companion object : ComponentID<ComponentA>()
}

class ComponentB : Component<ComponentB> {
    override val type: ComponentID<ComponentB> = ComponentB
    companion object : ComponentID<ComponentB>()
}

fun main() = Application {
    val e = world.createEntity(ComponentA())
    world.addComponent(e, ComponentB())
    world.removeComponent(e, ComponentA)
}