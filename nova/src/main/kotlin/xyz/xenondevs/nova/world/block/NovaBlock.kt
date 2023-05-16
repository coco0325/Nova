package xyz.xenondevs.nova.world.block

import com.mojang.serialization.Codec
import net.minecraft.resources.ResourceLocation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.nova.data.resources.Resources
import xyz.xenondevs.nova.data.resources.model.data.BlockModelData
import xyz.xenondevs.nova.data.resources.model.data.BlockStateBlockModelData
import xyz.xenondevs.nova.data.resources.model.data.DisplayEntityBlockModelData
import xyz.xenondevs.nova.data.world.block.property.BlockPropertyType
import xyz.xenondevs.nova.data.world.block.state.NovaBlockState
import xyz.xenondevs.nova.item.NovaItem
import xyz.xenondevs.nova.item.options.BlockOptions
import xyz.xenondevs.nova.registry.NovaRegistries
import xyz.xenondevs.nova.world.BlockPos
import xyz.xenondevs.nova.world.block.context.BlockPlaceContext
import java.util.concurrent.CompletableFuture

/**
 * A typealias for function that is invoked when a [Player] tries to place a [NovaBlock].
 * The function takes the [Player], the block's [ItemStack] and the [Location] where the block should be placed and
 * returns a [CompletableFuture] that completes with a [Boolean] indicating whether the block can be placed.
 */
typealias PlaceCheckFun = ((Player, ItemStack, Location) -> CompletableFuture<Boolean>)

/**
 * A typealias for a function that is invoked when a [Player] places a [NovaBlock] at the given [BlockPos].
 * The function takes the [BlockPos] of the placed block and returns a [List] of [BlockPos] that should be occupied by 
 * the multi block as well.
 */
typealias MultiBlockLoader = (BlockPos) -> List<BlockPos>

/**
 * Represents a block type in Nova.
 */
open class NovaBlock internal constructor(
    val id: ResourceLocation,
    val localizedName: String,
    internal val logic: BlockLogic<NovaBlockState>,
    val options: BlockOptions,
    val properties: List<BlockPropertyType<*>>,
    val placeCheck: PlaceCheckFun?,
    val multiBlockLoader: MultiBlockLoader?
) {
    
    /**
     * The [NovaItem] associated with this [NovaBlock].
     */
    var item: NovaItem? = null
    
    /**
     * The [BlockModelData] used for displaying this [NovaBlock] in the world.
     */
    val model: BlockModelData by lazy { Resources.getModelData(id).block!! }
    
    internal val vanillaBlockMaterial: Material
        get() = when (val block = model) {
            is DisplayEntityBlockModelData -> block.hitboxType
            is BlockStateBlockModelData -> block[0].type.material
        }
    
    internal open fun createBlockState(pos: BlockPos): NovaBlockState =
        NovaBlockState(pos, this)
    
    internal open fun createNewBlockState(ctx: BlockPlaceContext): NovaBlockState =
        NovaBlockState(this, ctx)
    
    companion object {
        
        val CODEC: Codec<NovaBlock> = NovaRegistries.BLOCK.byNameCodec()
        
    }
    
}