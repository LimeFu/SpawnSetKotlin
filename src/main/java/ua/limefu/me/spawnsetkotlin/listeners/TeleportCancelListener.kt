package ua.limefu.me.spawnsetkotlin.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import ua.limefu.me.spawnsetkotlin.SpawnSetKotlin
import java.util.HashMap

class TeleportCancelListener(private var plugin: SpawnSetKotlin): Listener {
    var playerTeleportLocation: HashMap<Any?, Any?> = HashMap<Any?, Any?>()
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val from = event.from
        val to = event.to
        if (from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ || from.world !== to.world) {
            val player = event.player
            if (playerTeleportLocation[player] != null) {
                playerTeleportLocation.remove(player)!!
                player.sendMessage(plugin.getMessage("messages.canceled"))
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (playerTeleportLocation[player] != null) {
                playerTeleportLocation.remove(player)!!
                player.sendMessage(plugin.getMessage("messages.canceled"))
            }
        }
    }

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }
}


