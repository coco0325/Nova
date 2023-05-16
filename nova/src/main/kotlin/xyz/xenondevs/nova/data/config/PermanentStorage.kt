package xyz.xenondevs.nova.data.config

import com.google.gson.JsonObject
import xyz.xenondevs.commons.gson.fromJson
import xyz.xenondevs.commons.gson.parseJson
import xyz.xenondevs.nova.data.serialization.json.GSON
import java.io.File

internal object PermanentStorage {
    
    private val file: File = File("plugins/Nova/storage.do-not-edit").also { it.parentFile.mkdirs() }
    val mainObj: JsonObject
    
    init {
        val legacyFile = File("plugins/Nova/storage.json")
        if (!file.exists() && legacyFile.exists())
            legacyFile.renameTo(file)
        
        mainObj = if (file.exists())
            file.parseJson() as JsonObject
        else JsonObject()
    }
    
    fun store(key: String, data: Any?) {
        if (data != null)
            mainObj.add(key, GSON.toJsonTree(data))
        else mainObj.remove(key)
        
        file.writeText(GSON.toJson(mainObj))
    }
    
    fun has(key: String): Boolean {
        return mainObj.has(key)
    }
    
    fun remove(key: String) {
        mainObj.remove(key)
        file.writeText(GSON.toJson(mainObj))
    }
    
    inline fun <reified T> retrieve(key: String, alternativeProvider: () -> T): T {
        return retrieveOrNull(key) ?: alternativeProvider()
    }
    
    inline fun <reified T> retrieveOrNull(key: String): T? {
        return GSON.fromJson<T>(mainObj.get(key))
    }
    
}