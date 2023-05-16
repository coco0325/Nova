@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION", "UNCHECKED_CAST")

package xyz.xenondevs.nova.util.item

import com.mojang.brigadier.StringReader
import net.kyori.adventure.text.Component
import net.minecraft.commands.arguments.item.ItemParser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation
import net.minecraft.world.item.ArmorItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.meta.ItemMeta
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.nova.LOGGER
import xyz.xenondevs.nova.addon.Addon
import xyz.xenondevs.nova.data.NamespacedId
import xyz.xenondevs.nova.data.recipe.ComplexTest
import xyz.xenondevs.nova.data.recipe.CustomRecipeChoice
import xyz.xenondevs.nova.data.recipe.ModelDataTest
import xyz.xenondevs.nova.data.recipe.NovaIdTest
import xyz.xenondevs.nova.data.recipe.NovaNameTest
import xyz.xenondevs.nova.data.recipe.TagTest
import xyz.xenondevs.nova.data.serialization.cbf.CBFCompoundTag
import xyz.xenondevs.nova.data.serialization.cbf.NamespacedCompound
import xyz.xenondevs.nova.data.serialization.persistentdata.get
import xyz.xenondevs.nova.integration.customitems.CustomItemServiceManager
import xyz.xenondevs.nova.item.NovaItem
import xyz.xenondevs.nova.item.behavior.Wearable
import xyz.xenondevs.nova.item.vanilla.AttributeModifier
import xyz.xenondevs.nova.registry.NovaRegistries
import xyz.xenondevs.nova.util.bukkitMirror
import xyz.xenondevs.nova.util.component.adventure.toAdventureComponent
import xyz.xenondevs.nova.util.component.adventure.toJson
import xyz.xenondevs.nova.util.data.NBTUtils
import xyz.xenondevs.nova.util.data.getCBFCompoundTag
import xyz.xenondevs.nova.util.data.getOrNull
import xyz.xenondevs.nova.util.data.getOrPut
import xyz.xenondevs.nova.util.data.getOrPutCBFCompoundTag
import xyz.xenondevs.nova.util.get
import xyz.xenondevs.nova.util.name
import xyz.xenondevs.nova.util.nmsCopy
import xyz.xenondevs.nova.util.nmsEquipmentSlot
import xyz.xenondevs.nova.util.reflection.ReflectionRegistry
import java.util.logging.Level
import kotlin.collections.set
import net.minecraft.nbt.Tag as NBTTag
import net.minecraft.world.entity.EquipmentSlot as MojangEquipmentSlot
import net.minecraft.world.item.ItemStack as MojangStack

val ItemStack.novaItem: NovaItem?
    get() = (itemMeta?.unhandledTags?.get("nova") as? CompoundTag)
        ?.getString("id")
        ?.let(NovaRegistries.ITEM::get)

val MojangStack.novaItem: NovaItem?
    get() = tag?.getCompound("nova")
        ?.getString("id")
        ?.let(NovaRegistries.ITEM::get)

val ItemStack.novaMaxStackSize: Int
    get() = novaItem?.maxStackSize ?: type.maxStackSize

val ItemStack.customModelData: Int
    get() {
        if (hasItemMeta()) {
            val itemMeta = itemMeta!!
            if (itemMeta.hasCustomModelData()) return itemMeta.customModelData
        }
        
        return 0
    }

val ItemStack.displayName: String?
    get() {
        if (hasItemMeta()) {
            val itemMeta = itemMeta!!
            return itemMeta.displayName
        }
        
        return null
    }

val ItemStack.localizedName: String?
    get() = novaItem?.localizedName ?: type.localizedName

val ItemStack.namelessCopyOrSelf: ItemStack
    get() {
        var itemStack = this
        if (hasItemMeta()) {
            val itemMeta = itemMeta!!
            if (itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(null)
                itemStack = clone().apply { setItemMeta(itemMeta) }
            }
        }
        
        return itemStack
    }

