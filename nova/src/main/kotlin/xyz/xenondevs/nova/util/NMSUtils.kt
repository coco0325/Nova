@file:Suppress("unused")

package xyz.xenondevs.nova.util

import com.mojang.datafixers.util.Either
import com.mojang.serialization.JsonOps
import com.mojang.serialization.Lifecycle
import io.papermc.paper.threadedregions.RegionizedWorldData
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.NonNullList
import net.minecraft.core.Registry
import net.minecraft.core.Rotations
import net.minecraft.core.WritableRegistry
import net.minecraft.network.protocol.Packet
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.server.players.PlayerList
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.phys.Vec3
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_19_R3.CraftServer
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import xyz.xenondevs.cbf.adapter.BinaryAdapter
import xyz.xenondevs.nova.addon.Addon
import xyz.xenondevs.nova.data.NamespacedId
import xyz.xenondevs.nova.registry.RegistryBinaryAdapter
import xyz.xenondevs.nova.registry.vanilla.VanillaRegistryAccess
import xyz.xenondevs.nova.transformer.patch.playerlist.BroadcastPacketPatch
import xyz.xenondevs.nova.util.reflection.ReflectionRegistry
import xyz.xenondevs.nova.util.reflection.ReflectionUtils
import xyz.xenondevs.nova.world.BlockPos
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.optionals.getOrNull
import net.minecraft.core.BlockPos as MojangBlockPos
import net.minecraft.world.entity.Entity as MojangEntity
import net.minecraft.world.entity.EquipmentSlot as MojangEquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute as MojangAttribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier as MojangAttributeModifier
import net.minecraft.world.entity.player.Player as MojangPlayer
import net.minecraft.world.item.ItemStack as MojangStack
import net.minecraft.world.level.block.Block as MojangBlock

val Entity.nmsEntity: MojangEntity
    get() = (this as CraftEntity).handle

val Player.serverPlayer: ServerPlayer
    get() = (this as CraftPlayer).handle

@Deprecated("Misleading name", replaceWith = ReplaceWith("nmsCopy"))
val ItemStack.nmsStack: MojangStack
    get() = CraftItemStack.asNMSCopy(this)

val ItemStack?.nmsCopy: MojangStack
    get() = CraftItemStack.asNMSCopy(this)

@Deprecated("Misleading name", replaceWith = ReplaceWith("bukkitCopy"))
val MojangStack.bukkitStack: ItemStack
    get() = CraftItemStack.asBukkitCopy(this)

val MojangStack.bukkitCopy: ItemStack
    get() = CraftItemStack.asBukkitCopy(this)

val MojangStack.bukkitMirror: ItemStack
    get() = CraftItemStack.asCraftMirror(this)

val Location.blockPos: MojangBlockPos
    get() = MojangBlockPos(blockX, blockY, blockZ)

val Location.vec3: Vec3
    get() = Vec3(x, y, z)

val Vector.vec3: Vec3
    get() = Vec3(x, y, z)

val MojangBlockPos.vec3: Vec3
    get() = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

val MojangBlockPos.center: Vec3
    get() = Vec3(x + 0.5, y + 0.5, z + 0.5)

val World.serverLevel: ServerLevel
    get() = (this as CraftWorld).handle

val Player.connection: ServerGamePacketListenerImpl
    get() = serverPlayer.connection

val NamespacedKey.resourceLocation: ResourceLocation
    get() = ResourceLocation(toString())

@Suppress("DEPRECATION")
val ResourceLocation.namespacedKey: NamespacedKey
    get() = NamespacedKey(namespace, path)

val ResourceLocation.namespacedId: NamespacedId
    get() = NamespacedId(namespace, path)

internal val ResourceLocation.name: String
    get() = path

val InteractionHand.bukkitEquipmentSlot: EquipmentSlot
    get() = when (this) {
        InteractionHand.MAIN_HAND -> EquipmentSlot.HAND
        InteractionHand.OFF_HAND -> EquipmentSlot.OFF_HAND
    }

val EquipmentSlot.interactionHand: InteractionHand
    get() = when (this) {
        EquipmentSlot.HAND -> InteractionHand.MAIN_HAND
        EquipmentSlot.OFF_HAND -> InteractionHand.OFF_HAND
        else -> throw UnsupportedOperationException()
    }

val InteractionHand.equipmentSlot: EquipmentSlot
    get() = when (this) {
        InteractionHand.MAIN_HAND -> EquipmentSlot.HAND
        InteractionHand.OFF_HAND -> EquipmentSlot.OFF_HAND
    }

