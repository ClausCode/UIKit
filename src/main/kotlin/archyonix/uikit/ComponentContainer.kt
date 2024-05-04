package archyonix.uikit

import archyonix.uikit.components.UIComponent
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection

interface ComponentContainer<T: UIComponent> {
    val components: MutableList<T>

    fun withComponent(component: T): ComponentContainer<T> {
        this.components.add(component)
        return this
    }

    fun removeComponent(component: T) {
        this.components.remove(component)
    }

    fun removeComponent(id: String) {
        this.components.removeIf { it.id == id }
    }

    fun <R : T> findComponent(id: String): R? {
        return this.components.find { it.id == id } as R
    }

    fun loadComponents(config: ConfigurationSection): MutableList<T> {
        val components: MutableList<T> = ArrayList()
        val componentsSection = config.getConfigurationSection("components")
        if (componentsSection != null) for (id in componentsSection.getKeys(false)) {
            val section = componentsSection.getConfigurationSection(id)!!
            val componentName = section.getString("component") ?: continue
            components.add(
                ComponentManager
                    .newComponentByName(componentName, id)
                    .importFromConfig(section) as T
            )
        }
        return components
    }
}