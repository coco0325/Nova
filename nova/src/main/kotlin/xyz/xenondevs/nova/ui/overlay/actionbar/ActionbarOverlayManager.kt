package xyz.xenondevs.nova.ui.overlay.actionbar

import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import xyz.xenondevs.nmsutils.network.event.PacketEventManager
import xyz.xenondevs.nmsutils.network.event.PacketHandler
import xyz.xenondevs.nmsutils.network.event.clientbound.ClientboundActionBarPacketEvent
import xyz.xenondevs.nmsutils.network.event.clientbound.ClientboundSystemChatPacketEvent
import xyz.xenondevs.nova.data.config.DEFAULT_CONFIG
import xyz.xenondevs.nova.data.resources.CharSizes
import xyz.xenondevs.nova.util.Task
import xyz.xenondevs.nova.util.component.adventure.move
import xyz.xenondevs.nova.util.runTaskTimer
import xyz.xenondevs.nova.util.send
import java.util.*
import net.minecraft.network.chat.Component as MojangComponent

object ActionbarOverlayManager {
    
    private var tickTask: Task? = null
    
    private val EMPTY_ACTION_BAR_PACKET = ClientboundSetActionBarTextPacket(MojangComponent.empty())
    private val overlays = HashMap<UUID, HashSet<ActionbarOverlay>>()
    private val interceptedActionbars = HashMap<UUID, Pair<Component, Long>>()
    
    init {
        reload()
    }
    
    internal fun reload() {
        if (tickTask != null) {
            tickTask?.cancel()
            PacketEventManager.unregisterListener(this)
            tickTask = null
        }
        
        if (DEFAULT_CONFIG.getBoolean("overlay.actionbar.enabled")) {
            PacketEventManager.registerListener(this)
            tickTask = runTaskTimer(0, 1, ActionbarOverlayManager::handleTick)
        }
    }
    
    fun registerOverlay(player: Player, overlay: ActionbarOverlay) {
        overlays.getOrPut(player.uniqueId) { HashSet() } += overlay
    }
    
    fun unregisterOverlay(player: Player, overlay: ActionbarOverlay) {
        val playerOverlays = overlays[player.uniqueId]
        if (playerOverlays != null) {
            playerOverlays -= overlay
            if (playerOverlays.isEmpty()) {
                overlays.remove(player.uniqueId)
                player.send(EMPTY_ACTION_BAR_PACKET)
            }
        }
    }
    
    /**
     * Every tick, we send empty actionbar packets to all players in the [overlays] map.
     * These packets will the get intercepted in the [handleChatPacket] where we can append
     * the characters from the [ActionbarOverlay].
     * We do it this way, so we can overwrite all actionbar messages and prevent flickering
     * between two different plugins trying to send their actionbar.
     */
    private fun handleTick() {
        overlays.keys
            .asSequence()
            .mapNotNull(Bukkit::getPlayer)
            .forEach { it.send(EMPTY_ACTION_BAR_PACKET) }
    }
    
    @PacketHandler
    private fun handleChatPacket(event: ClientboundSystemChatPacketEvent) {
        if (event.overlay) {
            val player = event.player
            val uuid = player.uniqueId
            if (overlays.containsKey(uuid)) {
                saveInterceptedComponent(player, event.adventureMessage)
                event.adventureMessage = getCurrentText(player)
            }
        }
    }
    
    @PacketHandler
    private fun handleChatPacket(event: ClientboundActionBarPacketEvent) {
        val player = event.player
        val uuid = player.uniqueId
        if (overlays.containsKey(uuid)) {
            if (event.packet !== EMPTY_ACTION_BAR_PACKET) {
                saveInterceptedComponent(player, event.adventureText)
            }
            
            event.adventureText = getCurrentText(player)
        }
    }
    
    private fun saveInterceptedComponent(player: Player, text: Component) {
        val mv = CharSizes.calculateComponentWidth(text, player.locale) / -2
        
        val component = Component.text()
            .move(mv) // to center, move the cursor to the right by half of the length
            .append(text)
            .move(mv) // move half of the text length back so the cursor is in the middle of the screen again (prevents client-side centering)
            .build()
        
        interceptedActionbars[player.uniqueId] = component to System.currentTimeMillis()
    }
    
    private fun getCurrentText(player: Player): Component {
        val uuid = player.uniqueId
        val builder = Component.text()
        
        // append custom overlays
        overlays[uuid]!!.forEach {
            builder.append(it.component)
            builder.move(-it.getWidth(player.locale))
        }
        
        // append intercepted actionbar text
        val interceptedActionbar = interceptedActionbars[uuid]
        if (interceptedActionbar != null) {
            val (text, time) = interceptedActionbar
            if (System.currentTimeMillis() - time < 3000) {
                builder.append(text)
            } else interceptedActionbars -= uuid
        }
        
        return builder.build()
    }
    
}