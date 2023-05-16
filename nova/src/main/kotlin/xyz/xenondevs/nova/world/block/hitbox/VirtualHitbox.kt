package xyz.xenondevs.nova.world.block.hitbox

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.player.PlayerInteractEvent
import org.joml.Vector3f
import xyz.xenondevs.nova.util.toVector3f
import xyz.xenondevs.nova.world.BlockPos
import java.util.*
import kotlin.math.floor

private typealias HitboxQualifier = (PlayerInteractEvent) -> Boolean

@Suppress("DuplicatedCode")
fun VirtualHitbox(from: Location, to: Location): VirtualHitbox {
    require(from.world != null && from.world == to.world) { "from and to must be in the same world" }
    require(from.x < to.x) { "from.x must be smaller than to.x" }
    require(from.y < to.y) { "from.y must be smaller than to.y" }
    require(from.z < to.z) { "from.z must be smaller than to.z" }
    
    val fromVec = from.toVector3f()
    val toVec = to.toVector3f()
    
    val xWidth = toVec.x - fromVec.x
    val zWidth = toVec.z - fromVec.z
    val height = toVec.y - fromVec.y
    val center = Vector3f(fromVec.x + xWidth / 2f, fromVec.y, fromVec.z + zWidth / 2f)
    
    return VirtualHitbox(
        from.world!!,
        center,
        fromVec, toVec,
        xWidth, zWidth, height
    )
}

class VirtualHitbox internal constructor(
    world: World,
    center: Vector3f,
    from: Vector3f, to: Vector3f,
    xWidth: Float, zWidth: Float, height: Float
) : Hitbox<ClickAtLocationHandler, ClickAtLocationHandler>(world, center, from, to, xWidth, zWidth, height) {
    
    internal val uuid = UUID.randomUUID() // region id for visualization
    
    internal val blocks: Set<BlockPos>
    internal var qualifier: HitboxQualifier? = null
    
    init {
        blocks = getBlocksBetween(world, from, to)
    }
    
    constructor(center: Location, xWidth: Double, zWidth: Double, height: Double) : this(
        center.world!!,
        center.toVector3f(),
        Vector3f((center.x - xWidth / 2).toFloat(), center.y.toFloat(), (center.z - zWidth / 2).toFloat()),
        Vector3f((center.x + xWidth / 2).toFloat(), (center.y + height).toFloat(), (center.z + zWidth / 2).toFloat()),
        xWidth.toFloat(),
        zWidth.toFloat(),
        height.toFloat()
    )
    
    fun setQualifier(qualifier: HitboxQualifier) {
        this.qualifier = qualifier
    }
    
}

private fun getBlocksBetween(world: World, from: Vector3f, to: Vector3f): Set<BlockPos> {
    val blocks = HashSet<BlockPos>()
    
    val minX = floor(from.x).toInt()
    val minY = floor(from.y).toInt()
    val minZ = floor(from.z).toInt()
    val maxX = floor(to.x).toInt()
    val maxY = floor(to.y).toInt()
    val maxZ = floor(to.z).toInt()
    
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            for (z in minZ..maxZ) {
                blocks += BlockPos(world, x, y, z)
            }
        }
    }
    
    return blocks
}