val EquipmentSlot.nmsEquipmentSlot: MojangEquipmentSlot
    get() = when (this) {
        EquipmentSlot.HAND -> MojangEquipmentSlot.MAINHAND
        EquipmentSlot.OFF_HAND -> MojangEquipmentSlot.OFFHAND
        EquipmentSlot.FEET -> MojangEquipmentSlot.FEET
        EquipmentSlot.LEGS -> MojangEquipmentSlot.LEGS
        EquipmentSlot.CHEST -> MojangEquipmentSlot.CHEST
        EquipmentSlot.HEAD -> MojangEquipmentSlot.HEAD
    }

val MojangEquipmentSlot.bukkitEquipmentSlot: EquipmentSlot
    get() = when (this) {
        MojangEquipmentSlot.MAINHAND -> EquipmentSlot.HAND
        MojangEquipmentSlot.OFFHAND -> EquipmentSlot.OFF_HAND
        MojangEquipmentSlot.FEET -> EquipmentSlot.FEET
        MojangEquipmentSlot.LEGS -> EquipmentSlot.LEGS
        MojangEquipmentSlot.CHEST -> EquipmentSlot.CHEST
        MojangEquipmentSlot.HEAD -> EquipmentSlot.HEAD
    }

val BlockFace.nmsDirection: Direction
    get() = when (this) {
        BlockFace.NORTH -> Direction.NORTH
        BlockFace.EAST -> Direction.EAST
        BlockFace.SOUTH -> Direction.SOUTH
        BlockFace.WEST -> Direction.WEST
        BlockFace.UP -> Direction.UP
        BlockFace.DOWN -> Direction.DOWN
        else -> throw UnsupportedOperationException()
    }

val Direction.blockFace: BlockFace
    get() = when (this) {
        Direction.NORTH -> BlockFace.NORTH
        Direction.EAST -> BlockFace.EAST
        Direction.SOUTH -> BlockFace.SOUTH
        Direction.WEST -> BlockFace.WEST
        Direction.UP -> BlockFace.UP
        Direction.DOWN -> BlockFace.DOWN
    }

val Attribute.nmsAttribute: MojangAttribute
    get() = when (this) {
        Attribute.GENERIC_MAX_HEALTH -> Attributes.MAX_HEALTH
        Attribute.GENERIC_FOLLOW_RANGE -> Attributes.FOLLOW_RANGE
        Attribute.GENERIC_KNOCKBACK_RESISTANCE -> Attributes.KNOCKBACK_RESISTANCE
        Attribute.GENERIC_MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED
        Attribute.GENERIC_FLYING_SPEED -> Attributes.FLYING_SPEED
        Attribute.GENERIC_ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE
        Attribute.GENERIC_ATTACK_KNOCKBACK -> Attributes.ATTACK_KNOCKBACK
        Attribute.GENERIC_ATTACK_SPEED -> Attributes.ATTACK_SPEED
        Attribute.GENERIC_ARMOR -> Attributes.ARMOR
        Attribute.GENERIC_ARMOR_TOUGHNESS -> Attributes.ARMOR_TOUGHNESS
        Attribute.GENERIC_LUCK -> Attributes.LUCK
        Attribute.HORSE_JUMP_STRENGTH -> Attributes.JUMP_STRENGTH
        Attribute.ZOMBIE_SPAWN_REINFORCEMENTS -> Attributes.SPAWN_REINFORCEMENTS_CHANCE
    }

val AttributeModifier.Operation.nmsOperation: MojangAttributeModifier.Operation
    get() = when (this) {
        AttributeModifier.Operation.ADD_NUMBER -> MojangAttributeModifier.Operation.ADDITION
        AttributeModifier.Operation.ADD_SCALAR -> MojangAttributeModifier.Operation.MULTIPLY_BASE
        AttributeModifier.Operation.MULTIPLY_SCALAR_1 -> MojangAttributeModifier.Operation.MULTIPLY_TOTAL
    }

val Material.nmsBlock: MojangBlock
    get() = CraftMagicNumbers.getBlock(this)

val Block.nmsState: BlockState
    get() = world.serverLevel.getBlockState(MojangBlockPos(x, y, z))

val BlockState.id: Int
    get() = MojangBlock.getId(this)

fun MojangBlockPos.toNovaPos(world: World): BlockPos =
    BlockPos(world, x, y, z)