internal val ItemStack.backingItemMeta: ItemMeta?
    get() {
        var backingMeta = ReflectionRegistry.ITEM_STACK_ITEM_META_FIELD.get(this) as? ItemMeta
        
        if (backingMeta == null) {
            backingMeta = Bukkit.getItemFactory().getItemMeta(type)
            ReflectionRegistry.ITEM_STACK_ITEM_META_FIELD.set(this, backingMeta)
        }
        
        return backingMeta
    }

internal val ItemStack.handle: MojangStack?
    get() {
        if (this is CraftItemStack)
            return ReflectionRegistry.CRAFT_ITEM_STACK_HANDLE_FIELD.get(this) as MojangStack?
        
        return null
    }

val ItemMeta.unhandledTags: MutableMap<String, NBTTag>
    get() = ReflectionRegistry.CRAFT_META_ITEM_UNHANDLED_TAGS_FIELD.get(this) as MutableMap<String, NBTTag>

val ItemStack.canDestroy: List<Material>
    get() {
        val tag = itemMeta?.unhandledTags?.get("CanDestroy") as? ListTag ?: return emptyList()
        return tag.mapNotNull { runCatching { ResourceLocation.of(it.asString, ':') }.getOrNull()?.let { Material.valueOf(it.name) } }
    }

val ItemStack.craftingRemainingItem: ItemStack?
    get() {
        val novaItem = novaItem
        if (novaItem != null)
            return novaItem.craftingRemainingItem?.get()
        
        return type.craftingRemainingItem?.let(::ItemStack)
    }

val ItemStack.equipSound: String?
    get() {
        val novaItem = novaItem
        if (novaItem != null)
            return novaItem.getBehavior(Wearable::class)?.options?.equipSound
        
        return (CraftMagicNumbers.getItem(type) as? ArmorItem)?.material?.equipSound?.location?.toString()
    }

fun ItemStack.isSimilarIgnoringName(other: ItemStack?): Boolean {
    val first = this.namelessCopyOrSelf
    val second = other?.namelessCopyOrSelf
    
    return first.isSimilar(second)
}

fun ItemStack.takeUnlessEmpty(): ItemStack? =
    if (type.isAir || amount <= 0) null else this

internal var MojangStack.adventureName: Component
    get() = tag
        ?.getOrNull<CompoundTag>("display")
        ?.getOrNull<StringTag>("Name")
        ?.asString
        ?.toAdventureComponent()
        ?: Component.empty()
    set(value) {
        orCreateTag.getOrPut("display", ::CompoundTag).putString("Name", value.toJson())
    }

internal var MojangStack.adventureLore: List<Component>
    get() = tag
        ?.getOrNull<CompoundTag>("display")
        ?.getOrNull<ListTag>("Lore")
        ?.map { (it as StringTag).asString.toAdventureComponent() }
        ?: emptyList()
    set(value) {
        orCreateTag.getOrPut("display", ::CompoundTag).put("Lore",  NBTUtils.createStringList(value.map(Component::toJson)))
    }

//<editor-fold desc="nova item data storage", defaultstate="collapsed">

//<editor-fold desc="BukkitStack - Nova Compound", defaultstate="collapsed">
var ItemStack.novaCompound: NamespacedCompound
    get() {
        val handle = handle
        if (handle != null)
            return handle.novaCompound
        
        val unhandledTags = backingItemMeta!!.unhandledTags
        val tag = unhandledTags.getOrPutCBFCompoundTag("nova_cbf", ::CBFCompoundTag) as? CBFCompoundTag
        return tag!!.compound
    }
    set(value) {
        val handle = handle
        if (handle != null) {
            handle.novaCompound = value
        } else {
            backingItemMeta!!.unhandledTags["nova_cbf"] = CBFCompoundTag(value)
        }
    }

val ItemStack.novaCompoundOrNull: NamespacedCompound?
    get() {
        val handle = handle
        if (handle != null)
            return handle.novaCompoundOrNull
        
        return backingItemMeta?.unhandledTags?.getCBFCompoundTag("nova_cbf")?.compound
    }
//</editor-fold>

