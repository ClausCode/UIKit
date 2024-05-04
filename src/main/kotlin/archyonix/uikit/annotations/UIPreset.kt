package archyonix.uikit.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class UIPreset(
    val path: String,
    val isResource: Boolean = false
)
