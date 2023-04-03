package xyz.xenondevs.nova.integration.protection

import com.destroystokyo.paper.ClientOption
import com.destroystokyo.paper.Title
import com.destroystokyo.paper.block.TargetBlockInfo
import com.destroystokyo.paper.entity.TargetEntityInfo
import com.destroystokyo.paper.profile.PlayerProfile
import io.papermc.paper.entity.LookAnchor
import io.papermc.paper.entity.TeleportFlag
import io.papermc.paper.threadedregions.scheduler.EntityScheduler
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.TriState
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.DyeColor
import org.bukkit.Effect
import org.bukkit.EntityEffect
import org.bukkit.FluidCollisionMode
import org.bukkit.GameMode
import org.bukkit.Instrument
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Note
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.WeatherType
import org.bukkit.World
import org.bukkit.WorldBorder
import org.bukkit.advancement.Advancement
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.PistonMoveReaction
import org.bukkit.block.Sign
import org.bukkit.block.data.BlockData
import org.bukkit.conversations.Conversation
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.entity.*
import org.bukkit.entity.memory.MemoryKey
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MainHand
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.PlayerInventory
import org.bukkit.map.MapView
import org.bukkit.metadata.MetadataValue
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.util.BoundingBox
import org.bukkit.util.Consumer
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector
import org.jetbrains.annotations.Contract
import xyz.xenondevs.nova.util.PermissionUtils
import java.net.InetSocketAddress
import java.util.*

/**
 * A [Player] which will throw an [UnsupportedOperationException]
 * when methods which aren't supported by [OfflinePlayer] are called.
 *
 * This [Player] is also granted access to the [hasPermission]
 * method via Vault.
 */
