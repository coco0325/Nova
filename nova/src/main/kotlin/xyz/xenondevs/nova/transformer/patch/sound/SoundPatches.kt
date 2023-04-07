@file:Suppress("unused")

package xyz.xenondevs.nova.transformer.patch.sound

import net.minecraft.core.Holder
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.objectweb.asm.Opcodes
import xyz.xenondevs.bytebase.asm.buildInsnList
import xyz.xenondevs.bytebase.jvm.VirtualClassPath
import xyz.xenondevs.bytebase.util.replaceFirst
import xyz.xenondevs.nova.item.behavior.Wearable
import xyz.xenondevs.nova.transformer.MultiTransformer
import xyz.xenondevs.nova.util.MINECRAFT_SERVER
import xyz.xenondevs.nova.util.forcePacketBroadcast
import xyz.xenondevs.nova.util.item.novaItem
import xyz.xenondevs.nova.util.item.soundGroup
import xyz.xenondevs.nova.util.reflection.ReflectionRegistry
import xyz.xenondevs.nova.util.soundGroup
import xyz.xenondevs.nova.util.toNovaPos
import xyz.xenondevs.nova.world.BlockPos
import xyz.xenondevs.nova.world.block.logic.sound.SoundEngine
import kotlin.math.floor
import kotlin.random.Random
import net.minecraft.core.BlockPos as MojangBlockPos
import net.minecraft.world.entity.Entity as MojangEntity
import net.minecraft.world.entity.LivingEntity as MojangLivingEntity
import net.minecraft.world.item.ItemStack as MojangStack
import net.minecraft.world.level.block.Block as MojangBlock
import net.minecraft.world.level.block.state.BlockState as MojangBlockState

internal object SoundPatches : MultiTransformer(MojangEntity::class, MojangLivingEntity::class, MojangBlock::class, MojangStack::class) {
    
    override fun transform() {
        transformEntityPlayStepSound()
        transformLivingEntityPlayBlockFallSound()
        transformBlockPlayerWillDestroy()
    }
    
    private fun transformEntityPlayStepSound() {
        VirtualClassPath[ReflectionRegistry.ENTITY_PLAY_STEP_SOUND_METHOD]
            .instructions = buildInsnList {
            aLoad(0)
            aLoad(1)
            aLoad(2)
            invokeStatic(::playStepSound)
            _return()
        }
    }
    
    @JvmStatic
    fun playStepSound(entity: MojangEntity, nmsPos: MojangBlockPos, state: MojangBlockState) {
        if (!state.material.isLiquid) {
            val level = entity.level
            val above = level.getBlockState(nmsPos.above())
            val setPos = (if (above.`is`(BlockTags.INSIDE_STEP_SOUND_BLOCKS)) nmsPos.above() else nmsPos).toNovaPos(level.world)
            
            val block = setPos.block
            val soundGroup = block.blockSoundGroup ?: return
            val newSound = soundGroup.stepSound
            val oldSound = block.type.soundGroup.stepSound.key.key
            
            playSound(entity, oldSound, newSound.name, soundGroup.volume, soundGroup.pitch)
        }
    }
    
    private fun transformLivingEntityPlayBlockFallSound() {
        VirtualClassPath[ReflectionRegistry.LIVING_ENTITY_PLAY_BLOCK_FALL_SOUND_METHOD]
            .instructions = buildInsnList {
            aLoad(0)
            invokeStatic(::playFallSound)
            _return()
        }
    }
    
    @JvmStatic
    fun playFallSound(entity: MojangLivingEntity) {
        if (!entity.isSilent) {
            val x = floor(entity.x).toInt()
            val y = floor(entity.y).toInt()
            val z = floor(entity.z).toInt()
            
            val pos = BlockPos(entity.level.world, x, y, z)
            val block = pos.block
            
            if (!block.type.isAir) {
                val soundGroup = block.blockSoundGroup ?: return
                val newSound = soundGroup.fallSound
                val oldSound = block.type.soundGroup.fallSound.key.key
                
                playSound(entity, oldSound, newSound.name, soundGroup.volume, soundGroup.pitch)
            }
        }
    }
    
    @JvmStatic
    fun playSound(entity: MojangEntity, oldSound: String, newSound: String, volume: Float, pitch: Float) {
        val level = entity.level
        val player = if (SoundEngine.overridesSound(oldSound)) null else entity as? Player
        
        val pos = entity.position()
        val packet = ClientboundSoundPacket(
            Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation(newSound))),
            entity.soundSource,
            pos.x,
            pos.y,
            pos.z,
            volume, pitch,
            level.random.nextLong()
        )
        
        MINECRAFT_SERVER.playerList.broadcast(player, entity.x, entity.y, entity.z, 16.0, level.dimension(), packet)
    }
    
    private fun transformBlockPlayerWillDestroy() {
        VirtualClassPath[Block::playerWillDestroy].instructions.replaceFirst(0, 0, buildInsnList {
            aLoad(1)
            aLoad(2)
            invokeStatic(::playBreakSound)
            _return()
        }) { it.opcode == Opcodes.RETURN }
    }
    
    @JvmStatic
    fun playBreakSound(level: Level, pos: MojangBlockPos) {
        val novaPos = pos.toNovaPos(level.world)
        val soundGroup = novaPos.block.blockSoundGroup ?: return
        val oldSound = novaPos.block.type.soundGroup.breakSound.key.key
        
        // send custom break sound if it's overridden
        if (SoundEngine.overridesSound(oldSound)) {
            val pitch = soundGroup.pitch
            val volume = soundGroup.volume
            MINECRAFT_SERVER.playerList.broadcast(
                null,
                pos.x + 0.5, pos.y + 0.5, pos.z + 0.5,
                if (volume > 1.0) 16.0 * volume else 16.0,
                level.dimension(),
                ClientboundSoundPacket(
                    Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation(soundGroup.breakSound.name))),
                    SoundSource.BLOCKS,
                    pos.x + 0.5,
                    pos.y + 0.5,
                    pos.z + 0.5,
                    volume, pitch,
                    Random.nextLong()
                )
            )
        }
    }

    @JvmStatic
    fun getEquipSound(itemStack: MojangStack): SoundEvent? {
        val novaItem = itemStack.novaItem
        if (novaItem != null) {
            val soundEventName = novaItem.getBehavior(Wearable::class)?.options?.equipSound
                ?: return null
            return SoundEvent.createVariableRangeEvent(ResourceLocation.tryParse(soundEventName))
        }
        
        return (itemStack.item as? ArmorItem)?.material?.equipSound
    }
    
    @JvmStatic
    fun playEquipSound(entity: LivingEntity, itemStack: MojangStack) {
        if (itemStack.isEmpty || entity.isSpectator)
            return
        
        val equipSound = (itemStack.item as? ArmorItem)?.material?.equipSound
            ?: return
        
        forcePacketBroadcast { entity.playSound(equipSound, 1f, 1f) }
    }
    
}