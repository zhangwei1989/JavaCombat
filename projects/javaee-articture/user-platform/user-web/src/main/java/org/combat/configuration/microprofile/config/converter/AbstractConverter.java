package org.combat.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhangwei
 * @Description AbstractConverter
 * @Date: 2021/3/25 18:47
 */
public abstract class AbstractConverter<T> implements Converter<T> {

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        if (value == null) {
            throw new NullPointerException("The value must not be null!");
        }

        return doConvert(value);
    }

    protected abstract T doConvert(String value);

}
