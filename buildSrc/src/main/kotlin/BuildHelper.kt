import org.gradle.api.Project
import java.io.File

object BuildHelper {

    fun sdkVersion(): Int {
        return 33
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
