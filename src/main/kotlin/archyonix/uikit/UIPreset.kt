package archyonix.uikit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class UIPreset(
    val path: String,
    val isResource: Boolean = false
)
