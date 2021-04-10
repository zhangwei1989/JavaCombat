package org.combat.rest.util;

import javax.ws.rs.Path;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

public interface PathUtils {

    String SLASH = "/";

    String ENCODED_SLASH = URLUtils.encode(SLASH);

    static String buildPath(String path, String... segments) {
        StringBuilder pathBuilder = new StringBuilder();

        if (path != null) {
            pathBuilder.append(path);
        }

        for (String segment : segments) {
            if (!segment.startsWith(SLASH)) {
                pathBuilder.append(SLASH);
            }
            pathBuilder.append(segment);
        }

        return pathBuilder.toString();
    }

    static String resolvePath(Class resource, String methodName) {
        return Stream.of(resource.getMethods())
                .filter(method -> Objects.equals(methodName, method.getName()))
                .map(PathUtils::resolvePath)
                .filter(Objects::nonNull)
                .findFirst()
                .get();
    }

    static String resolvePath(Class<?> resourceClass, Method handleMethod) {
        String pathFromResourceClass = resolvePath(resourceClass);
        String pathFromHandleMethod = resolvePath(handleMethod);
        return pathFromResourceClass != null ? pathFromResourceClass + pathFromHandleMethod : pathFromHandleMethod;
    }

    static String resolvePath(AnnotatedElement annotatedElement) {
        Path path = annotatedElement.getAnnotation(Path.class);
        if (path == null) {
            return null;
        }

        String value = path.value();
        if (!value.startsWith(SLASH)) {
            value = SLASH + value;
        }

        return value;
    }

}
