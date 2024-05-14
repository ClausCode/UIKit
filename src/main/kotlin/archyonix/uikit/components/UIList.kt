package archyonix.uikit.components

import archyonix.uikit.ComponentContainer
import archyonix.uikit.UI
import net.minestom.server.inventory.Inventory
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class UIList(id: String) : UIComponent(id), ComponentContainer<UIView> {
    override val components: MutableList<UIView> = ArrayList()
    private var format: ListFormat = ListFormat.FREE
    private var minX: Int = 0
    private var minY: Int = 0
    private var maxX: Int = 0
    private var maxY: Int = 0
    private var page: Int = 0

    fun withStart(x: Int, y: Int): UIList {
        this.minX = max(0, min(x, 8))
        this.minY = max(0, min(x, 5))
        return this
    }

    fun withEnd(x: Int, y: Int): UIList {
        this.maxX = max(0, min(x, 8))
        this.maxY = max(0, min(x, 5))
        return this
    }

    fun withFormat(format: ListFormat): UIList {
        this.format = format
        return this
    }

    fun nextPage(): UIList = setPage(page + 1)
    fun prevPage(): UIList = setPage(page - 1)

    fun setPage(page: Int): UIList {
        this.page = max(0, min(page, findPageCount() - 1))
        return this
    }

    private fun findSlotX(slot: Int) = slot % 9
    private fun findSlotY(slot: Int) = floor(slot / 9.0).toInt()

    private fun findPageSize() = when (format) {
        ListFormat.FREE -> {
            val startSlot = minY * 9 + minX
            val endSlot = maxY * 9 + maxX
            endSlot - startSlot + 1
        }

        ListFormat.STRICT -> {
            val columns = maxX - minX + 1
            val rows = maxY - minY + 1
            columns * rows
        }
    }

    private fun findPageCount() =
        ceil(components.size / findPageSize().toDouble()).toInt()

    override fun render(ui: UI, inventory: Inventory) {
        when (format) {
            ListFormat.FREE -> {
                val startSlot = minY * 9 + minX
                val endSlot = maxY * 9 + maxX

                var index = page * findPageSize()
                for (slot in startSlot..endSlot) {
                    if (index >= components.size) break
                    components[index++]
                        .withX(findSlotX(slot))
                        .withY(findSlotY(slot))
                        .render(ui, inventory)
                }
            }

            ListFormat.STRICT -> {
                var index = findPageSize() * page
                for (y in minY..maxY) {
                    for (x in minX..maxX) {
                        val component = components.getOrNull(index++) ?: return
                        component
                            .withX(x)
                            .withY(y)
                            .render(ui, inventory)
                    }
                }
            }
        }
    }

    override fun importFromConfig(config: ConfigurationSection): UIComponent {
        val startPos = config.getString("startPos") ?: "0:0"
        val endPos = config.getString("endPos") ?: "0:0"

        this.minX = startPos.getUIPosX()
        this.minY = startPos.getUIPosY()
        this.maxX = endPos.getUIPosX()
        this.maxY = endPos.getUIPosY()

        this.format = ListFormat.valueOf(config.getString("format")?.uppercase() ?: "FREE")
        loadComponents(config).forEach { withComponent(it as UIView) }

        return this
    }

    enum class ListFormat { FREE, STRICT }
}