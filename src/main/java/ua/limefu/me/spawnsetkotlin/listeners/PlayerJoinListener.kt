package ua.limefu.me.spawnsetkotlin.listeners

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ua.limefu.me.spawnsetkotlin.SpawnSetKotlin

class PlayerJoinListener(private var plugin: SpawnSetKotlin): Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val p = event.player
        if (p != null) {
            if (plugin.cm!!.getConfig("config")!!.getBoolean("teleport.everyjoin")) {
                val spawn = plugin.SpawnLocation
                if (spawn == null) {
                    p.sendMessage(ChatColor.RED.toString() + "SetSpawn Error:")
                    p.sendMessage("SPAWN IS NOT SET!!!")
                    p.sendMessage("If you are Administrator, you can set spawn using /setspawn.")
                } else {
                    p.teleport(spawn)
                }
            } else {
                if (!p.hasPlayedBefore()) {
                    val spawn = plugin.SpawnLocation
                    if (spawn == null) {
                        p.sendMessage(ChatColor.RED.toString() + "SetSpawn Error:")
                        p.sendMessage("SPAWN IS NOT SET!!!")
                        p.sendMessage("If you are Administrator, you can set spawn using /setspawn.")
                    } else {
                        p.teleport(spawn)
                    }
                }
            }
        }
    }



}