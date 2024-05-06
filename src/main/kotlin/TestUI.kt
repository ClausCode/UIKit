import archyonix.uikit.UI
import archyonix.uikit.annotations.UIPreset
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

@UIPreset(path = "ui.yaml", isResource = false)
class TestUI(viewer: Player) : UI(viewer, InventoryType.CHEST_6_ROW) {
}