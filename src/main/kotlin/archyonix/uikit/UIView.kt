package archyonix.uikit

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.TextColor
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
    private var x: Int = 0
    private var y: Int = 0
    private var hasView: Boolean = true
    private var icon: String = "minecraft:stone"
    private var amount: Int = 1
    private var displayName: String = "View"
    private var description: List<String> = emptyList()
    private val properties: MutableMap<String, String> = HashMap()
    private val tags: MutableMap<String, String> = HashMap()
    private var onRender: (component: UIView) -> Unit = {}

    override fun render(ui: UI, inventory: Inventory) {
        if (!hasView) return

        val slot = y * 9 + x
        if (slot >= inventory.size) return
        onRender(this)
        inventory.setItemStack(slot, createItemStack(ui.colors))
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

    fun withView(isView: Boolean): UIView {
        this.hasView = isView
        return this
    }

    fun withIcon(icon: String): UIView {
        this.icon = icon
        return this
    }

    fun withDisplayName(displayName: String): UIView {
        this.displayName = displayName
        return this
    }

    fun withDescription(description: List<String>): UIView {
        this.description = description
        return this
    }

    fun withPosition(x: Int, y: Int): UIView {
        this.x = x
        this.y = y
        return this
    }

    fun withAmount(amount: Int): UIView {
        this.amount = max(1, min(amount, 127))
        return this
    }

    fun withProperty(name: String, value: String): UIView {
        this.properties[name] = value
        return this
    }

    fun withX(x: Int): UIView {
        this.x = max(0, min(x, 8))
        return this
    }

    fun withY(y: Int): UIView {
        this.y = max(0, min(y, 5))
        return this
    }

    fun withTag(name: String, value: String): UIView {
        this.tags[name] = value
        return this
    }

    fun onRender(onRenderFunc: (component: UIView) -> Unit): UIView {
        this.onRender = onRenderFunc
        return this
    }

    override fun importFromConfig(config: ConfigurationSection): UIComponent {
        var view = UIView(config.name)

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
            .withView(config.getBoolean("isView", true))
            .withOrder(config.getInt("order"))
    }
}

fun String.withProperties(props: Map<String, String>): String {
    var resultString = this
    for (key in props.keys) {
        resultString = resultString.replace("%$key%", props[key]!!)
    }
    return resultString
}

fun String.withColors(colors: Map<String, TextColor>): Component {
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
        components.add(Component.text(resultLine, color ?: TextColor.fromHexString("#8e8f90")))
    }
    return Component.join(JoinConfiguration.noSeparators(), components)
}