fun Player.send(vararg packets: Packet<*>) {
    val connection = connection
    packets.forEach { connection.send(it) }
}

fun Player.send(packets: Iterable<Packet<*>>) {
    val connection = connection
    packets.forEach { connection.send(it) }
}

fun Packet<*>.sendTo(vararg players: Player) {
    players.forEach { it.send(this) }
}

fun Packet<*>.sendTo(players: Iterable<Player>) {
    players.forEach { it.send(this) }
}

fun Rotations.copy(x: Float? = null, y: Float? = null, z: Float? = null) =
    Rotations(x ?: this.x, y ?: this.y, z ?: this.z)

fun Rotations.add(x: Float, y: Float, z: Float) =
    Rotations(this.x + x, this.y + y, this.z + z)

val MINECRAFT_SERVER: DedicatedServer = (Bukkit.getServer() as CraftServer).server

val serverTick: Int
    get() = MINECRAFT_SERVER.tickCount

@Suppress("FunctionName")
fun <E> NonNullList(list: List<E>, default: E? = null): NonNullList<E> {
    val nonNullList: NonNullList<E>
    if (default == null) {
        nonNullList = NonNullList.createWithCapacity(list.size)
        nonNullList.addAll(list)
    } else {
        nonNullList = NonNullList.withSize(list.size, default)
        list.forEachIndexed { index, e -> nonNullList[index] = e }
    }
    
    return nonNullList
}

fun <T : Comparable<T>> BlockState.hasProperty(property: Property<T>, value: T): Boolean {
    return hasProperty(property) && values[property] == value
}

fun BlockPos.setBlockStateNoUpdate(state: BlockState) {
    val section = chunkSection
    val old = section.getBlockState(this)
    section.setBlockStateSilently(this, state)
    world.serverLevel.sendBlockUpdated(nmsPos, old, state, 3)
}

fun BlockPos.setBlockStateSilently(state: BlockState) {
    chunkSection.setBlockStateSilently(this, state)
}

fun BlockPos.setBlockState(state: BlockState) {
    world.serverLevel.setBlock(nmsPos, state, 11)
}

fun BlockPos.getBlockState(): BlockState {
    return chunkSection.getBlockState(this)
}

val BlockPos.chunkSection: LevelChunkSection
    get() {
        val chunk = world.serverLevel.getChunk(x shr 4, z shr 4)
        return chunk.getSection(chunk.getSectionIndex(y))
    }

fun LevelChunkSection.setBlockStateSilently(pos: BlockPos, state: BlockState) {
    setBlockState(pos.x and 0xF, pos.y and 0xF, pos.z and 0xF, state)
}

fun LevelChunkSection.getBlockState(pos: BlockPos): BlockState {
    return getBlockState(pos.x and 0xF, pos.y and 0xF, pos.z and 0xF)
}

/*inline fun Level.captureDrops(run: () -> Unit): List<ItemEntity> {
    val captureDrops = ArrayList<ItemEntity>()
    this.captureDrops = captureDrops
    try {
        run.invoke()
        return captureDrops
    } finally {
        this.captureDrops = null
    }
}*/

inline fun RegionizedWorldData.captureDrops(run: () -> Unit): List<ItemEntity> {
    val captureDrops = ArrayList<ItemEntity>()
    this.captureDrops = captureDrops
    try {
        run.invoke()
        return captureDrops
    } finally {
        this.captureDrops = null
    }
}

fun <T> Either<T, T>.take(): T {
    return left().orElse(null) ?: right().get()
}

fun PlayerList.broadcast(location: Location, maxDistance: Double, packet: Packet<*>) =
    broadcast(null, location.x, location.y, location.z, maxDistance, location.world!!.serverLevel.dimension(), packet)

fun PlayerList.broadcast(block: Block, maxDistance: Double, packet: Packet<*>) =
    broadcast(null, block.x.toDouble(), block.y.toDouble(), block.z.toDouble(), maxDistance, block.world.serverLevel.dimension(), packet)

fun PlayerList.broadcast(exclude: MojangPlayer?, location: Location, maxDistance: Double, packet: Packet<*>) =
    broadcast(exclude, location.x, location.y, location.z, maxDistance, location.world!!.serverLevel.dimension(), packet)

fun PlayerList.broadcast(exclude: MojangPlayer?, block: Block, maxDistance: Double, packet: Packet<*>) =
    broadcast(exclude, block.x.toDouble(), block.y.toDouble(), block.z.toDouble(), maxDistance, block.world.serverLevel.dimension(), packet)

