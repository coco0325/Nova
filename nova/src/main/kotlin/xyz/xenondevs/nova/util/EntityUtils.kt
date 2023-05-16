package xyz.xenondevs.nova.util

import com.mojang.authlib.GameProfile
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.FluidTags
import net.minecraft.world.effect.MobEffectInstance
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R3.CraftServer
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import xyz.xenondevs.nova.util.data.NBTUtils
import xyz.xenondevs.nova.world.block.logic.`break`.BlockBreaking
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import net.minecraft.world.entity.Entity as MojangEntity
import net.minecraft.world.entity.EntityType as NMSEntityType

/**
 * The current block destroy progress of the player.
 * Between 0 and 1 or null if the player is not breaking a block at the moment.
 */
val Player.destroyProgress: Double?
    get() = BlockBreaking.getBreaker(this)?.progress?.coerceAtMost(1.0)

/**
 * Swings the [hand] of the player.
 * @throws IllegalArgumentException If the [hand] is not a valid hand.
 */
fun Player.swingHand(hand: EquipmentSlot) {
    when (hand) {
        EquipmentSlot.HAND -> swingMainHand()
        EquipmentSlot.OFF_HAND -> swingOffHand()
        else -> throw IllegalArgumentException("EquipmentSlot is not a hand")
    }
}

/**
 * Teleports the [Entity] after modifying its location using the [modifyLocation] lambda.
 */
fun Entity.teleport(modifyLocation: Location.() -> Unit) {
    val location = location
    location.modifyLocation()
    teleport(location)
}

/**
 * The translation key for the name of this [Entity]. 
 */
val Entity.localizedName: String?
    get() = (this as CraftEntity).handle.type.descriptionId

/**
 * If the [Entity's][Entity] eye is underwater.
 */
val Entity.eyeInWater: Boolean
    get() = (this as CraftEntity).handle.isEyeInFluid(FluidTags.WATER)

object EntityUtils {
    
    internal val DUMMY_PLAYER = createFakePlayer(Location(Bukkit.getWorlds()[0], 0.0, 0.0, 0.0), UUID.randomUUID(), "Nova Dummy Player")
    
    /**
     * Gets a list of all passengers of this [Entity], including passengers of passengers.
     */
    fun getAllPassengers(entity: Entity): List<Entity> {
        val entitiesToCheck = CopyOnWriteArrayList<Entity>().apply { add(entity) }
        val passengers = ArrayList<Entity>()
        
        while (entitiesToCheck.isNotEmpty()) {
            for (entityToCheck in entitiesToCheck) {
                entitiesToCheck += entityToCheck.passengers
                passengers += entityToCheck.passengers
                
                entitiesToCheck -= entityToCheck
            }
        }
        
        return passengers
    }
    
    /**
     * Serializes an [Entity] to a [ByteArray].
     *
     * @param remove If the serialized [Entity] should be removed from the world.
     * @param nbtModifier Called before the [CompoundTag] gets compressed to a [ByteArray] to allow modifications.
     */
    fun serialize(
        entity: Entity,
        remove: Boolean = false,
        nbtModifier: ((CompoundTag) -> CompoundTag)? = null
    ): ByteArray {
        // get nms entity
        val nmsEntity = entity.nmsEntity
        
        // serialize data to compound tag
        var compoundTag = nmsEntity.saveWithoutId(CompoundTag())
        
        // add id tag to compound tag to identify entity type
        compoundTag.putString("id", nmsEntity.encodeId)
        
        // modify nbt data
        if (nbtModifier != null) compoundTag = nbtModifier.invoke(compoundTag)
        
        // write data to byte array
        val stream = ByteArrayOutputStream()
        NbtIo.writeCompressed(compoundTag, stream)
        val data = stream.toByteArray()
        
        if (remove) {
            getAllPassengers(entity).forEach { it.remove() }
            entity.remove()
        }
        
        return data
    }
    
    /**
     * Spawns an [Entity] based on serialized [data] and a [location].
     *
     * @param nbtModifier Called before the [Entity] gets spawned into the world to allow nbt modifications.
     */
    fun deserializeAndSpawn(
        data: ByteArray,
        location: Location,
        nbtModifier: ((CompoundTag) -> CompoundTag)? = null
    ): MojangEntity {
        // get world
        val world = location.world!!
        val level = world.serverLevel
        
        // read data to compound tag
        var compoundTag = NbtIo.readCompressed(ByteArrayInputStream(data))
        
        // set new location in nbt data
        compoundTag.put("Pos", NBTUtils.createDoubleList(location.x, location.y, location.z))
        
        // set new rotation in nbt data
        compoundTag.put("Rotation", NBTUtils.createFloatList(location.yaw, location.pitch))
        
        // modify nbt data
        if (nbtModifier != null) compoundTag = nbtModifier.invoke(compoundTag)
        
        // deserialize compound tag to entity
        return NMSEntityType.loadEntityRecursive(compoundTag, level) { entity ->
            // assign new uuid
            entity.uuid = UUID.randomUUID()
            
            // add entity to world
            level.addWithUUID(entity)
            entity
        }!!
    }
    
    /**
     * Creates a fake [ServerPlayer] object.
     */
    fun createFakePlayer(
        location: Location,
        uuid: UUID = UUID.randomUUID(),
        name: String = "Nova FakePlayer",
        hasEvents: Boolean = false
    ): ServerPlayer {
        val server = (Bukkit.getServer() as CraftServer).server
        val world = location.world!!.serverLevel
        val gameProfile = GameProfile(uuid, name)
        return FakePlayer(server, world, gameProfile, hasEvents)
    }
    
}

class FakePlayer(
    server: MinecraftServer,
    level: ServerLevel,
    profile: GameProfile,
    val hasEvents: Boolean
) : ServerPlayer(server, level, profile) {
    
    init {
        advancements.stopListening()
    }
    
    override fun onEffectAdded(mobeffect: MobEffectInstance?, entity: MojangEntity?) {
        // empty
    }
    
}