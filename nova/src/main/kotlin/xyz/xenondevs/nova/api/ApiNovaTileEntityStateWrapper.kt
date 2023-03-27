package xyz.xenondevs.nova.api

import org.bukkit.Location
import xyz.xenondevs.nova.api.block.NovaBlock
import xyz.xenondevs.nova.api.tileentity.TileEntity
import xyz.xenondevs.nova.data.world.block.state.NovaTileEntityState
import xyz.xenondevs.nova.api.block.NovaTileEntityState as INovaTileEntityState

internal class ApiNovaTileEntityStateWrapper(state: NovaTileEntityState): INovaTileEntityState {
    /**
     * The tile-entity represented by this [NovaTileEntityState].
     */
    override val tileEntity: TileEntity = ApiTileEntityWrapper(state.tileEntity)
    
    /**
     * The material of this [NovaBlockState].
     */
    override val material: NovaBlock = ApiBlockWrapper(state.material)
    
    /**
     * The location of this [NovaBlockState].
     */
    override val location: Location = state.location
}