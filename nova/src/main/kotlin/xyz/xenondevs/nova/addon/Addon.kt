@file:Suppress("LeakingThis")

package xyz.xenondevs.nova.addon

import java.io.File
import java.util.logging.Logger

abstract class Addon {
    
    lateinit var logger: Logger
    lateinit var addonFile: File
    lateinit var dataFolder: File
    lateinit var description: AddonDescription
    
    val registry = AddonRegistryHolder(this)
    
    open fun init() = Unit
    open fun onEnable() = Unit
    open fun onDisable() = Unit
    
}