package xyz.xenondevs.nova.ui.waila.info.impl

import net.minecraft.resources.ResourceLocation
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Lantern
import org.bukkit.entity.Player
import xyz.xenondevs.nova.ui.waila.info.VanillaWailaInfoProvider
import xyz.xenondevs.nova.ui.waila.info.WailaInfo

internal object LanternWailaInfoProvider : VanillaWailaInfoProvider(setOf(Material.LANTERN, Material.SOUL_LANTERN)) {
    
    override fun getInfo(player: Player, block: Block): WailaInfo {
        val info = DefaultVanillaWailaInfoProvider.getInfo(player, block)
        val lantern = block.blockData as Lantern
        if (lantern.isHanging) {
            info.icon = ResourceLocation("minecraft", block.type.name.lowercase() + "_hanging")
        }
        return info
    }
    
}