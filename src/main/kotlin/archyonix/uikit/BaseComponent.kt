package archyonix.uikit

import net.minestom.server.inventory.Inventory
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

abstract class BaseComponent<T>(
    val id: String
) {
    var order: Int = 0
    open val ComponentIdTag = Tag.String("component-id")

    fun withOrder(order: Int): T {
        this.order = order
        return this as T
    }
    abstract fun render(ui: UI, inventory: Inventory)
    abstract fun importFromConfig(config: ConfigurationSection): T
}