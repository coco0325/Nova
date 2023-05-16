@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.nova.data.serialization.yaml

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import xyz.xenondevs.cbf.CBF
import xyz.xenondevs.commons.reflection.classifierClass
import xyz.xenondevs.nova.data.serialization.yaml.serializer.BarMatcherSerializer
import xyz.xenondevs.nova.data.serialization.yaml.serializer.PotionEffectSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

private val NUMBER_CONVERTER_MAP: Map<KClass<*>, (Number) -> Number> = mapOf(
    Byte::class to { it.toByte() },
    Short::class to { it.toShort() },
    Int::class to { it.toInt() },
    Long::class to { it.toLong() },
    Float::class to { it.toFloat() },
    Double::class to { it.toDouble() }
)

/**
 * Serializes the given [value] of the reified type [T] to a [Map] and sets it under
 * the given [path] in the [ConfigurationSection].
 */
inline fun <reified T : Any> ConfigurationSection.setSerialized(path: String, value: T) {
    setSerialized(path, value, typeOf<T>())
}

/**
 * Serializes the given [value] of [type] to a [Map] and sets it under the given [path]
 * in the [ConfigurationSection].
 */
fun ConfigurationSection.setSerialized(path: String, value: Any, type: KType) {
    set(path, YamlSerialization.serialize(value, type) ?: value)
}

/**
 * Retrieves the value at the given [path] in the [ConfigurationSection] and deserializes it
 * to the reified type [T].
 */
inline fun <reified T : Any> ConfigurationSection.getDeserialized(path: String): T? {
    return getDeserialized(path, typeOf<T>())
}

/**
 * Retrieves the value at the given [path] in the [ConfigurationSection] and deserializes it
 * to the given [type].
 */
fun <T : Any> ConfigurationSection.getDeserialized(path: String, type: KType): T? {
    val value = get(path) ?: return null
    return YamlSerialization.deserialize(value, type)
}

internal object YamlSerialization {
    
    private val serializers = HashMap<KClass<*>, YamlSerializer<*>>()
    
    init {
        registerSerializer(PotionEffectSerializer)
        registerSerializer(BarMatcherSerializer)
    }
    
    private inline fun <reified T> registerSerializer(serializer: YamlSerializer<T>) {
        serializers[T::class] = serializer
    }
    
    inline fun <reified T : Any> serialize(value: T): Map<String, Any>? {
        return serialize(value, typeOf<T>())
    }
    
    fun serialize(value: Any, type: KType): Map<String, Any>? {
        // TODO: improved type support
        val serializer = getSerializer<Any>(type.classifierClass!!)
        if (serializer != null) {
            return serializer.serialize(value)
        }
        
        if (value is ConfigurationSerializable) {
            return value.serialize()
        }
        
        return null
    }
    
    inline fun <reified T : Any> deserialize(value: T): T {
        return deserialize(value, typeOf<T>())
    }
    
    fun <T : Any> deserialize(value: Any, type: KType): T {
        val clazz = type.classifierClass!!
        
        // if value is a collection, also deserialize the elements
        if (value is Collection<*> && clazz.isSubclassOf(List::class)) {
            return deserializeCollectionEntries(value, type)
        }
        
        // if value is a map, also deserialize the entries
        if (value is Map<*, *> && clazz.isSubclassOf(Map::class)) {
            return deserializeMapEntries(value, type)
        }
        
        // value and type are the same
        if (value::class == clazz || value::class.isSubclassOf(clazz)) {
            return value as T
        }
        
        // value is a different number type
        if (value::class.isSubclassOf(Number::class) && clazz.isSubclassOf(Number::class)) {
            val numberConverter = NUMBER_CONVERTER_MAP[clazz] as (Number) -> T
            return numberConverter.invoke(value as Number)
        }
        
        // value requires deserializer
        if (value is Map<*, *>) {
            value as Map<String, Any>
            
            val novaSerializer = getSerializer<T>(clazz)
            if (novaSerializer != null)
                return novaSerializer.deserialize(value)
            
            if (clazz.isSubclassOf(ConfigurationSerializable::class))
                return ConfigurationSerialization.deserializeObject(value, clazz.java as Class<out ConfigurationSerializable>) as T
        }
        
        // failed to deserialize value
        throw IllegalArgumentException("Value $value cannot be deserialized to type $type")
    }
    
    private fun <T> deserializeCollectionEntries(value: Collection<*>, type: KType): T {
        val listType = type.arguments[0].type!!
        val dest = CBF.createInstance<MutableCollection<Any?>>(type) ?: ArrayList()
        value.forEach { if (it != null) dest += deserialize<Any>(it, listType) else dest += null }
        return dest as T
    }
    
    private fun <T> deserializeMapEntries(value: Map<*, *>, type: KType): T {
        val typeArgs = type.arguments
        val keyType = typeArgs[0].type!!
        val valueType = typeArgs[1].type!!
        
        val dest = CBF.createInstance<MutableMap<Any?, Any?>>(type) ?: HashMap()
        value.forEach { (key, value) ->
            val newKey = if (key != null) deserialize<Any>(key, keyType) else null
            val newValue = if (value != null) deserialize<Any>(value, valueType) else null
            
            dest[newKey] = newValue
        }
        
        return dest as T
    }
    
    private fun <T : Any> getSerializer(clazz: KClass<*>): YamlSerializer<T>? {
        return serializers[clazz] as? YamlSerializer<T>
    }
    
}

internal interface YamlSerializer<T> {
    
    fun serialize(value: T): MutableMap<String, Any>
    
    fun deserialize(map: Map<String, Any>): T
    
}