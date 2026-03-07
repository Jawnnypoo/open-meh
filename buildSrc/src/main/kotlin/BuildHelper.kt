import org.gradle.api.Project

object BuildHelper {

    fun sdkVersion(): Int {
        return 36
    }

    fun mehApiKey(project: Project): String {
        return "\"${project.propertyOrEmpty("MEH_API_KEY")}\""
    }

    fun keystorePassword(project: Project): String {
        return project.propertyOrEmpty("KEYSTORE_PASSWORD")
    }

    fun keyPassword(project: Project): String {
        return project.propertyOrEmpty("KEY_PASSWORD")
    }
}
