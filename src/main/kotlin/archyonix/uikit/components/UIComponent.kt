package archyonix.uikit.components

import archyonix.uikit.UI
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

abstract class UIComponent(
    val id: String
) {
    var order: Int = 0
    open fun action(ui: UI, event: InventoryPreClickEvent) {}
    abstract fun importFromConfig(config: ConfigurationSection): UIComponent
    abstract fun render(ui: UI, inventory: Inventory)
    fun withOrder(order: Int): UIComponent {
        this.order = order
        return this
    }
}

val ComponentIdTag = Tag.String("component-id")