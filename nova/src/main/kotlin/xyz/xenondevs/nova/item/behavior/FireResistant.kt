package xyz.xenondevs.nova.item.behavior

import xyz.xenondevs.nova.item.NovaItem
import xyz.xenondevs.nova.item.vanilla.VanillaMaterialProperty

class FireResistant : ItemBehavior() {
    
    override fun getVanillaMaterialProperties(): List<VanillaMaterialProperty> {
        return listOf(VanillaMaterialProperty.FIRE_RESISTANT)
    }
    
    companion object : ItemBehaviorFactory<FireResistant>() {
        override fun create(item: NovaItem) = FireResistant()
    }
    
}