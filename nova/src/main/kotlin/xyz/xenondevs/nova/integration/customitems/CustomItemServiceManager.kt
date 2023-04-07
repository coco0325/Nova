package xyz.xenondevs.nova.integration.customitems

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.nova.data.NamespacedId
import xyz.xenondevs.nova.data.recipe.SingleItemTest
import xyz.xenondevs.nova.data.resources.ResourcePath
import xyz.xenondevs.nova.initialize.InitFun
import xyz.xenondevs.nova.initialize.InitializationStage
import xyz.xenondevs.nova.initialize.InternalInit
import xyz.xenondevs.nova.integration.InternalIntegration
import xyz.xenondevs.nova.integration.customitems.plugin.ItemsAdder
import xyz.xenondevs.nova.integration.customitems.plugin.MMOItems
import xyz.xenondevs.nova.integration.customitems.plugin.Oraxen

@InternalInit(stage = InitializationStage.POST_WORLD_ASYNC)
object CustomItemServiceManager {
    
    internal val PLUGINS: List<CustomItemService> by lazy { listOf(ItemsAdder, Oraxen, MMOItems).filter(InternalIntegration::isInstalled) }
    
    @InitFun
    private fun init() {
        PLUGINS.forEach(CustomItemService::awaitLoad)
    }
    
    fun placeBlock(item: ItemStack, location: Location, playSound: Boolean): Boolean {
        return PLUGINS.any { it.placeBlock(item, location, playSound) }
    }
    
    fun removeBlock(block: Block, breakEffects: Boolean): Boolean {
        return PLUGINS.any { it.removeBlock(block, breakEffects) }
    }
    
    fun getDrops(block: Block, tool: ItemStack?): List<ItemStack>? {
        return PLUGINS.firstNotNullOfOrNull { it.getDrops(block, tool) }
    }
    
    fun getItemType(item: ItemStack): CustomItemType? {
        return PLUGINS.firstNotNullOfOrNull { it.getItemType(item) }
    }
    
    fun getBlockType(block: Block): CustomBlockType? {
        return PLUGINS.firstNotNullOfOrNull { it.getBlockType(block) }
    }
    
    fun getItemByName(name: String): ItemStack? {
        return PLUGINS.firstNotNullOfOrNull { it.getItemById(name) }
    }
    
    fun getItemTest(name: String): SingleItemTest? {
        return PLUGINS.firstNotNullOfOrNull { it.getItemTest(name) }
    }
    
    fun getId(item: ItemStack): String? {
        return PLUGINS.firstNotNullOfOrNull { it.getId(item) }
    }
    
    fun getId(block: Block): String? {
        return PLUGINS.firstNotNullOfOrNull { it.getId(block) }
    }
    
    fun getName(item: ItemStack, locale: String): Component? {
        return PLUGINS.firstNotNullOfOrNull { it.getName(item, locale) }
    }
    
    fun getName(block: Block, locale: String): Component? {
        return PLUGINS.firstNotNullOfOrNull { it.getName(block, locale) }
    }
    
    fun hasRecipe(key: NamespacedKey): Boolean {
        return PLUGINS.any { it.hasRecipe(key) }
    }
    
    fun canBreakBlock(block: Block, tool: ItemStack?): Boolean? {
        return PLUGINS.firstNotNullOfOrNull { it.canBreakBlock(block, tool) }
    }
    
    fun getBlockItemModelPaths(): Map<NamespacedId, ResourcePath> {
        val map = HashMap<NamespacedId, ResourcePath>()
        PLUGINS.forEach { map += it.getBlockItemModelPaths() }
        return map
    }
    
}