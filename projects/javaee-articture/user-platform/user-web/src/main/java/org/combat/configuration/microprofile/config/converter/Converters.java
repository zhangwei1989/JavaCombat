package org.combat.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author zhangwei
 * @Description Converters
 * @Date: 2021/3/21 21:39
 */
public class Converters implements Iterable<Converter> {

    private ClassLoader classLoader;

    private boolean addedDiscoveredConverters = false;

    private static final int DEFAULT_PRIORITY = 100;

    private final Map<Class<?>, PriorityQueue<PrioritizedConverter>> typedConverters = new HashMap<>();

    public Converters() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public Converters(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public List<Converter> getConverters(Class<?> convertedType) {
        PriorityQueue<PrioritizedConverter> prioritizedConverters = typedConverters.get(convertedType);

        if (prioritizedConverters == null || prioritizedConverters.isEmpty()) {
            return Collections.emptyList();
        }

        List<Converter> converters = new LinkedList<>();

        for (PrioritizedConverter prioritizedConverter : prioritizedConverters) {
            converters.add(prioritizedConverter.getConverter());
        }

        return converters;
    }

    public void addDiscoveredConverters() {
        if (addedDiscoveredConverters) {
            return;
        }

        addConverters(ServiceLoader.load(Converter.class, classLoader));
        addedDiscoveredConverters = true;
    }

    private void addConverters(Iterable<Converter> converters) {
        converters.forEach(this::addConverter);
    }

    private void addConverter(Converter converter) {
        addConverter(converter, DEFAULT_PRIORITY);
    }

    private void addConverter(Converter converter, int priority) {
        Class<?> convertedType = resolveConvertedType(converter);
        addConverter(converter, priority, convertedType);
    }

    public void addConverter(Converter converter, int priority, Class<?> convertedType) {
        PriorityQueue priorityQueue = typedConverters.computeIfAbsent(convertedType, t -> new PriorityQueue<>());
        priorityQueue.offer(new PrioritizedConverter(converter, priority));
    }

    private Class<?> resolveConvertedType(Converter converter) {
        assertConverter(converter);
        Class<?> convertedType = null;
        Class<?> converterClass = converter.getClass();

        while (converterClass != null) {
            convertedType = resolveConvertedType(converterClass);
            if (convertedType != null) {
                break;
            }

            Type superType = converterClass.getGenericSuperclass();

            if (superType instanceof ParameterizedType) {
                convertedType = resolveConvertedType(superType);
            }

            if (convertedType != null) {
                break;
            }

            // recursively
            converterClass = converterClass.getSuperclass();
        }

        return convertedType;
    }

    private Class<?> resolveConvertedType(Class<?> converterClass) {
        Class<?> convertedType = null;

        for (Type superInterface : converterClass.getGenericInterfaces()) {
            convertedType = resolveConvertedType(superInterface);
            if (convertedType != null) {
                break;
            }
        }

        return convertedType;
    }

    private Class<?> resolveConvertedType(Type type) {
        Class<?> convertedType = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;

            if (pType.getRawType() instanceof Class) {
                Class<?> rawType = (Class) pType.getRawType();
                if (Converter.class.isAssignableFrom(rawType)) {
                    Type[] arguments = pType.getActualTypeArguments();
                    if (arguments.length == 1 && arguments[0] instanceof Class) {
                        convertedType = (Class) arguments[0];
                    }
                }
            }
        }

        return convertedType;
    }

    private void assertConverter(Converter<?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Converter must not bt an interface!");
        }

        if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Converter must not bt abstract!");
        }
    }

    public void addConverters(Converter... converters) {
        addConverters(Arrays.asList(converters));
    }

    @Override
    public Iterator<Converter> iterator() {
        List<Converter> allConverters = new LinkedList<>();
        for (PriorityQueue<PrioritizedConverter> converters : typedConverters.values()) {
            for (PrioritizedConverter converter : converters) {
                allConverters.add(converter.getConverter());
            }
        }

        return allConverters.iterator();
    }
}
