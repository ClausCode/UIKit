package archyonix.uikit

import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.*

abstract class UI(
    inventoryType: InventoryType,
) {
    private var displayName: String = "UI"
    private val colors: MutableMap<String, TextColor> = HashMap()
    private val inventory: Inventory = Inventory(inventoryType, "")
    private val handler: EventNode<Event> = EventNode.all(UUID.randomUUID().toString())

    init {
        if (javaClass.isAnnotationPresent(UIPreset::class.java)) importPreset()

        handler.addListener(InventoryCloseEvent::class.java) { event ->
            if (event.inventory != null && event.inventory!! == inventory) {
                MinecraftServer.getGlobalEventHandler().removeChild(handler)
            }
        }
    }

    fun show(viewer: Player) {
        MinecraftServer.getGlobalEventHandler().addChild(handler)
    }

    fun withColor(name: String, color: TextColor): UI {
        this.colors[name] = color
        return this
    }

    fun withDisplayName(displayName: String): UI {
        this.displayName = displayName
        return this
    }

    fun importPreset() {
        val preset = javaClass.getAnnotation(UIPreset::class.java)
        val inputStream: InputStreamReader = if (preset.isResource) {
            InputStreamReader(javaClass.classLoader.getResourceAsStream(preset.path))
        } else {
            FileReader(File(preset.path))
        }
        val config = YamlConfiguration.loadConfiguration(inputStream)
    }
}