//<editor-fold desc="MojangStack - Nova Compound", defaultstate="collapsed">
var MojangStack.novaCompound: NamespacedCompound
    get() = orCreateTag.getOrPutCBFCompoundTag("nova_cbf", ::CBFCompoundTag).compound
    set(value) {
        orCreateTag.put("nova_cbf", CBFCompoundTag(value))
    }

val MojangStack.novaCompoundOrNull: NamespacedCompound?
    get() = tag?.getCBFCompoundTag("nova_cbf")?.compound
//</editor-fold>

//<editor-fold desc="BukkitStack - retrieve", defaultstate="collapsed">
inline fun <reified T : Any> ItemStack.retrieveData(namespace: String, key: String): T? {
    // TODO: Remove legacy support at some point
    //<editor-fold desc="legacy support", defaultstate="collapsed">
    val itemMeta = itemMeta
    val dataContainer = itemMeta?.persistentDataContainer
    if (dataContainer != null) {
        val namespacedKey = NamespacedKey(namespace, key)
        val value = dataContainer.get<T>(namespacedKey)
        if (value != null) {
            dataContainer.remove(namespacedKey)
            storeData(namespace, key, value)
            return value
        }
        
        this.itemMeta = itemMeta
    }
    //</editor-fold>
    
    return novaCompoundOrNull?.get(namespace, key)
}

inline fun <reified T : Any> ItemStack.retrieveData(key: NamespacedKey): T? {
    return retrieveData(key.namespace, key.key)
}

inline fun <reified T : Any> ItemStack.retrieveData(id: ResourceLocation): T? {
    return retrieveData(id.namespace, id.path)
}

inline fun <reified T : Any> ItemStack.retrieveData(addon: Addon, key: String): T? {
    return retrieveData(addon.description.id, key)
}
//</editor-fold>

//<editor-fold desc="BukkitStack - store", defaultstate="collapsed">
inline fun <reified T : Any> ItemStack.storeData(namespace: String, key: String, data: T?) {
    // TODO: Remove legacy support at some point
    //<editor-fold desc="legacy support", defaultstate="collapsed">
    val itemMeta = itemMeta
    val dataContainer = itemMeta?.persistentDataContainer
    if (dataContainer != null) {
        dataContainer.remove(NamespacedKey(namespace, key))
        this.itemMeta = itemMeta
    }
    //</editor-fold>
    
    novaCompound[namespace, key] = data
}

inline fun <reified T : Any> ItemStack.storeData(key: NamespacedKey, data: T?) {
    storeData(key.namespace, key.key, data)
}

inline fun <reified T : Any> ItemStack.storeData(id: ResourceLocation, data: T?) {
    storeData(id.namespace, id.path, data)
}

inline fun <reified T : Any> ItemStack.storeData(addon: Addon, key: String, data: T?) {
    storeData(addon.description.id, key, data)
}
//</editor-fold>

//<editor-fold desc="MojangStack - retrieve", defaultstate="collapsed">
inline fun <reified T : Any> MojangStack.retrieveData(namespace: String, key: String): T? {
    // TODO: Remove legacy support at some point
    // For legacy support, we convert the MojangStack to a Bukkit ItemStack to access the persistent data container
    return bukkitMirror.retrieveData(namespace, key)
}

inline fun <reified T : Any> MojangStack.retrieveData(key: NamespacedKey): T? {
    return retrieveData(key.namespace, key.key)
}

inline fun <reified T : Any> MojangStack.retrieveData(id: ResourceLocation): T? {
    return retrieveData(id.namespace, id.path)
}

inline fun <reified T : Any> MojangStack.retrieveData(addon: Addon, key: String): T? {
    return retrieveData(addon.description.id, key)
}
//</editor-fold>

//<editor-fold desc="MojangStack - store", defaultstate="collapsed">
@PublishedApi
internal inline fun <reified T : Any> MojangStack.storeData(namespace: String, key: String, data: T?) {
    // TODO: Remove legacy support at some point
    // For legacy support, we convert the MojangStack to a CraftItemStack to access the persistent data container
    bukkitMirror.storeData(namespace, key, data)
}

inline fun <reified T : Any> MojangStack.storeData(key: NamespacedKey, data: T?) {
    storeData(key.namespace, key.key, data)
}

