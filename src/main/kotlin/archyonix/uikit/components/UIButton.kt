package archyonix.uikit.components

import archyonix.uikit.UI
import net.minestom.server.event.inventory.InventoryPreClickEvent

class UIButton(id: String) : UIView(id) {
    private var onAction: (component: UIButton) -> Unit = {}
    override fun action(ui: UI, event: InventoryPreClickEvent) {
        onAction(this)
    }

    fun onAction(onActionFunc: (component: UIButton) -> Unit): UIButton {
        this.onAction = onActionFunc
        return this
    }

    override fun withView(isView: Boolean): UIButton {
        return super.withView(isView) as UIButton
    }

    override fun withIcon(icon: String): UIButton {
        return super.withIcon(icon) as UIButton
    }

    override fun withDisplayName(displayName: String): UIButton {
        return super.withDisplayName(displayName) as UIButton
    }

    override fun withDescription(description: List<String>): UIButton {
        return super.withDescription(description) as UIButton
    }

    override fun withPosition(x: Int, y: Int): UIButton {
        return super.withPosition(x, y) as UIButton
    }

    override fun withAmount(amount: Int): UIButton {
        return super.withAmount(amount) as UIButton
    }

    override fun withProperty(name: String, value: String): UIButton {
        return super.withProperty(name, value) as UIButton
    }

    override fun withX(x: Int): UIButton {
        return super.withX(x) as UIButton
    }

    override fun withY(y: Int): UIButton {
        return super.withY(y) as UIButton
    }

    override fun withTag(name: String, value: String): UIButton {
        return super.withTag(name, value) as UIButton
    }

    override fun onRender(onRenderFunc: (component: UIView) -> Unit): UIButton {
        return super.onRender(onRenderFunc) as UIButton
    }
}