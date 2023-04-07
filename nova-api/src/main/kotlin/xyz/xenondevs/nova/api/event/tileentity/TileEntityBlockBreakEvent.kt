package xyz.xenondevs.nova.api.event.tileentity

import org.bukkit.block.Block
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.nova.api.tileentity.TileEntity

/**
 * Called when a [TileEntity] breaks a block and has passed internal protection checks.
 */
class TileEntityBreakBlockEvent(tileEntity: TileEntity, val block: Block, drops: MutableList<ItemStack>) : TileEntityEvent(tileEntity) {
    
    var drops: MutableList<ItemStack> = drops
        set(value) {
            field.clear()
            field.addAll(value)
        }
    
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
    
    override fun toString(): String {
        return "TileEntityBreakBlockEvent(tileEntity=$tileEntity, block=$block, drops=$drops)"
    }
    
    companion object {
        
        @JvmStatic
        private val HANDLERS = HandlerList()
        
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
        
    }
    
}