inline fun <reified T : Any> MojangStack.storeData(id: ResourceLocation, data: T?) {
    storeData(id.namespace, id.path, data)
}

inline fun <reified T : Any> MojangStack.storeData(addon: Addon, key: String, data: T?) {
    storeData(addon.description.id, key, data)
}
//</editor-fold>

//</editor-fold>

//<editor-fold desc="deprecated", defaultstate="collapsed">
@Deprecated("Replaced by ItemStack.takeUnlessEmpty", ReplaceWith("takeUnlessEmpty()"))
fun ItemStack.takeUnlessAir(): ItemStack? =
    if (type.isAir) null else this
//</editor-fold>

object ItemUtils {
    
    fun isIdRegistered(id: String): Boolean {
        try {
            val nid = NamespacedId.of(id, "minecraft")
            return when (nid.namespace) {
                "minecraft" -> runCatching { Material.valueOf(nid.name.uppercase()) }.isSuccess
                "nova" -> NovaRegistries.ITEM.getByName(nid.name).isNotEmpty()
                else -> NovaRegistries.ITEM[id] != null || CustomItemServiceManager.getItemByName(id) != null
            }
        } catch (ignored: Exception) {
        }
        
        return false
    }
    
    fun getRecipeChoice(nameList: List<String>): RecipeChoice {
        val tests = nameList.map { id ->
            try {
                if (id.startsWith("#")) {
                    val tagName = NamespacedKey.fromString(id.substringAfter('#'))
                        ?: throw IllegalArgumentException("Malformed tag: $id")
                    val tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, tagName, Material::class.java)
                        ?: throw IllegalArgumentException("Invalid tag: $id")
                    return@map TagTest(tag)
                }
                
                if (id.contains("{"))
                    return@map ComplexTest(toItemStack(id))
                
                when (id.substringBefore(':')) {
                    "minecraft" -> {
                        val material = Material.valueOf(id.drop(10).uppercase())
                        return@map ModelDataTest(material, intArrayOf(0), ItemStack(material))
                    }
                    
                    "nova" -> {
                        val name = id.substringAfter(':')
                        val novaItems = NovaRegistries.ITEM.getByName(name)
                        if (novaItems.isNotEmpty()) {
                            return@map NovaNameTest(name, novaItems.map { it.createItemStack() })
                        } else throw IllegalArgumentException("Not an item name in Nova: $name")
                    }
                    
                    else -> {
                        val novaItems = NovaRegistries.ITEM[id]
                        if (novaItems != null) {
                            return@map NovaIdTest(id, novaItems.createItemStack())
                        } else {
                            return@map CustomItemServiceManager.getItemTest(id)!!
                        }
                    }
                }
            } catch (ex: Exception) {
                throw IllegalArgumentException("Unknown item $id", ex)
            }
        }
        
