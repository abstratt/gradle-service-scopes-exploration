package dumper
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class DumperSettingsPlugin implements Plugin<Settings> {
    void apply(Settings settings) {
        println "Applied $this to $settings"
        def services = settings.services
        new dumper.Dumper().debugServiceRegistry(0, new LinkedHashSet(), services)
    }
}