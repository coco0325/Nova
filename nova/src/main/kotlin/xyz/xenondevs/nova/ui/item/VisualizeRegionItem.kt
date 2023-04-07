package xyz.xenondevs.nova.ui.item

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.impl.AbstractItem
import xyz.xenondevs.nova.item.DefaultGuiItems
import xyz.xenondevs.nova.util.playClickSound
import xyz.xenondevs.nova.world.region.Region
import xyz.xenondevs.nova.world.region.VisualRegion
import java.util.*

class VisualizeRegionItem(
    private val player: Player,
    private val regionUUID: UUID,
    private val getRegion: () -> Region,
) : AbstractItem() {
    
    override fun getItemProvider(): ItemProvider {
        val visible = VisualRegion.isVisible(player, regionUUID)
        return if (visible) DefaultGuiItems.AREA_BTN_ON.clientsideProvider
        else DefaultGuiItems.AREA_BTN_OFF.clientsideProvider
    }
    
    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
        player.playClickSound()
        VisualRegion.toggleView(player, regionUUID, getRegion())
        notifyWindows()
    }
    
}