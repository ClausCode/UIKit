package archyonix.uikit

import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.*

abstract class UI(
    val viewer: Player,
    inventoryType: InventoryType,
) {
    private var displayName: String = "UI"
    val colors: MutableMap<String, TextColor> = HashMap()
    private val inventory: Inventory = Inventory(inventoryType, "")
    private val handler: EventNode<Event> = EventNode.all(UUID.randomUUID().toString())
    private val components: MutableSet<UIComponent> = HashSet()

    init {
        if (javaClass.isAnnotationPresent(UIPreset::class.java)) importPreset()

        handler.addListener(InventoryCloseEvent::class.java) { event ->
            if (event.inventory != null && event.inventory!! == inventory) {
                MinecraftServer.getGlobalEventHandler().removeChild(handler)
            }
        }

        handler.addListener(InventoryPreClickEvent::class.java) { event ->
            if (event.inventory == inventory) event.isCancelled = true
        }
    }

    fun show() {
        inventory.title = displayName.withColors(colors)
        viewer.openInventory(inventory)
        render()

        MinecraftServer.getGlobalEventHandler().addChild(handler)
    }

    fun render() = components
        .sortedBy { it.order }
        .forEach { it.render(this, inventory) }

    fun withColor(name: String, color: TextColor): UI {
        this.colors[name] = color
        return this
    }

    fun withComponent(component: UIComponent): UI {
        this.components.add(component)
        return this
    }

    fun removeComponent(component: UIComponent) {
        this.components.remove(component)
    }

    fun removeComponent(id: String) {
        this.components.removeIf { it.id == id }
    }

    fun <T : UIComponent> findComponent(id: String): T {
        return this.components.find { it.id == id } as T
    }

    fun withColor(name: String, colorHEX: String): UI {
        this.colors[name] = TextColor.fromHexString(colorHEX)!!
        return this
    }

    fun withDisplayName(displayName: String): UI {
        this.displayName = displayName
        return this
    }

    private fun importPreset() {
        val preset = javaClass.getAnnotation(UIPreset::class.java)
        val inputStream: InputStreamReader = if (preset.isResource) {
            InputStreamReader(javaClass.classLoader.getResourceAsStream(preset.path)!!)
        } else {
            FileReader(File(preset.path))
        }
        val config = YamlConfiguration.loadConfiguration(inputStream)
        this.displayName = config.getString("displayName") ?: displayName

        val colorsSection = config.getConfigurationSection("colors")
        if (colorsSection != null) for (key in colorsSection.getKeys(false)) {
            withColor(key, colorsSection.getString(key) ?: "#8e8f90")
        }

        val componentsSection = config.getConfigurationSection("components")
        if (componentsSection != null) for (id in componentsSection.getKeys(false)) {
            val section = componentsSection.getConfigurationSection(id)!!
            val componentName = section.getString("component") ?: continue
            withComponent(
                ComponentManager
                    .newComponentByName(componentName, id)
                    .importFromConfig(section)
            )
        }
    }
}