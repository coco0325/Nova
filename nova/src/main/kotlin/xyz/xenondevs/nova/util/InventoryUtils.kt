package xyz.xenondevs.nova.util

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.RecipeChoice
import xyz.xenondevs.invui.inventory.VirtualInventory
import xyz.xenondevs.invui.inventory.event.UpdateReason
import xyz.xenondevs.invui.util.InventoryUtils
import xyz.xenondevs.nova.util.item.takeUnlessEmpty

/**
 * Adds a [List] of [ItemStack]s to a [VirtualInventory].
 */
fun VirtualInventory.addAll(reason: UpdateReason?, items: List<ItemStack>) =
    items.forEach { addItem(reason, it) }

/**
 * Checks if an [Inventory] is full.
 */
fun Inventory.isFull(): Boolean {
    for (item in contents)
        if (item == null || item.amount < item.type.maxStackSize)
            return false
    return true
}

/**
 * Adds an [ItemStack] to an [Inventory] while respecting both
 * the max stack size of the inventory and the max stack size
 * of the item type.
 *
 * Unlike Bukkit's addItem method, the [ItemStack] provided as the
 * method parameter will not be modified.
 *
 * @return The amount of items that did not fit.
 */
fun Inventory.addItemCorrectly(itemStack: ItemStack, blockedSlots: BooleanArray = BooleanArray(size)) =
    InventoryUtils.addItemCorrectly(this, itemStack, blockedSlots)

/**
 * Adds [items] to the [Player's][Player] inventory or drops them on
 * the ground if there is not enough space.
 */
fun Player.addToInventoryOrDrop(items: List<ItemStack>) {
    val inventory = inventory
    items.forEach {
        val leftover = inventory.addItemCorrectly(it)
        if (leftover > 0) {
            val drop = it.clone().apply { amount = leftover }
            InventoryUtils.dropItemLikePlayer(this, drop)
        }
    }
}

/**
 * Checks if this [Inventory] contains all [choices]
 */
fun Inventory.containsAll(choices: List<RecipeChoice>): Boolean {
    val choiceMap = HashMap<RecipeChoice, Int>()
    for (choice in choices) {
        val amount = choiceMap[choice] ?: 0
        choiceMap[choice] = amount + 1
    }
    
    for (item in storageContents) {
        if (item == null) continue
        
        val matchingChoice = choiceMap.keys.firstOrNull { it.test(item) } ?: continue
        val requiredAmount = choiceMap[matchingChoice]!! - item.amount
        
        if (requiredAmount > 0)
            choiceMap[matchingChoice] = requiredAmount
        else choiceMap -= matchingChoice
    }
    
    return choiceMap.isEmpty()
}

/**
 * Removes one item matching the given [choice] and returns it
 */
fun Inventory.takeFirstOccurrence(choice: RecipeChoice): ItemStack? {
    for (item in storageContents) {
        if (item == null) continue
        
        if (choice.test(item)) {
            val itemClone = item.clone()
            item.amount--
            return itemClone.apply { amount = 1 }
        }
    }
    
    return null
}

/**
 * Gets the first [ItemStack] in the [Inventory.getStorageContents]
 * that is similar to [type] and not a full stack.
 */
fun Inventory.getFirstPartialStack(type: ItemStack) = InventoryUtils.getFirstPartialStack(this, type)

/**
 * Gets the first slot index of the [Inventory.getStorageContents]
 * that is completely empty.
 */
fun Inventory.getFirstEmptySlot(): Int? = InventoryUtils.getFirstEmptySlot(this).takeUnless { it == -1 }

/**
 * Puts an [ItemStack] on the [prioritizedSlot] or adds it to the [Inventory][PlayerInventory]
 * if the given slot is occupied.
 */
fun PlayerInventory.addPrioritized(prioritizedSlot: EquipmentSlot, itemStack: ItemStack) {
    if (getItem(prioritizedSlot)?.takeUnlessEmpty() == null) setItem(prioritizedSlot, itemStack)
    else addItem(itemStack)
}

/**
 * Puts an [ItemStack] on the [prioritizedSlot] or adds it to the [Inventory]
 * if the given slot is occupied.
 */
fun Inventory.addPrioritized(prioritizedSlot: Int, itemStack: ItemStack) {
    if (getItem(prioritizedSlot) == null) setItem(prioritizedSlot, itemStack)
    else addItem(itemStack)
}

/**
 * If the [Player] has is currently looking into an inventory.
 * Does not detect the player's inventory itself because that is not sent to the server.
 */
val Player.hasInventoryOpen: Boolean
    get() = openInventory.topInventory.type != InventoryType.CRAFTING

/**
 * Checks if an [InventoryView] is the player inventory
 */
fun InventoryView.isPlayerView() = topInventory is CraftingInventory && topInventory.size == 5

/**
 * A [VirtualInventory] implementation that does not store any items, but voids them.
 */
class VoidingVirtualInventory(size: Int) : VirtualInventory(null, size) {
    override fun setCloneBackingItem(slot: Int, itemStack: ItemStack?) = Unit
    override fun setDirectBackingItem(slot: Int, itemStack: ItemStack?) = Unit
}