        return CustomRecipeChoice(tests)
    }
    
    fun getItemBuilder(id: String, basicClientSide: Boolean = false): ItemBuilder {
        try {
            return when (id.substringBefore(':')) {
                "minecraft" -> ItemBuilder(toItemStack(id))
                "nova" -> {
                    val name = id.substringAfter(':')
                    val novaItems = NovaRegistries.ITEM.getByName(name).first()
                    
                    if (basicClientSide) novaItems.model.createClientsideItemBuilder()
                    else novaItems.createItemBuilder()
                }
                
                else -> {
                    val novaItem = NovaRegistries.ITEM[id]
                    if (novaItem != null) {
                        if (basicClientSide) novaItem.model.createClientsideItemBuilder()
                        else novaItem.createItemBuilder()
                    } else CustomItemServiceManager.getItemByName(id)!!.let(::ItemBuilder)
                }
            }
        } catch (ex: Exception) {
            throw IllegalArgumentException("Invalid item name: $id", ex)
        }
    }
    
    fun getItemAndLocalizedName(id: String): Pair<ItemStack, String> {
        val itemStack: ItemStack
        val localizedName: String
        
        try {
            when (id.substringBefore(':')) {
                "minecraft" -> {
                    itemStack = toItemStack(id)
                    localizedName = itemStack.type.localizedName!!
                }
                
                "nova" -> {
                    val name = id.substringAfter(':')
                    val novaItem = NovaRegistries.ITEM.getByName(name).first()
                    itemStack = novaItem.createItemStack()
                    localizedName = novaItem.localizedName
                }
                
                else -> {
                    val novaItem = NovaRegistries.ITEM[id]
                    if (novaItem != null) {
                        localizedName = novaItem.localizedName
                        itemStack = novaItem.createItemStack()
                    } else {
                        itemStack = CustomItemServiceManager.getItemByName(id)!!
                        localizedName = itemStack.displayName ?: ""
                    }
                }
            }
        } catch (ex: Exception) {
            throw IllegalArgumentException("Invalid item name: $id", ex)
        }
        
        return itemStack to localizedName
    }
    
    fun toItemStack(s: String): ItemStack {
        val holder = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), StringReader(s))
        val nmsStack = MojangStack(holder.item, 1).apply { tag = holder.nbt }
        return CraftItemStack.asBukkitCopy(nmsStack)
    }
    
    fun getId(itemStack: ItemStack): String {
        val novaItem = itemStack.novaItem
        if (novaItem != null) return novaItem.id.toString()
        
        val customNameKey = CustomItemServiceManager.getId(itemStack)
        if (customNameKey != null) return customNameKey
        
        return "minecraft:${itemStack.type.name.lowercase()}"
    }
    
    /**
     * Gets the custom attribute modifiers that are configured in the NBT-data of this [ItemStack] for the given [slot].
     */
    fun getCustomAttributeModifiers(itemStack: ItemStack, slot: EquipmentSlot?): List<AttributeModifier> {
        return getCustomAttributeModifiers(itemStack.nmsCopy, slot?.nmsEquipmentSlot)
    }
    
    /**
     * Gets the custom attribute modifiers that are configured in the NBT-data of this [ItemStack] for the given [slot].
     */
    internal fun getCustomAttributeModifiers(itemStack: MojangStack, slot: MojangEquipmentSlot?): List<AttributeModifier> {
        val tag = itemStack.tag ?: return emptyList()
        return getCustomAttributeModifiers(tag, slot)
    }
    
    /**
     * Gets the custom attribute modifiers that are configured in the given [tag] for the given [slot].
     * The provided [tag] must be the item tag, not the item stack itself.
     */
    internal fun getCustomAttributeModifiers(tag: CompoundTag, slot: MojangEquipmentSlot?): List<AttributeModifier> {
        if (tag.contains("AttributeModifiers", NBTTag.TAG_LIST.toInt())) {
            val attributeModifiers = ArrayList<AttributeModifier>()
            
            tag.getList("AttributeModifiers", NBTTag.TAG_COMPOUND.toInt()).forEach { modifier ->
                modifier as CompoundTag
                
                try {
                    val modifierSlot = modifier.getString("Slot").takeUnless(String::isBlank)
                    
                    if (slot == null || modifierSlot == null || modifierSlot.equals(slot.name, true)) {
                        val slots = modifierSlot
                            ?.let { MojangEquipmentSlot.valueOf(it.uppercase()) }
                            ?.let { arrayOf(it) }
                            ?: MojangEquipmentSlot.values()
                        
                        val attribute = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.tryParse(modifier.getString("AttributeName")))
                            ?: return@forEach
                        
                        val name = modifier.getString("Name")
                        val amount = modifier.getDouble("Amount")
                        val operation = Operation.fromValue(modifier.getInt("Operation"))
                        val uuid = modifier.getUUID("UUID")
                            .takeUnless { it.mostSignificantBits == 0L && it.leastSignificantBits == 0L }
                            ?: return@forEach
                        
                        attributeModifiers += AttributeModifier(uuid, name, attribute, operation, amount, true, *slots)
                    }
                } catch (e: Exception) {
                    LOGGER.log(Level.WARNING, "Could not read attribute modifier: $modifier", e)
                }
            }
            
            return attributeModifiers
        }
        
        return emptyList()
    }
    
}