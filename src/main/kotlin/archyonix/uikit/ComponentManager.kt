package archyonix.uikit

import archyonix.uikit.components.UIButton
import archyonix.uikit.components.UIComponent
import archyonix.uikit.components.UIList
import archyonix.uikit.components.UIView

class ComponentManager private constructor() {
    companion object {
        private val components: MutableMap<String, Class<out UIComponent>> =
            mutableMapOf(
                Pair("UIView", UIView::class.java),
                Pair("UIButton", UIButton::class.java),
                Pair("UIList", UIList::class.java),
            )

        fun registerComponent(name: String, componentClass: Class<UIComponent>) {
            components[name] = componentClass
        }

        fun newComponentByName(name: String, id: String): UIComponent {
            val componentClass = components[name]
                ?: throw RuntimeException("Component with name '$name' is not registered")
            return componentClass
                .getDeclaredConstructor(String::class.java)
                .newInstance(id)
        }
    }
}