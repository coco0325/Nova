package xyz.xenondevs.nova.addon.registry

import net.minecraft.resources.ResourceLocation
import xyz.xenondevs.nova.material.NovaItem
import xyz.xenondevs.nova.registry.NovaRegistries
import xyz.xenondevs.nova.tileentity.upgrade.UpgradeType
import xyz.xenondevs.nova.util.set
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface UpgradeTypeRegistry : AddonGetter {
    
    fun <T> upgradeType(name: String, item: NovaItem, icon: NovaItem, valueType: KType): UpgradeType<T> {
        val id = ResourceLocation(addon.description.id, name)
        val upgradeType = UpgradeType<T>(id, item, icon, valueType)
        
        NovaRegistries.UPGRADE_TYPE[id] = upgradeType
        return upgradeType
    }
    
}

inline fun <reified T> UpgradeTypeRegistry.upgradeType(name: String, item: NovaItem, icon: NovaItem): UpgradeType<T> {
    return upgradeType(name, item, icon, typeOf<T>())
}