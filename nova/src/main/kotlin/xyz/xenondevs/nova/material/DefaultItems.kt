package xyz.xenondevs.nova.material

import xyz.xenondevs.nova.item.impl.WrenchBehavior

object DefaultItems {
    
    val WRENCH = NovaMaterialRegistry.registerCoreItem("wrench", WrenchBehavior)
    
    fun init() = Unit
    
}