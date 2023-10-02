package dumper
import org.gradle.api.Plugin
import org.gradle.api.Project

class DumperProjectPlugin implements Plugin<Project> {
    void apply(Project project) {
        println "Applied $this to $project"
        def services = project.services
        new dumper.Dumper().debugServiceRegistry(0, new LinkedHashSet(), services)
    }
}