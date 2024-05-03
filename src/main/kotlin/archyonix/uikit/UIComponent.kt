package archyonix.uikit

import net.minestom.server.inventory.Inventory
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

abstract class UIComponent(
    val id: String
) {
    var order: Int = 0
    abstract fun importFromConfig(config: ConfigurationSection): UIComponent
    abstract fun render(ui: UI, inventory: Inventory)
    fun withOrder(order: Int): UIComponent {
        this.order = order
        return this
    }

    open val ComponentIdTag = Tag.String("component-id")
}
