package org.combat.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhangwei
 * @Description PrioritizedConverter
 * @Date: 2021/3/21 23:25
 */
public class PrioritizedConverter<T> implements Converter<T>, Comparable<PrioritizedConverter<T>> {

    private final Converter<T> converter;

    private int priority;

    public PrioritizedConverter(Converter<T> converter, int priority) {
        this.converter = converter;
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public Converter<T> getConverter() {
        return this.converter;
    }

    @Override
    public int compareTo(PrioritizedConverter<T> other) {
        return Integer.compare(other.priority, this.priority);
    }

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        return this.converter.convert(value);
    }
}