internal class FakeOnlinePlayer(
    private val offlinePlayer: OfflinePlayer,
    private val location: Location
) : Player, OfflinePlayer by offlinePlayer {
    
    override fun hasPermission(name: String): Boolean {
        return PermissionUtils.hasPermission(world, uniqueId, name)
    }
    
    override fun hasPermission(perm: Permission): Boolean {
        return PermissionUtils.hasPermission(world, uniqueId, perm.name)
    }
    
    override fun isPermissionSet(name: String): Boolean {
        return true
    }
    
    override fun isPermissionSet(perm: Permission): Boolean {
        return true
    }
    
    override fun getWorld(): World {
        return location.world!!
    }
    
    override fun getLocale(): String {
        return "en_us"
    }

    override fun getAffectsSpawning(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setAffectsSpawning(affects: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getViewDistance(): Int {
        TODO("Not yet implemented")
    }

    override fun setViewDistance(viewDistance: Int) {
        TODO("Not yet implemented")
    }

    override fun getSimulationDistance(): Int {
        TODO("Not yet implemented")
    }

    override fun setSimulationDistance(simulationDistance: Int) {
        TODO("Not yet implemented")
    }

    override fun getNoTickViewDistance(): Int {
        TODO("Not yet implemented")
    }

    override fun setNoTickViewDistance(viewDistance: Int) {
        TODO("Not yet implemented")
    }

    override fun getSendViewDistance(): Int {
        TODO("Not yet implemented")
    }

    override fun setSendViewDistance(viewDistance: Int) {
        TODO("Not yet implemented")
    }

    override fun getLocation(): Location {
        return location
    }
    
    override fun isOnline(): Boolean {
        return true
    }
    
    override fun getName(): String {
        return offlinePlayer.name ?: "OfflinePlayer"
    }
    
    override fun getDisplayName(): String {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setDisplayName(name: String?) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun playerListName(name: Component?) {
        TODO("Not yet implemented")
    }

    override fun playerListName(): Component {
        TODO("Not yet implemented")
    }

    override fun playerListHeader(): Component? {
        TODO("Not yet implemented")
    }

    override fun playerListFooter(): Component? {
        TODO("Not yet implemented")
    }

    override fun getPlayerListName(): String {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPlayerListName(name: String?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPlayerListHeader(): String? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPlayerListFooter(): String? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPlayerListHeader(header: String?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPlayerListFooter(footer: String?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPlayerListHeaderFooter(header: String?, footer: String?) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun setPlayerListHeaderFooter(header: Array<out BaseComponent?>?, footer: Array<out BaseComponent?>?) {
        TODO("Not yet implemented")
    }

    override fun setPlayerListHeaderFooter(header: BaseComponent?, footer: BaseComponent?) {
        TODO("Not yet implemented")
    }

    override fun setCompassTarget(loc: Location) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getCompassTarget(): Location {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getAddress(): InetSocketAddress? {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun getProtocolVersion(): Int {
        TODO("Not yet implemented")
    }

    override fun getVirtualHost(): InetSocketAddress? {
        TODO("Not yet implemented")
    }

    override fun displayName(): Component {
        TODO("Not yet implemented")
    }

    override fun displayName(displayName: Component?) {
        TODO("Not yet implemented")
    }

    override fun sendRawMessage(message: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun kickPlayer(message: String?) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun kick() {
        TODO("Not yet implemented")
    }

    override fun kick(message: Component?) {
        TODO("Not yet implemented")
    }

    override fun kick(message: Component?, cause: PlayerKickEvent.Cause) {
        TODO("Not yet implemented")
    }

    override fun chat(msg: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun performCommand(command: String): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isOnGround(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isSneaking(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSneaking(sneak: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isSprinting(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSprinting(sprinting: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun saveData() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun loadData() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSleepingIgnored(isSleeping: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isSleepingIgnored(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setBedSpawnLocation(location: Location?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setBedSpawnLocation(location: Location?, force: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playNote(loc: Location, instrument: Byte, note: Byte) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playNote(loc: Location, instrument: Instrument, note: Note) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(location: Location, sound: Sound, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(location: Location, sound: String, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(location: Location, sound: Sound, category: SoundCategory, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(location: Location, sound: String, category: SoundCategory, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(entity: Entity, sound: Sound, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(entity: Entity, sound: String, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(entity: Entity, sound: Sound, category: SoundCategory, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playSound(entity: Entity, sound: String, category: SoundCategory, volume: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun stopSound(sound: Sound) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun stopSound(sound: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun stopSound(sound: Sound, category: SoundCategory?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun stopSound(sound: String, category: SoundCategory?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun stopSound(sound: SoundCategory) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun stopAllSounds() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun breakBlock(p0: Block): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playEffect(loc: Location, effect: Effect, data: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> playEffect(loc: Location, effect: Effect, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendBlockChange(loc: Location, material: Material, data: Byte) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendBlockChange(loc: Location, block: BlockData) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendBlockChanges(blocks: MutableCollection<BlockState>, suppressLightUpdates: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendBlockDamage(loc: Location, progress: Float) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun sendBlockDamage(loc: Location, progress: Float, sourceId: Int) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun sendMultiBlockChange(blockChanges: MutableMap<Location, BlockData>, suppressLightUpdates: Boolean) {
        TODO("Not yet implemented")
    }

    override fun sendEquipmentChange(entity: LivingEntity, slot: EquipmentSlot, item: ItemStack?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendEquipmentChange(entity: LivingEntity, items: MutableMap<EquipmentSlot, ItemStack>) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun sendSignChange(
        loc: Location,
        lines: MutableList<out Component>?,
        dyeColor: DyeColor,
        hasGlowingText: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun sendSignChange(loc: Location, lines: Array<out String?>?) {
        TODO("Not yet implemented")
    }

    override fun sendSignChange(loc: Location, lines: Array<out String?>?, dyeColor: DyeColor) {
        TODO("Not yet implemented")
    }

    override fun sendSignChange(
        loc: Location,
        lines: Array<out String?>?,
        dyeColor: DyeColor,
        hasGlowingText: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun sendMap(map: MapView) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun showWinScreen() {
        TODO("Not yet implemented")
    }

    override fun hasSeenWinScreen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setHasSeenWinScreen(hasSeenWinScreen: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setTitleTimes(fadeInTicks: Int, stayTicks: Int, fadeOutTicks: Int) {
        TODO("Not yet implemented")
    }

    override fun setSubtitle(subtitle: Array<out BaseComponent>?) {
        TODO("Not yet implemented")
    }

    override fun setSubtitle(subtitle: BaseComponent?) {
        TODO("Not yet implemented")
    }

    override fun sendTitle(title: Title) {
        TODO("Not yet implemented")
    }

    override fun addCustomChatCompletions(completions: MutableCollection<String>) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun removeCustomChatCompletions(completions: MutableCollection<String>) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setCustomChatCompletions(completions: MutableCollection<String>) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun updateInventory() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPreviousGameMode(): GameMode? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPlayerTime(time: Long, relative: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPlayerTime(): Long {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPlayerTimeOffset(): Long {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isPlayerTimeRelative(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun resetPlayerTime() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPlayerWeather(type: WeatherType) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPlayerWeather(): WeatherType? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun resetPlayerWeather() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun giveExp(amount: Int) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun giveExp(amount: Int, applyMending: Boolean) {
        TODO("Not yet implemented")
    }

    override fun applyMending(amount: Int): Int {
        TODO("Not yet implemented")
    }

    override fun giveExpLevels(amount: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getExp(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setExp(exp: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getLevel(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setLevel(level: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getTotalExperience(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setTotalExperience(exp: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendExperienceChange(progress: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendExperienceChange(progress: Float, level: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getAllowFlight(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setAllowFlight(flight: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun setFlyingFallDamage(flyingFallDamage: TriState) {
        TODO("Not yet implemented")
    }

    override fun hasFlyingFallDamage(): TriState {
        TODO("Not yet implemented")
    }

    override fun hidePlayer(player: Player) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hidePlayer(plugin: Plugin, player: Player) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun showPlayer(player: Player) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun showPlayer(plugin: Plugin, player: Player) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun canSee(player: Player): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun canSee(entity: Entity): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hideEntity(plugin: Plugin, entity: Entity) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun showEntity(plugin: Plugin, entity: Entity) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isFlying(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setFlying(value: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    @Throws(IllegalArgumentException::class)
    override fun setFlySpeed(value: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    @Throws(IllegalArgumentException::class)
    override fun setWalkSpeed(value: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFlySpeed(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getWalkSpeed(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setTexturePack(url: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setResourcePack(url: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setResourcePack(url: String, hash: ByteArray?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setResourcePack(url: String, hash: ByteArray?, prompt: String?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setResourcePack(url: String, hash: ByteArray?, force: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setResourcePack(url: String, hash: ByteArray?, prompt: String?, force: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun setResourcePack(url: String, hash: ByteArray?, prompt: Component?, force: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setResourcePack(url: String, hash: String) {
        TODO("Not yet implemented")
    }

    override fun setResourcePack(url: String, hash: String, required: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setResourcePack(url: String, hash: String, required: Boolean, resourcePackPrompt: Component?) {
        TODO("Not yet implemented")
    }

    override fun getScoreboard(): Scoreboard {
        throw UnsupportedOperationException("Player is not online")
    }
    
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override fun setScoreboard(scoreboard: Scoreboard) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getWorldBorder(): WorldBorder? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setWorldBorder(border: WorldBorder?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isHealthScaled(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setHealthScaled(scale: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    @Throws(IllegalArgumentException::class)
    override fun setHealthScale(scale: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getHealthScale(): Double {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun sendHealthUpdate(health: Double, foodLevel: Int, saturationLevel: Float) {
        TODO("Not yet implemented")
    }

    override fun sendHealthUpdate() {
        TODO("Not yet implemented")
    }

    override fun getSpectatorTarget(): Entity? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSpectatorTarget(entity: Entity?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendTitle(title: String?, subtitle: String?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendTitle(title: String?, subtitle: String?, fadeIn: Int, stay: Int, fadeOut: Int) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun updateTitle(title: Title) {
        TODO("Not yet implemented")
    }

    override fun hideTitle() {
        TODO("Not yet implemented")
    }

    override fun resetTitle() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun spawnParticle(particle: Particle, location: Location, count: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> spawnParticle(particle: Particle, location: Location, count: Int, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun spawnParticle(particle: Particle, location: Location, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> spawnParticle(particle: Particle, location: Location, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun spawnParticle(particle: Particle, location: Location, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, extra: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, extra: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> spawnParticle(particle: Particle, location: Location, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, extra: Double, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> spawnParticle(particle: Particle, x: Double, y: Double, z: Double, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, extra: Double, data: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getAdvancementProgress(advancement: Advancement): AdvancementProgress {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getClientViewDistance(): Int {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun locale(): Locale {
        TODO("Not yet implemented")
    }

    override fun getPing(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun updateCommands() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openBook(book: ItemStack) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openSign(sign: Sign) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun showDemoScreen() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isAllowingServerListings(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun getResourcePackStatus(): PlayerResourcePackStatusEvent.Status? {
        TODO("Not yet implemented")
    }

    override fun getResourcePackHash(): String? {
        TODO("Not yet implemented")
    }

    override fun hasResourcePack(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setPlayerProfile(profile: PlayerProfile) {
        TODO("Not yet implemented")
    }

    override fun getCooldownPeriod(): Float {
        TODO("Not yet implemented")
    }

    override fun getCooledAttackStrength(adjustTicks: Float): Float {
        TODO("Not yet implemented")
    }

    override fun resetCooldown() {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> getClientOption(option: ClientOption<T>): T {
        TODO("Not yet implemented")
    }

    override fun boostElytra(firework: ItemStack): Firework? {
        TODO("Not yet implemented")
    }

    override fun sendOpLevel(level: Byte) {
        TODO("Not yet implemented")
    }

    override fun addAdditionalChatCompletions(completions: MutableCollection<String>) {
        TODO("Not yet implemented")
    }

    override fun removeAdditionalChatCompletions(completions: MutableCollection<String>) {
        TODO("Not yet implemented")
    }

    override fun getClientBrandName(): String? {
        TODO("Not yet implemented")
    }

    override fun lookAt(x: Double, y: Double, z: Double, playerAnchor: LookAnchor) {
        TODO("Not yet implemented")
    }

    override fun lookAt(entity: Entity, playerAnchor: LookAnchor, entityAnchor: LookAnchor) {
        TODO("Not yet implemented")
    }

    override fun showElderGuardian(silent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getWardenWarningCooldown(): Int {
        TODO("Not yet implemented")
    }

    override fun setWardenWarningCooldown(cooldown: Int) {
        TODO("Not yet implemented")
    }

    override fun getWardenTimeSinceLastWarning(): Int {
        TODO("Not yet implemented")
    }

    override fun setWardenTimeSinceLastWarning(time: Int) {
        TODO("Not yet implemented")
    }

    override fun getWardenWarningLevel(): Int {
        TODO("Not yet implemented")
    }

    override fun setWardenWarningLevel(warningLevel: Int) {
        TODO("Not yet implemented")
    }

    override fun increaseWardenWarningLevel() {
        TODO("Not yet implemented")
    }

    override fun spigot(): Player.Spigot {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun name(): Component {
        TODO("Not yet implemented")
    }

    override fun customName(): Component? {
        TODO("Not yet implemented")
    }

    override fun customName(customName: Component?) {
        TODO("Not yet implemented")
    }

    override fun getInventory(): PlayerInventory {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEnderChest(): Inventory {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getMainHand(): MainHand {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setWindowProperty(prop: InventoryView.Property, value: Int): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEnchantmentSeed(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setEnchantmentSeed(seed: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getOpenInventory(): InventoryView {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openInventory(inventory: Inventory): InventoryView? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openWorkbench(location: Location?, force: Boolean): InventoryView? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openEnchanting(location: Location?, force: Boolean): InventoryView? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openInventory(inventory: InventoryView) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openMerchant(trader: Villager, force: Boolean): InventoryView? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun openMerchant(merchant: Merchant, force: Boolean): InventoryView? {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun openAnvil(location: Location?, force: Boolean): InventoryView? {
        TODO("Not yet implemented")
    }

    override fun openCartographyTable(location: Location?, force: Boolean): InventoryView? {
        TODO("Not yet implemented")
    }

    override fun openGrindstone(location: Location?, force: Boolean): InventoryView? {
        TODO("Not yet implemented")
    }

    override fun openLoom(location: Location?, force: Boolean): InventoryView? {
        TODO("Not yet implemented")
    }

    override fun openSmithingTable(location: Location?, force: Boolean): InventoryView? {
        TODO("Not yet implemented")
    }

    override fun openStonecutter(location: Location?, force: Boolean): InventoryView? {
        TODO("Not yet implemented")
    }

    override fun closeInventory() {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun closeInventory(reason: InventoryCloseEvent.Reason) {
        TODO("Not yet implemented")
    }

    override fun getItemInHand(): ItemStack {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setItemInHand(item: ItemStack?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getItemOnCursor(): ItemStack {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setItemOnCursor(item: ItemStack?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasCooldown(material: Material): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getCooldown(material: Material): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setCooldown(material: Material, ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun isDeeplySleeping(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSleepTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun getPotentialBedLocation(): Location? {
        TODO("Not yet implemented")
    }

    override fun getFishHook(): FishHook? {
        TODO("Not yet implemented")
    }

    override fun sleep(location: Location, force: Boolean): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun wakeup(setSpawnLocation: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getBedLocation(): Location {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getGameMode(): GameMode {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setGameMode(mode: GameMode) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isBlocking(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isHandRaised(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun getHandRaised(): EquipmentSlot {
        TODO("Not yet implemented")
    }

    override fun isJumping(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setJumping(jumping: Boolean) {
        TODO("Not yet implemented")
    }

    override fun playPickupItemAnimation(item: Item, quantity: Int) {
        TODO("Not yet implemented")
    }

    override fun getHurtDirection(): Float {
        TODO("Not yet implemented")
    }

    override fun setHurtDirection(hurtDirection: Float) {
        TODO("Not yet implemented")
    }

    override fun knockback(strength: Double, directionX: Double, directionZ: Double) {
        TODO("Not yet implemented")
    }

    override fun broadcastSlotBreak(slot: EquipmentSlot) {
        TODO("Not yet implemented")
    }

    override fun broadcastSlotBreak(slot: EquipmentSlot, players: MutableCollection<Player>) {
        TODO("Not yet implemented")
    }

    override fun damageItemStack(stack: ItemStack, amount: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun damageItemStack(slot: EquipmentSlot, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun getBodyYaw(): Float {
        TODO("Not yet implemented")
    }

    override fun setBodyYaw(bodyYaw: Float) {
        TODO("Not yet implemented")
    }

    override fun getItemInUse(): ItemStack? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getExpToLevel(): Int {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun releaseLeftShoulderEntity(): Entity? {
        TODO("Not yet implemented")
    }

    override fun releaseRightShoulderEntity(): Entity? {
        TODO("Not yet implemented")
    }

    override fun getAttackCooldown(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun discoverRecipe(recipe: NamespacedKey): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun discoverRecipes(recipes: Collection<NamespacedKey>): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun undiscoverRecipe(recipe: NamespacedKey): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun undiscoverRecipes(recipes: Collection<NamespacedKey>): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasDiscoveredRecipe(recipe: NamespacedKey): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getDiscoveredRecipes(): Set<NamespacedKey> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getShoulderEntityLeft(): Entity? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setShoulderEntityLeft(entity: Entity?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getShoulderEntityRight(): Entity? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setShoulderEntityRight(entity: Entity?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun dropItem(dropAll: Boolean): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getExhaustion(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setExhaustion(value: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getSaturation(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSaturation(value: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFoodLevel(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setFoodLevel(value: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getSaturatedRegenRate(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSaturatedRegenRate(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getUnsaturatedRegenRate(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setUnsaturatedRegenRate(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getStarvationRate(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setStarvationRate(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setLastDeathLocation(location: Location?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun fireworkBoost(itemStack: ItemStack): Firework? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEyeHeight(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEyeHeight(ignorePose: Boolean): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEyeLocation(): Location {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getLineOfSight(transparent: Set<Material>?, maxDistance: Int): List<Block> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getTargetBlock(transparent: Set<Material>?, maxDistance: Int): Block {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun getTargetBlock(maxDistance: Int, fluidMode: TargetBlockInfo.FluidMode): Block? {
        TODO("Not yet implemented")
    }

    override fun getTargetBlockFace(maxDistance: Int, fluidMode: TargetBlockInfo.FluidMode): BlockFace? {
        TODO("Not yet implemented")
    }

    override fun getTargetBlockFace(maxDistance: Int, fluidMode: FluidCollisionMode): BlockFace? {
        TODO("Not yet implemented")
    }

    override fun getTargetBlockInfo(maxDistance: Int, fluidMode: TargetBlockInfo.FluidMode): TargetBlockInfo? {
        TODO("Not yet implemented")
    }

    override fun getTargetEntity(maxDistance: Int, ignoreBlocks: Boolean): Entity? {
        TODO("Not yet implemented")
    }

    override fun getTargetEntityInfo(maxDistance: Int, ignoreBlocks: Boolean): TargetEntityInfo? {
        TODO("Not yet implemented")
    }

    override fun rayTraceEntities(maxDistance: Int, ignoreBlocks: Boolean): RayTraceResult? {
        TODO("Not yet implemented")
    }

    override fun getLastTwoTargetBlocks(transparent: Set<Material>?, maxDistance: Int): List<Block> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getTargetBlockExact(maxDistance: Int): Block? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getTargetBlockExact(maxDistance: Int, fluidCollisionMode: FluidCollisionMode): Block? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun rayTraceBlocks(maxDistance: Double): RayTraceResult? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun rayTraceBlocks(maxDistance: Double, fluidCollisionMode: FluidCollisionMode): RayTraceResult? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getRemainingAir(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setRemainingAir(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getMaximumAir(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setMaximumAir(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getArrowCooldown(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setArrowCooldown(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getArrowsInBody(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setArrowsInBody(count: Int) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun setArrowsInBody(count: Int, fireEvent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getBeeStingerCooldown(): Int {
        TODO("Not yet implemented")
    }

    override fun setBeeStingerCooldown(ticks: Int) {
        TODO("Not yet implemented")
    }

    override fun getBeeStingersInBody(): Int {
        TODO("Not yet implemented")
    }

    override fun setBeeStingersInBody(count: Int) {
        TODO("Not yet implemented")
    }

    override fun getMaximumNoDamageTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setMaximumNoDamageTicks(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getLastDamage(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setLastDamage(damage: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getNoDamageTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setNoDamageTicks(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getKiller(): Player? {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun setKiller(killer: Player?) {
        TODO("Not yet implemented")
    }

    override fun addPotionEffect(effect: PotionEffect): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addPotionEffect(effect: PotionEffect, force: Boolean): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addPotionEffects(effects: Collection<PotionEffect>): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasPotionEffect(type: PotionEffectType): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPotionEffect(type: PotionEffectType): PotionEffect? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun removePotionEffect(type: PotionEffectType) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getActivePotionEffects(): Collection<PotionEffect> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasLineOfSight(other: Entity): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun hasLineOfSight(location: Location): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRemoveWhenFarAway(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setRemoveWhenFarAway(remove: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEquipment(): EntityEquipment {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setCanPickupItems(pickup: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getCanPickupItems(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isLeashed(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    @Throws(IllegalStateException::class)
    override fun getLeashHolder(): Entity {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setLeashHolder(holder: Entity?): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isGliding(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setGliding(gliding: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isSwimming(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSwimming(swimming: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isRiptiding(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isSleeping(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isClimbing(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setAI(ai: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasAI(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun attack(target: Entity) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun swingMainHand() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun swingOffHand() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setCollidable(collidable: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isCollidable(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getCollidableExemptions(): Set<UUID> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> getMemory(memoryKey: MemoryKey<T>): T? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T> setMemory(memoryKey: MemoryKey<T>, memoryValue: T?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getHurtSound(): Sound? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getDeathSound(): Sound? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFallDamageSound(fallHeight: Int): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFallDamageSoundSmall(): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFallDamageSoundBig(): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getDrinkingSound(itemStack: ItemStack): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEatingSound(itemStack: ItemStack): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun canBreatheUnderwater(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getCategory(): EntityCategory {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setInvisible(invisible: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isInvisible(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun getArrowsStuck(): Int {
        TODO("Not yet implemented")
    }

    override fun setArrowsStuck(arrows: Int) {
        TODO("Not yet implemented")
    }

    override fun getShieldBlockingDelay(): Int {
        TODO("Not yet implemented")
    }

    override fun setShieldBlockingDelay(delay: Int) {
        TODO("Not yet implemented")
    }

    override fun getActiveItem(): ItemStack {
        TODO("Not yet implemented")
    }

    override fun clearActiveItem() {
        TODO("Not yet implemented")
    }

    override fun getItemUseRemainingTime(): Int {
        TODO("Not yet implemented")
    }

    override fun getHandRaisedTime(): Int {
        TODO("Not yet implemented")
    }

    override fun getAttribute(attribute: Attribute): AttributeInstance? {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun registerAttribute(attribute: Attribute) {
        TODO("Not yet implemented")
    }

    override fun damage(amount: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun damage(amount: Double, source: Entity?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getHealth(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setHealth(health: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getAbsorptionAmount(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setAbsorptionAmount(amount: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getMaxHealth(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setMaxHealth(health: Double) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun resetMaxHealth() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    @Contract("null -> null; !null -> !null")
    override fun getLocation(loc: Location?): Location? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setVelocity(velocity: Vector) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getVelocity(): Vector {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getHeight(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getWidth(): Double {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getBoundingBox(): BoundingBox {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isInWater(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setRotation(yaw: Float, pitch: Float) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun teleport(
        location: Location,
        cause: PlayerTeleportEvent.TeleportCause,
        vararg teleportFlags: TeleportFlag
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun teleport(location: Location): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun teleport(location: Location, cause: PlayerTeleportEvent.TeleportCause): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun teleport(destination: Entity): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun teleport(destination: Entity, cause: PlayerTeleportEvent.TeleportCause): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getNearbyEntities(x: Double, y: Double, z: Double): List<Entity> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEntityId(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFireTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getMaxFireTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setFireTicks(ticks: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setVisualFire(p0: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isVisualFire(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFreezeTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getMaxFreezeTicks(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setFreezeTicks(p0: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isFrozen(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun isFreezeTickingLocked(): Boolean {
        TODO("Not yet implemented")
    }

    override fun lockFreezeTicks(locked: Boolean) {
        TODO("Not yet implemented")
    }

    override fun remove() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isDead(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isValid(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getServer(): Server {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isPersistent(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPersistent(persistent: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPassenger(): Entity? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPassenger(passenger: Entity): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPassengers(): List<Entity> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addPassenger(passenger: Entity): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun removePassenger(passenger: Entity): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isEmpty(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun eject(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFallDistance(): Float {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setFallDistance(distance: Float) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setLastDamageCause(event: EntityDamageEvent?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getLastDamageCause(): EntityDamageEvent? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getTicksLived(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setTicksLived(value: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun playEffect(type: EntityEffect) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getType(): EntityType {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getSwimSound(): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getSwimSplashSound(): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getSwimHighSpeedSplashSound(): Sound {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isInsideVehicle(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun leaveVehicle(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getVehicle(): Entity? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setCustomNameVisible(flag: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isCustomNameVisible(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setVisibleByDefault(visible: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isVisibleByDefault(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setGlowing(flag: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isGlowing(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setInvulnerable(flag: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isInvulnerable(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun isSilent(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setSilent(flag: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasGravity(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setGravity(gravity: Boolean) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPortalCooldown(): Int {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setPortalCooldown(cooldown: Int) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getScoreboardTags(): Set<String> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addScoreboardTag(tag: String): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun removeScoreboardTag(tag: String): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPistonMoveReaction(): PistonMoveReaction {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getFacing(): BlockFace {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPose(): Pose {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getSpawnCategory(): SpawnCategory {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun teamDisplayName(): Component {
        TODO("Not yet implemented")
    }

    override fun getOrigin(): Location? {
        TODO("Not yet implemented")
    }

    override fun fromMobSpawner(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getEntitySpawnReason(): CreatureSpawnEvent.SpawnReason {
        TODO("Not yet implemented")
    }

    override fun isUnderWater(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInRain(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInBubbleColumn(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInWaterOrRain(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInWaterOrBubbleColumn(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInWaterOrRainOrBubbleColumn(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInLava(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTicking(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getTrackedPlayers(): MutableSet<Player> {
        TODO("Not yet implemented")
    }

    override fun spawnAt(location: Location, reason: CreatureSpawnEvent.SpawnReason): Boolean {
        TODO("Not yet implemented")
    }

    override fun isInPowderedSnow(): Boolean {
        TODO("Not yet implemented")
    }

    override fun collidesAt(location: Location): Boolean {
        TODO("Not yet implemented")
    }

    override fun wouldCollideUsing(boundingBox: BoundingBox): Boolean {
        TODO("Not yet implemented")
    }

    override fun getScheduler(): EntityScheduler {
        TODO("Not yet implemented")
    }

    override fun setMetadata(metadataKey: String, newMetadataValue: MetadataValue) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getMetadata(metadataKey: String): List<MetadataValue> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun hasMetadata(metadataKey: String): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun removeMetadata(metadataKey: String, owningPlugin: Plugin) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendMessage(message: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendMessage(messages: Array<String>) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendMessage(sender: UUID?, message: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendMessage(sender: UUID?, messages: Array<String>) {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun sendActionBar(message: String) {
        TODO("Not yet implemented")
    }

    override fun sendActionBar(alternateChar: Char, message: String) {
        TODO("Not yet implemented")
    }

    override fun sendActionBar(vararg message: BaseComponent) {
        TODO("Not yet implemented")
    }

    override fun showTitle(title: Array<out BaseComponent?>?) {
        TODO("Not yet implemented")
    }

    override fun showTitle(title: BaseComponent?) {
        TODO("Not yet implemented")
    }

    override fun showTitle(
        title: Array<out BaseComponent?>?,
        subtitle: Array<out BaseComponent?>?,
        fadeInTicks: Int,
        stayTicks: Int,
        fadeOutTicks: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun showTitle(
        title: BaseComponent?,
        subtitle: BaseComponent?,
        fadeInTicks: Int,
        stayTicks: Int,
        fadeOutTicks: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun addAttachment(plugin: Plugin, name: String, value: Boolean): PermissionAttachment {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addAttachment(plugin: Plugin, name: String, value: Boolean, ticks: Int): PermissionAttachment? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun addAttachment(plugin: Plugin, ticks: Int): PermissionAttachment? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun removeAttachment(attachment: PermissionAttachment) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun recalculatePermissions() {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getCustomName(): String? {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun setCustomName(name: String?) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getPersistentDataContainer(): PersistentDataContainer {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T : Projectile> launchProjectile(projectile: Class<out T?>): T {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun <T : Projectile> launchProjectile(projectile: Class<out T?>, velocity: Vector?): T {
        throw UnsupportedOperationException("Player is not online")
    }

    override fun <T : Projectile?> launchProjectile(
        projectile: Class<out T>,
        velocity: Vector?,
        function: Consumer<T>?
    ): T {
        TODO("Not yet implemented")
    }

    override fun getFrictionState(): TriState {
        TODO("Not yet implemented")
    }

    override fun setFrictionState(state: TriState) {
        TODO("Not yet implemented")
    }

    override fun isConversing(): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun acceptConversationInput(input: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun beginConversation(conversation: Conversation): Boolean {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun abandonConversation(conversation: Conversation) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun abandonConversation(conversation: Conversation, details: ConversationAbandonedEvent) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendRawMessage(sender: UUID?, message: String) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun sendPluginMessage(source: Plugin, channel: String, message: ByteArray) {
        throw UnsupportedOperationException("Player is not online")
    }
    
    override fun getListeningPluginChannels(): Set<String> {
        throw UnsupportedOperationException("Player is not online")
    }
    
}