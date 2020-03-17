import org.gradle.api.Project
import java.io.File

object BuildHelper {

    private var locatedFile: Boolean? = null

    fun sdkVersion(): Int {
        return 28
    }

    fun crashlyticsVersion(): String {
        return "2.10.1"
    }

    fun timberVersion(): String {
        return "4.7.1"
    }

    fun moshiVersion(): String {
        return "1.9.2"
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

    fun firebaseEnabled(project: Project): Boolean {
        return fileExists(project)
    }

    private fun fileExists(project: Project): Boolean {
        val located = locatedFile
        return if (located != null) {
            located
        } else {
            val fileExists = project.file("${project.rootDir}/app/google-services.json").exists()
            locatedFile = fileExists
            printFirebase()
            fileExists
        }
    }

    private fun printFirebase() {
        println(
                """

 / _(_)         | |
 | |_ _ _ __ ___| |__   __ _ ___  ___
 |  _| | '__/ _ \ '_ \ / _` / __|/ _ \
 | | | | | |  __/ |_) | (_| \__ \  __/
 |_| |_|_|  \___|_.__/ \__,_|___/\___|
 enabled

            """.trimIndent()
        )
    }

}
