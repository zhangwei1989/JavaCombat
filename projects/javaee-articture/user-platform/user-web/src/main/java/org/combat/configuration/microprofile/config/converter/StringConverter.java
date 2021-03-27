package org.combat.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhangwei
 * @Description StringConverter
 * @Date: 2021/3/25 18:55
 */
public class StringConverter implements Converter<String> {

    @Override
    public String convert(String value) throws IllegalArgumentException, NullPointerException {
        return value;
    }
}
