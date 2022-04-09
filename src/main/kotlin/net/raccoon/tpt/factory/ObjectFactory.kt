package net.raccoon.tpt.factory

import net.raccoon.tpt.ApplicationContext
import net.raccoon.tpt.Bean

sealed interface ObjectFactory<O>: Comparable<ObjectFactory<*>> {

    fun newObject(context: ApplicationContext): O

    fun getBean(): Bean

    fun finalize() {}

    fun getLayer(): Layer

    override fun compareTo(other: ObjectFactory<*>): Int =
        getLayer().order - other.getLayer().order

    enum class Layer(val order: Int) {
        LOWEST(1),
        MIDDLE(2),
        HIGHEST(3)
        ;
    }

}
