# gradle-service-scopes-exploration



```
gradle -Dpriority=1
Applied dumper.DumperSettingsPlugin@6597e902 to settings 'service-registries-and-scopes'
 - registry: SettingsScopeServices
             - registry: BuildScopeServices
                         - registry: build tree services
                                     - registry: build session services
                                                     - registry: services for Gradle user home dir /Users/rafael/.gradle
                                                                 - registry: DaemonServices
                                                                             - registry: NativeServices
                                                                             - registry: CommandLineLogging
                                                     - registry: cross session services

> Configure project :
Applied dumper.DumperProjectPlugin@5bf1d9a2 to root project 'service-registries-and-scopes'
 - registry: ProjectScopeServices
             - registry: GradleScopeServices
                         - registry: BuildScopeServices
                                     - registry: build tree services
                                                 - registry: build session services
                                                                 - registry: services for Gradle user home dir /Users/rafael/.gradle
                                                                             - registry: DaemonServices
                                                                                         - registry: NativeServices
                                                                                         - registry: CommandLineLogging
                                                                 - registry: cross session services

BUILD SUCCESSFUL in 363ms
4 actionable tasks: 4 up-to-date

```

For higher verbosity, use `-Dpriority=0`.