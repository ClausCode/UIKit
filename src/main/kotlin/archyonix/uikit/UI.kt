package archyonix.uikit

import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.ComponentIdTag
import archyonix.uikit.components.UIComponent
import archyonix.uikit.components.withColors
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
) : ComponentContainer<UIComponent> {
    override val components: MutableList<UIComponent> = ArrayList()
    private var displayName: String = "UI"
    val colors: MutableMap<String, TextColor> = HashMap()
    private val inventory: Inventory = Inventory(inventoryType, "")
    private val handler: EventNode<Event> = EventNode.all(UUID.randomUUID().toString())

    init {
        if (javaClass.isAnnotationPresent(UIPreset::class.java)) importPreset()

        handler.addListener(InventoryCloseEvent::class.java) { event ->
            if (event.player.username == viewer.username) {
                MinecraftServer.getGlobalEventHandler().removeChild(handler)
            }
        }

        handler.addListener(InventoryPreClickEvent::class.java) { event ->
            if (event.player.username != viewer.username) return@addListener
            if (event.inventory == inventory) event.isCancelled = true
            if (event.slot < 0 || event.slot >= inventoryType.size) return@addListener
            val itemStack = inventory.getItemStack(event.slot)
            if (!itemStack.hasTag(ComponentIdTag)) return@addListener
            val componentId = itemStack.getTag(ComponentIdTag)!!
            val component = findComponent<UIComponent>(componentId) ?: return@addListener
            component.action(this, event)
        }
    }

    fun show() {
        inventory.title = displayName.withColors(colors)
        viewer.openInventory(inventory)
        render()

        MinecraftServer.getGlobalEventHandler().addChild(handler)
    }

    fun close() {
        MinecraftServer.getGlobalEventHandler().removeChild(handler)
        viewer.closeInventory()
    }

    fun render() = components
        .sortedBy { it.order }
        .forEach { it.render(this, inventory) }

    fun withColor(name: String, color: TextColor): UI {
        this.colors[name] = color
        return this
    }

    fun withColor(name: String, colorHEX: String): UI {
        this.colors[name] = TextColor.fromHexString(colorHEX)!!
        return this
    }

    fun withDisplayName(displayName: String): UI {
        this.displayName = displayName
        return this
    }

    override fun <R : UIComponent> findComponent(id: String): R? {
        val target = this.components.find { it.id == id }
        if (target != null) return target as R

        val containerList = this.components
            .filter { it is ComponentContainer<*> }
            .map { it as ComponentContainer<*> }

        for (container in containerList) {
            val target = container.findComponent(id) as UIComponent?
            if (target != null) return target as R
        }

        return null
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

        loadComponents(config).forEach { withComponent(it) }
    }
}