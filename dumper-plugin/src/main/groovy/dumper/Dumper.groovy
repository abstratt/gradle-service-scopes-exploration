
package dumper

class Dumper {
    private Map localIds = [:]
    private int nextId = 1
    private int threshold = System.getProperty("priority", "1").toInteger()

    private String getLocalId(Object object) {
        def existingId = localIds[object]
        if (existingId) {
            return existingId
        }
        def newId = object.toString()
        if (newId.contains("@")) {
            newId = object.class.simpleName + "@" + nextId++
        }
        localIds[object] = newId 
        return newId
    }

    void debugServiceRegistry(int level, Set history, Object serviceRegistry) {
        debugDefaultServiceRegistry(level, history, serviceRegistry)
    }

    private visit(int level, Set history, Object node) {
        def repeat = !history.add(node)
        if (repeat) {
            indent(level, "Already visited ${getLocalId(node)}")
        }
        return !repeat
    }

    public void debugDefaultServiceRegistry(int level, Set history, Object serviceRegistry) {
        // DefaultServiceRegistry is a hierarchical {@link ServiceRegistry} implementation.

        if (!visit(level, history, serviceRegistry)) {
            return
        }

        indent level, "registry: ${getLocalId(serviceRegistry)}", 1

        if (isA(serviceRegistry.class, "ProjectScopeServices")) {
            debugProjectScopeServices(level, serviceRegistry)
        }

        // It holds its own services:
        def ownServices = access(serviceRegistry, "ownServices")
        debugOwnServicesServiceProvider(level, history, ownServices)

        def parentServices = access(serviceRegistry, "parentServices")
        debugParentServiceProvider(level, history, parentServices)

        if (isA(serviceRegistry.class, "NativeServices")) {
            debugNativeServices(level, serviceRegistry)
        }

    }

    def debugProjectScopeServices(int level, Object projectScopeServices) {
        def project = access(projectScopeServices, "project")
        indent level, "project: ${project}"
    }

    def debugNativeServices(int level, Object nativeServices) {
        indent level, "initializedFeatures features: ${access(nativeServices, 'initializedFeatures')}"
        indent level, "enabled features: ${access(nativeServices, 'enabledFeatures')}"
        indent level, "nativeBaseDir: ${access(nativeServices, 'nativeBaseDir')}"
    }

    def debugParentCompositeServiceProvider(int level, Set history, Object compositeServiceProvider) {
        if (!visit(level, history, compositeServiceProvider)) {
            return
        }
        def childServiceProviders = access(compositeServiceProvider, "serviceProviders")
        indent level, "${childServiceProviders.size()} children"
        childServiceProviders.each {
            debugParentServiceProvider(level + 1, history, it)
        }
    }

    def debugSingleParentServiceProvider(int level, Set history, Object singleParentServiceProvider) {
        if (!visit(level, history, singleParentServiceProvider)) {
            return
        }
        def parentProvider = singleParentServiceProvider.parent
        debugParentServiceProvider(level + 1, history, parentProvider)
    }

    def debugOwnServicesServiceProvider(int level, Set history, Object ownServices) {
        if (!visit(level, history, ownServices)) {
            return
        }
        indent level, "own service providers: ${access(ownServices, "providersByType").size()}"
        def owner = access(ownServices, "this\$0")
        debugDefaultServiceRegistry(level + 1, history, owner)
    }

    def debugParentServiceProvider(int level, Set history, Object serviceProvider) {
        indent level, "service provider: ${getLocalId(serviceProvider)}"
        if (serviceProvider == null) {
            indent(level, "Null service provider")
        } else if (isA(serviceProvider.class, "CompositeServiceProvider")) {
            debugParentCompositeServiceProvider(level+1, history, serviceProvider)
        } else if (isA(serviceProvider.class, "ParentServices")) {
            debugSingleParentServiceProvider(level+1, history, serviceProvider)
        } else if (isA(serviceProvider.class, "OwnServices")) {
            debugOwnServicesServiceProvider(level+1, history, serviceProvider)
        } else {
            indent(level, "Unexpected service provider: " + serviceProvider)
        }
    }

    def access(Object object, String... fieldNames) {
        def current = object
        fieldNames.each { fieldName ->
            current = access(current, fieldName)
        }
        return current
    }

    def access(Object object, String fieldName) {
        if (object == null) {
            return null
        }
        //println "access ${fieldName} in ${object.class}"
        def field = findField(object.class, fieldName)
        field.accessible = true
        return field.get(object)
    }

    def findField(Class clazz, String fieldName) {
        def found = tryToFindField(clazz, fieldName)
        if (!found) throw new NoSuchFieldException("No field $fieldName in $clazz")
        return found
    }

    def tryToFindField(Class clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName)
        } catch (NoSuchFieldException e) {
            if (clazz.superclass != null) {
                return tryToFindField(clazz.superclass, fieldName)
            }

        }
    }

    def boolean isA(Class clazz, String className) {
        if (clazz == null) {
            return false
        }
        if (clazz.simpleName == className) {
            return true
        }
        return isA(clazz.superclass, className)
    }

    def indent(int level, Object message, int priority = 0) {
        if (priority >= threshold) {
            println("  ".repeat(level) + " - $message")
        }
    }
}
