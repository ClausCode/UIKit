import archyonix.uikit.UI
import archyonix.uikit.UIPreset
import archyonix.uikit.UIView
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "ui.yaml", isResource = false)
class TestUI(viewer: Player) : UI(viewer, InventoryType.CHEST_6_ROW) {
    init {
        findComponent<UIView>("component-id")
            .withProperty("userId", viewer.username)
            .withProperty("score", viewer.health.toString())
    }
}