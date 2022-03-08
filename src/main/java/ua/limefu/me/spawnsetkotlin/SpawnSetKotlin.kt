package ua.limefu.me.spawnsetkotlin

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ua.limefu.me.spawnsetkotlin.listeners.PlayerJoinListener
import ua.limefu.me.spawnsetkotlin.listeners.TeleportCancelListener
import java.io.IOException
import javax.sound.midi.Patch

class SpawnSetKotlin : JavaPlugin() {
    var teleportCancelListener: TeleportCancelListener? = null
    private var playerJoinListener: PlayerJoinListener? = null

    @JvmField
    var cm: ConfigsManager? = null

    override fun onEnable() {
        System.console().printf("=== [ SpawnSet by LimeFu] === \n")
        System.console().printf("============================== \n")
        teleportCancelListener = TeleportCancelListener(this)
        playerJoinListener = PlayerJoinListener(this)
        cm = ConfigsManager(this)
        cm!!.registerConfig("config", "config.yml")
        cm!!.registerConfig("messages", "messages.yml")
        cm!!.registerConfig("spawn", "spawn.yml")
        cm!!.loadAll()
        cm!!.saveAll()
    }

    override fun onDisable() {

    }

    fun getMessage(patch: String): String? {
        return cm?.getConfig("messages")?.getString(patch)?.replace("&".toRegex(), "" + ChatColor.COLOR_CHAR)
    }
    val SpawnLocation: Location?
    get() {
        val spawnCfg = cm!!.getConfig("spawn")
        if (spawnCfg != null) {
            if (spawnCfg.getString("world") == null) return null
        }
        val spawn = Location(null, 0.0, 0.0, 0.0)
        if (spawnCfg != null) {
            spawn.x = spawnCfg.getDouble("x")
        }
        if (spawnCfg != null) {
            spawn.y = spawnCfg.getDouble("y")
        }
        if (spawnCfg != null) {
            spawn.z = spawnCfg.getDouble("z")
        }
        if (spawnCfg != null) {
            spawn.world = Bukkit.getWorld(spawnCfg.getString("world"))
        }
        if (spawnCfg != null) {
            spawn.yaw = spawnCfg.getInt("yaw").toFloat()
        }
        if (spawnCfg != null) {
            spawn.pitch = spawnCfg.getInt("pitch").toFloat()
        }
        return spawn
    }
    private fun setSpawnLocation(world: String?, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        val spawnCfg = cm?.getConfig("spawn")
        if (spawnCfg != null) {
            spawnCfg.set("world", world)
        }
        if (spawnCfg != null) {
            spawnCfg.set("x", x)
        }
        if (spawnCfg != null) {
            spawnCfg.set("y", y)
        }
        if (spawnCfg != null) {
            spawnCfg.set("z", z)
        }
        if (spawnCfg != null) {
            spawnCfg.set("yaw", yaw)
        }
        if (spawnCfg != null) {
            spawnCfg.set("pitch", pitch)
        }
        try {
            if (spawnCfg != null) {
                spawnCfg.save()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, Label: String, args: Array<String>): Boolean {
        if (cmd.name.equals("setspawn", ignoreCase = true)) {
            if (sender is Player) {
                val p = sender
                if (cm!!.getConfig("config")!!.getBoolean("permissions.disable") || p.hasPermission("setspawn.setspawn")) {
                    p.sendMessage(getMessage("messages.spawnset"))
                    val l = p.getLocation()
                    setSpawnLocation(l.world.name, l.x, l.y, l.z, l.yaw, l.pitch)
                } else {
                    p.sendMessage(getMessage("messages.permissions"))
                }
            } else {
                sender.sendMessage("[SetSpawn] You must be player to execute this command.")
            }
        } else if (cmd.name.equals("spawn", ignoreCase = true)) {
            if (sender is Player) {
                val p = sender
                val spawn = SpawnLocation
                if (spawn == null) {
                    p.sendMessage("[Error] Spawn is not set.")
                    p.sendMessage("If you are Administrator, you can set spawn using /setspawn.")
                    return true
                }
                if (cm!!.getConfig("config")!!.getBoolean("permissions.disable") || p.hasPermission("setspawn.spawn")) {
                    if (cm!!.getConfig("config")!!.getBoolean("teleport.cooldown_enabled")) {
                        p.sendMessage(
                            getMessage("messages.pleasewait")?.replace("\\{1\\}".toRegex(), "" + (cm!!.getConfig("config")
                                ?.getLong("teleport.cooldown")))
                        )
                        if (cm!!.getConfig("config")!!.getBoolean("messages.spawn")) {
                            teleportPlayerWithDelay(p, cm!!.getConfig("config")!!.getLong("teleport.cooldown"), spawn, getMessage("messages.spawn"), null)
                        } else {
                            teleportPlayerWithDelay(p, cm!!.getConfig("config")!!.getLong("teleport.cooldown"), spawn, null, null)
                        }
                    } else {
                        if (cm!!.getConfig("config")!!.getBoolean("messages.spawn")) {
                            p.sendMessage(getMessage("messages.spawn"))
                        }
                        p.teleport(spawn)
                    }
                    val loc = p.location
                    if (cm!!.getConfig("config")!!.getBoolean("effects.mobspawner")) {
                        p.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 6)
                    }
                    if (cm!!.getConfig("config")!!.getBoolean("effects.smoke")) {
                        p.playEffect(loc, Effect.SMOKE, 6)
                    }
                    if (cm!!.getConfig("config")!!.getBoolean("effects.slime")) {
                        p.playEffect(loc, Effect.SLIME, 6)
                    }
                    if (cm!!.getConfig("config")!!.getBoolean("effects.potion")) {
                        p.playEffect(loc, Effect.POTION_BREAK, 6)
                    }
                } else {
                    p.sendMessage(getMessage("messages.permissions"))
                }
            } else {
                sender.sendMessage("[SetSpawn] You must be player to execute this command.")
            }
        } else if (cmd.name.equals("rspawn", ignoreCase = true)) {
            if (sender is Player) {
                if (cm!!.getConfig("config")!!.getBoolean("permissions.disable") || sender.hasPermission("setspawn.rspawn")) {
                    val p = sender
                    if (cm!!.getConfig("config")!!.getBoolean("permissions.disable")) {
                        if (!p.isOp) {
                            p.sendMessage("You must be op to execute /rspawn command.")
                            return true
                        }
                    }
                    p.sendMessage("[SetSpawn] Configuration reloaded.")
                    cm!!.save("spawn")
                    cm!!.loadAll()
                } else {
                    sender.sendMessage(getMessage("messages.permissions"))
                }
            } else {
                sender.sendMessage("[SetSpawn] Configuration reloaded.")
                cm!!.save("spawn")
                cm!!.loadAll()
            }
        }
        return true
    }

    companion object {
        private var plugin: SpawnSetKotlin? = null
        fun teleportPlayerWithDelay(player: Player, l: Long, location: Location?, messageAfterTp: String?, postTeleport: Runnable?) {
            if (plugin!!.teleportCancelListener!!.playerTeleportLocation[player] != null) {
                plugin!!.teleportCancelListener!!.playerTeleportLocation.remove(player)
            }
            val task = plugin!!.server.scheduler.runTaskLater(plugin, {
                if (player.isOnline) {
                    player.teleport(location)
                    if (messageAfterTp != null) {
                        player.sendMessage(messageAfterTp)
                    }
                    postTeleport?.run()
                }
            }, l * 20L)
            plugin!!.teleportCancelListener!!.playerTeleportLocation[player] = task
        }
    }
}