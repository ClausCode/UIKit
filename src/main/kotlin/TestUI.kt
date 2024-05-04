import archyonix.uikit.UI
import archyonix.uikit.annotations.UIPreset
import archyonix.uikit.components.UIButton
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "ui.yaml", isResource = false)
class TestUI(viewer: Player) : UI(viewer, InventoryType.CHEST_6_ROW) {
    init {
        findComponent<UIButton>("component-id")
            ?.onAction { viewer.sendMessage(it.id) }
            ?.withProperty("userId", viewer.username)
            ?.withProperty("score", viewer.health.toString())
    }
}