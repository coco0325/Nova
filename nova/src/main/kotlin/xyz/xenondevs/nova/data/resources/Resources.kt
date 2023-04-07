package xyz.xenondevs.nova.data.resources

import net.minecraft.resources.ResourceLocation
import org.bukkit.Material
import xyz.xenondevs.nova.data.config.PermanentStorage
import xyz.xenondevs.nova.data.resources.builder.content.armor.info.ArmorTexture
import xyz.xenondevs.nova.data.resources.builder.content.font.FontChar
import xyz.xenondevs.nova.data.resources.model.data.BlockModelData
import xyz.xenondevs.nova.data.resources.model.data.ItemModelData

data class ModelData(
    val item: Map<Material, ItemModelData>? = null,
    val block: BlockModelData? = null,
    val armor: ResourceLocation? = null
)

object Resources {
    
    internal lateinit var modelDataLookup: Map<String, ModelData>
    internal lateinit var armorDataLookup: Map<String, ArmorTexture>
    internal lateinit var guiDataLookup: Map<String, FontChar>
    internal lateinit var wailaDataLookup: Map<String, FontChar>
    internal lateinit var textureIconLookup: Map<String, FontChar>
    internal lateinit var languageLookup: Map<String, Map<String, String>>
    
    internal fun updateModelDataLookup(modelDataLookup: Map<ResourceLocation, ModelData>) {
        this.modelDataLookup = modelDataLookup.mapKeysTo(HashMap()) { it.key.toString() }
        PermanentStorage.store("modelDataLookup", this.modelDataLookup)
    }
    
    internal fun updateArmorDataLookup(armorDataLookup: Map<ResourceLocation, ArmorTexture>) {
        this.armorDataLookup = armorDataLookup.mapKeysTo(HashMap()) { it.key.toString() }
        PermanentStorage.store("armorDataLookup", this.armorDataLookup)
    }
    
    internal fun updateGuiDataLookup(guiDataLookup: Map<String, FontChar>) {
        this.guiDataLookup = guiDataLookup
        PermanentStorage.store("guiDataLookup", guiDataLookup)
    }
    
    internal fun updateWailaDataLookup(wailaDataLookup: Map<String, FontChar>) {
        this.wailaDataLookup = wailaDataLookup
        PermanentStorage.store("wailaDataLookup", wailaDataLookup)
    }
    
    internal fun updateTextureIconLookup(textureIconLookup: Map<String, FontChar>) {
        this.textureIconLookup = textureIconLookup
        PermanentStorage.store("textureIconLookup", textureIconLookup)
    }
    
    internal fun updateLanguageLookup(languageLookup: Map<String, Map<String, String>>) {
        this.languageLookup = languageLookup
        PermanentStorage.store("languageLookup", languageLookup)
    }
    
    fun getModelData(id: ResourceLocation): ModelData {
        return modelDataLookup[id.toString()]!!
    }
    
    fun getModelData(path: ResourcePath): ModelData {
        return modelDataLookup[path.toString()]!!
    }
    
    fun getModelData(id: String): ModelData {
        return modelDataLookup[id]!!
    }
    
    fun getModelDataOrNull(id: ResourceLocation): ModelData? {
        return modelDataLookup[id.toString()]
    }
    
    fun getModelDataOrNull(path: ResourcePath): ModelData? {
        return modelDataLookup[path.toString()]
    }
    
    fun getModelDataOrNull(id: String): ModelData? {
        return modelDataLookup[id]
    }
    
    fun getArmorData(id: ResourceLocation): ArmorTexture {
        return armorDataLookup[id.toString()]!!
    }
    
    fun getArmorData(path: ResourcePath): ArmorTexture {
        return armorDataLookup[path.toString()]!!
    }
    
    fun getArmorData(id: String): ArmorTexture {
        return armorDataLookup[id]!!
    }
    
    fun getArmorDataOrNull(id: ResourceLocation): ArmorTexture? {
        return armorDataLookup[id.toString()]
    }
    
    fun getArmorDataOrNull(path: ResourcePath): ArmorTexture? {
        return armorDataLookup[path.toString()]
    }
    
    fun getArmorDataOrNull(id: String): ArmorTexture? {
        return armorDataLookup[id]
    }
    
    fun getGuiChar(id: ResourceLocation): FontChar {
        return guiDataLookup[id.toString()]!!
    }
    
    fun getGuiChar(path: ResourcePath): FontChar {
        return guiDataLookup[path.toString()]!!
    }
    
    fun getGuiChar(id: String): FontChar {
        return guiDataLookup[id]!!
    }
    
    fun getGuiCharOrNull(id: ResourceLocation): FontChar? {
        return guiDataLookup[id.toString()]
    }
    
    fun getGuiCharOrNull(path: ResourcePath): FontChar? {
        return guiDataLookup[path.toString()]
    }
    
    fun getGuiCharOrNull(id: String): FontChar? {
        return guiDataLookup[id]
    }
    
    fun getWailaIconChar(id: ResourceLocation): FontChar {
        return wailaDataLookup[id.toString()]!!
    }
    
    fun getWailaIconChar(path: ResourcePath): FontChar {
        return wailaDataLookup[path.toString()]!!
    }
    
    fun getWailaIconChar(id: String): FontChar {
        return wailaDataLookup[id]!!
    }
    
    fun getWailaIconCharOrNull(id: ResourceLocation): FontChar? {
        return wailaDataLookup[id.toString()]
    }
    
    fun getWailaIconCharOrNull(path: ResourcePath): FontChar? {
        return wailaDataLookup[path.toString()]
    }
    
    fun getWailaIconCharOrNull(id: String): FontChar? {
        return wailaDataLookup[id]
    }
    
    fun getTextureIconChar(id: ResourceLocation): FontChar {
        return textureIconLookup[id.toString()]!!
    }
    
    fun getTextureIconChar(path: ResourcePath): FontChar {
        return textureIconLookup[path.toString()]!!
    }
    
    fun getTextureIconChar(id: String): FontChar {
        return textureIconLookup[id]!!
    }
    
    fun getTextureIconCharOrNull(id: ResourceLocation): FontChar? {
        return textureIconLookup[id.toString()]
    }
    
    fun getTextureIconCharOrNull(path: ResourcePath): FontChar? {
        return textureIconLookup[path.toString()]
    }
    
    fun getTextureIconCharOrNull(id: String): FontChar? {
        return textureIconLookup[id]
    }
    
}