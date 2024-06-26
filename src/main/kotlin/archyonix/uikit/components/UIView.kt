package archyonix.uikit.components

import archyonix.uikit.UI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.max
import kotlin.math.min

open class UIView(
    id: String
) : UIComponent(id) {
    var x: Int = 0
    var y: Int = 0
    var hasView: Boolean = true
    var icon: String = "minecraft:stone"
    var amount: Int = 1
    var displayName: String = "View"
    var description: List<String> = emptyList()
    val properties: MutableMap<String, String> = HashMap()
    val tags: MutableMap<String, String> = HashMap()
    var onRender: (component: UIView) -> UIView = { this }

    override fun render(ui: UI, inventory: Inventory) {
        if (!hasView) return

        val slot = y * 9 + x
        if (slot >= inventory.size) return
        inventory.setItemStack(slot, onRender(clone()).createItemStack(ui.colors))
    }

    private fun createItemStack(colors: Map<String, TextColor>): ItemStack {
        val material = try {
            Material.fromNamespaceId(icon)!!
        } catch (e: Exception) {
            e.printStackTrace()
            Material.STONE
        }

        var itemStack = ItemStack
            .of(material, amount)
            .withDisplayName(
                displayName
                    .withProperties(properties)
                    .withColors(colors)
            )
            .withLore(
                description
                    .map { line ->
                        line
                            .withProperties(properties)
                            .withColors(colors)
                    }
            )

        tags.forEach { (key, value) ->
            itemStack = itemStack.withTag(
                Tag.String(key), value
            )
        }


        return itemStack
            .withTag(ComponentIdTag, id)
            .withMeta {
                it.unbreakable(true)
                it.hideFlag(
                    ItemHideFlag.HIDE_ATTRIBUTES,
                    ItemHideFlag.HIDE_ENCHANTS,
                    ItemHideFlag.HIDE_UNBREAKABLE
                )
            }
    }

    open fun withView(isView: Boolean): UIView {
        this.hasView = isView
        return this
    }

    open fun withIcon(icon: String): UIView {
        this.icon = icon
        return this
    }

    open fun withDisplayName(displayName: String): UIView {
        this.displayName = displayName
        return this
    }

    open fun withDescription(description: List<String>): UIView {
        this.description = description
        return this
    }

    open fun withPosition(x: Int, y: Int): UIView {
        this.x = x
        this.y = y
        return this
    }

    open fun withAmount(amount: Int): UIView {
        this.amount = max(1, min(amount, 127))
        return this
    }

    open fun withProperty(name: String, value: String): UIView {
        this.properties[name] = value
        return this
    }

    open fun withX(x: Int): UIView {
        this.x = max(0, min(x, 8))
        return this
    }

    open fun withY(y: Int): UIView {
        this.y = max(0, min(y, 5))
        return this
    }

    open fun withTag(name: String, value: String): UIView {
        this.tags[name] = value
        return this
    }

    private var onAction: (component: UIView) -> Unit = {}
    override fun action(ui: UI, event: InventoryPreClickEvent) {
        onAction(this)
    }

    open fun onAction(onActionFunc: (component: UIView) -> Unit): UIView {
        this.onAction = onActionFunc
        return this
    }

    open fun onRender(onRenderFunc: (component: UIView) -> UIView): UIView {
        this.onRender = onRenderFunc
        return this
    }

    override fun importFromConfig(config: ConfigurationSection): UIComponent {
        var view = this

        val tagSection = config.getConfigurationSection("tags")
        if (tagSection != null) {
            for (key in tagSection.getKeys(false)) {
                view = view.withTag(key, tagSection.getString(key) ?: "")
            }
        }

        return view
            .withDisplayName(config.getString("displayName") ?: view.displayName)
            .withDescription(config.getStringList("description"))
            .withIcon(config.getString("icon") ?: view.icon)
            .withAmount(config.getInt("amount"))
            .withX(config.getString("pos")?.split(":")?.get(0)?.toInt() ?: 0)
            .withY(config.getString("pos")?.split(":")?.get(1)?.toInt() ?: 0)
            .withView(config.getBoolean("isView", true))
            .withOrder(config.getInt("order"))
    }

    fun clone(): UIView {
        val view = UIView(id)
            .withX(x)
            .withY(y)
            .withView(hasView)
            .withAmount(amount)
            .withDisplayName(displayName)
            .withDescription(description)
            .withIcon(icon)
        view.properties.putAll(properties)
        view.tags.putAll(tags)
        return view
    }
}

fun String.getUIPosX() = split(":")[0].toInt()
fun String.getUIPosY() = split(":")[1].toInt()

fun String.withProperties(props: Map<String, String>): String {
    var resultString = this
    for (key in props.keys) {
        resultString = resultString.replace("%$key%", props[key]!!)
    }
    return resultString
}

fun String.withColors(colors: Map<String, TextColor>): Component {
    if (colors.isEmpty()) return Component.text("§r$this").decoration(TextDecoration.ITALIC, false)

    val components: MutableList<Component> = mutableListOf()
    for (line in split("<")) {
        var resultLine = line
        var color: TextColor? = null
        for (colorName in colors.keys) {
            if (line.startsWith("$colorName>")) {
                resultLine = line.replace("$colorName>", "")
                color = colors[colorName]
                break
            }
        }
        if (color == null) components.add(Component.text("§r$resultLine"))
        else components.add(Component.text("§r$resultLine", color))
    }
    return Component.join(JoinConfiguration.noSeparators(), components)
        .decoration(TextDecoration.ITALIC, false)
}