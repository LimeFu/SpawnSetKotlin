package ua.limefu.me.spawnsetkotlin

import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.util.ArrayList

class ConfigsManager(private val plugin: JavaPlugin) {
    private val configs: MutableList<RConfig?> = ArrayList()
    fun registerConfig(id: String, fileName: String): Boolean {
        val file = File(plugin.dataFolder, fileName)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            try {
                copy(plugin.getResource(fileName), file)
            } catch (e: Exception) {

            }
        }
        configs.add(RConfig(id, file))
        return true
    }
    fun unregisterConfig(id: String): Boolean {
        return configs.remove(getConfig(id))
    }

    fun getConfig(id: String?) : RConfig? {
        for (c in configs) {
            if (c!!.configId.equals(id, ignoreCase = true)) return c
        }
        return null
    }
    fun saveAll() : Boolean {
        try {
            for (c in configs) {
                if (c != null) {
                    c.save()
                }
            }
        } catch (e : Exception) {
            print("Error While saving all configs")
            e.printStackTrace()
            return false
        }
        return true
    }
    fun save(id: String) : Boolean {
        val c = getConfig(id)
        try {
            if (c != null) {
                c.save()
            }
        } catch (e : Exception) {
            print("Error occurred while saving a config with id $id")
            e.printStackTrace()
            return false
        }
        return true
    }
    fun loadAll() : Boolean {
        try {
            for (c in configs) {
                if (c != null) {
                    c.load()
                }
            }
        } catch (e : Exception) {
            print("Error occured while loading all configs")
            e.printStackTrace()
            return false
        }
        return false
    }
    fun load(id: String) : Boolean {
        try {
            getConfig(id)?.load()
        } catch (e: Exception) {
            print("Error occured while loading a config with id $id")
            e.printStackTrace()
            return false
        }
        return true
    }
     private fun print(msg: String) {
         plugin.logger.info("ConfigManager: $msg")
     }
    private fun copy(inp: InputStream, file: File) {
        try {
            val out: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int

            while (inp.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            out.close()
            inp.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    inner class RConfig(val configId: String, private val file: File) : YamlConfiguration() {
        @Throws(IOException::class)
        fun save() {
            save(file)
        }

        @Throws(InvalidConfigurationException::class, IOException::class)
        fun load() {
            load(file)
        }


        }
    }

