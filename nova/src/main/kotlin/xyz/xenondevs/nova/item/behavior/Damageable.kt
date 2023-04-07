package xyz.xenondevs.nova.item.behavior

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.nova.data.serialization.cbf.NamespacedCompound
import xyz.xenondevs.nova.item.NovaItem
import xyz.xenondevs.nova.item.logic.PacketItemData
import xyz.xenondevs.nova.item.options.DamageableOptions
import xyz.xenondevs.nova.item.vanilla.VanillaMaterialProperty
import xyz.xenondevs.nova.util.item.novaCompound
import kotlin.math.min
import net.minecraft.world.item.ItemStack as MojangStack

class Damageable(val options: DamageableOptions) : ItemBehavior() {
    
    @Deprecated("Replaced by DamageableOptions", ReplaceWith("options.durability"))
    val durability: Int by options.durabilityProvider
    
    //<editor-fold desc="Bukkit ItemStack methods", defaultstate="collapsed">
    fun getDamage(itemStack: ItemStack): Int {
        return getDamage(itemStack.novaCompound)
    }
    
    fun setDamage(itemStack: ItemStack, damage: Int) {
        setDamage(itemStack.novaCompound, damage)
    }
    
    fun addDamage(itemStack: ItemStack, damage: Int) {
        addDamage(itemStack.novaCompound, damage)
    }
    
    fun getDurability(itemStack: ItemStack): Int {
        return getDurability(itemStack.novaCompound)
    }
    
    fun setDurability(itemStack: ItemStack, durability: Int) {
        return setDurability(itemStack.novaCompound, durability)
    }
    //</editor-fold>
    
    //<editor-fold desc="Mojang ItemStack methods", defaultstate="collapsed">
    fun getDamage(itemStack: MojangStack): Int {
        return getDamage(itemStack.novaCompound)
    }
    
    fun setDamage(itemStack: MojangStack, damage: Int) {
        setDamage(itemStack.novaCompound, damage)
    }
    
    fun addDamage(itemStack: MojangStack, damage: Int) {
        addDamage(itemStack.novaCompound, damage)
    }
    
    fun getDurability(itemStack: MojangStack): Int {
        return getDurability(itemStack.novaCompound)
    }
    
    fun setDurability(itemStack: MojangStack, durability: Int) {
        return setDurability(itemStack.novaCompound, durability)
    }
    //</editor-fold>
    
    //<editor-fold desc="Compound methods", defaultstate="collapsed">
    fun getDamage(data: NamespacedCompound): Int {
        return min(options.durability, data["nova", "damage"] ?: 0)
    }
    
    fun setDamage(data: NamespacedCompound, damage: Int) {
        val coercedDamage = damage.coerceIn(0..options.durability)
        data["nova", "damage"] = coercedDamage
    }
    
    fun addDamage(data: NamespacedCompound, damage: Int) {
        setDamage(data, getDamage(data) + damage)
    }
    
    fun getDurability(data: NamespacedCompound): Int {
        return options.durability - getDamage(data)
    }
    
    fun setDurability(data: NamespacedCompound, durability: Int) {
        setDamage(data, options.durability - durability)
    }
    //</editor-fold>
    
    override fun updatePacketItemData(data: NamespacedCompound, itemData: PacketItemData) {
        val damage = getDamage(data)
        val durability = options.durability - damage
        
        itemData.durabilityBar = durability / options.durability.toDouble()
        
        itemData.addAdvancedTooltipsLore(
            Component.translatable("item.durability", Component.text(durability), Component.text(options.durability))
        )
    }
    
    override fun getVanillaMaterialProperties(): List<VanillaMaterialProperty> {
        return listOf(VanillaMaterialProperty.DAMAGEABLE)
    }
    
    companion object : ItemBehaviorFactory<Damageable>() {
        override fun create(item: NovaItem) =
            Damageable(DamageableOptions.configurable(item))
    }
    
}