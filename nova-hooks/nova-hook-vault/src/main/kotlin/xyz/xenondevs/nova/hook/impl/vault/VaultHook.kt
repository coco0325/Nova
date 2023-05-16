package xyz.xenondevs.nova.hook.impl.vault

import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.World
import xyz.xenondevs.nova.integration.Hook
import xyz.xenondevs.nova.integration.permission.PermissionIntegration
import java.util.concurrent.CompletableFuture

@Hook(plugins = ["Vault"], unless = ["LuckPerms"])
internal object VaultHook : PermissionIntegration {
    
    private val PERMISSIONS = Bukkit.getServicesManager().getRegistration(Permission::class.java)!!.provider
    
    override fun hasPermission(world: World, player: OfflinePlayer, permission: String): CompletableFuture<Boolean?> =
        CompletableFuture.completedFuture(PERMISSIONS.playerHas(world.name, player, permission))
    
}