fun PlayerList.broadcast(exclude: Player?, location: Location, maxDistance: Double, packet: Packet<*>) =
    broadcast(exclude?.serverPlayer, location.x, location.y, location.z, maxDistance, location.world!!.serverLevel.dimension(), packet)

fun PlayerList.broadcast(exclude: Player?, block: Block, maxDistance: Double, packet: Packet<*>) =
    broadcast(exclude?.serverPlayer, block.x.toDouble(), block.y.toDouble(), block.z.toDouble(), maxDistance, block.world.serverLevel.dimension(), packet)

fun <T : Any> Registry<T>.byNameBinaryAdapter(): BinaryAdapter<T> {
    return RegistryBinaryAdapter(this)
}

operator fun <T> Registry<T>.get(key: String): T? {
    return get(ResourceLocation.of(key, ':'))
}

fun <T> Registry<T>.getOrCreateHolder(id: ResourceLocation): Holder<T> {
    val key = ResourceKey.create(key(), id)
    val holder = getHolder(key)
    
    if (holder.isPresent)
        return holder.get()
    
    if (this !is MappedRegistry<T>)
        throw IllegalStateException("Can't create holder for non MappedRegistry ${this.key()}")
    
    return this.createRegistrationLookup().getOrThrow(key)
}

operator fun Registry<*>.contains(key: String): Boolean {
    return containsKey(ResourceLocation.of(key, ':'))
}

internal operator fun <T> WritableRegistry<T>.set(name: String, value: T) {
    register(ResourceKey.create(key(), ResourceLocation.of(name, ':')), value, Lifecycle.stable())
}

internal operator fun <T> WritableRegistry<T>.set(id: ResourceLocation, value: T) {
    register(ResourceKey.create(key(), id), value, Lifecycle.stable())
}

operator fun <T> WritableRegistry<T>.set(addon: Addon, key: String, value: T) {
    register(ResourceKey.create(key(), ResourceLocation(addon, key)), value, Lifecycle.stable())
}

fun ResourceLocation.toString(separator: String): String {
    return namespace + separator + path
}

fun ResourceLocation(addon: Addon, name: String): ResourceLocation {
    return ResourceLocation(addon.description.id, name)
}

fun preventPacketBroadcast(run: () -> Unit) {
    BroadcastPacketPatch.dropAll = true
    try {
        run.invoke()
    } finally {
        BroadcastPacketPatch.dropAll = false
    }
}

fun replaceBroadcastExclusion(exclude: ServerPlayer, run: () -> Unit) {
    BroadcastPacketPatch.exclude = exclude
    try {
        run.invoke()
    } finally {
        BroadcastPacketPatch.exclude = null
    }
}

fun forcePacketBroadcast(run: () -> Unit) {
    BroadcastPacketPatch.ignoreExcludedPlayer = true
    try {
        run.invoke()
    } finally {
        BroadcastPacketPatch.ignoreExcludedPlayer = false
    }
}

object NMSUtils {
    
    val ENTITY_COUNTER = ReflectionUtils.getField(
        MojangEntity::class.java,
        true,
        "SRF(net.minecraft.world.entity.Entity ENTITY_COUNTER)"
    ).get(null) as AtomicInteger
    
    val REGISTRY_ACCESS = MINECRAFT_SERVER.registryAccess()!!
    val REGISTRY_OPS = RegistryOps.create(JsonOps.INSTANCE, VanillaRegistryAccess)!!
    
    fun freezeRegistry(registry: Registry<*>) {
        if (registry !is MappedRegistry) return
        ReflectionRegistry.MAPPED_REGISTRY_FROZEN_FIELD[registry] = true
    }
    
    fun unfreezeRegistry(registry: Registry<*>) {
        if (registry !is MappedRegistry) return
        ReflectionRegistry.MAPPED_REGISTRY_FROZEN_FIELD[registry] = false
    }
    
    fun <T, R : Registry<T>> getRegistry(location: ResourceKey<R>) =
        REGISTRY_ACCESS.registry(location).getOrNull() ?: throw IllegalArgumentException("Registry $location does not exist!")
    
    fun <T, R : Registry<T>> getHolder(key: ResourceKey<T>): Holder.Reference<T> {
        val registry = ResourceKey.createRegistryKey<T>(key.registry())
        return REGISTRY_ACCESS.registryOrThrow(registry).getHolderOrThrow(key)
    }
    
}
