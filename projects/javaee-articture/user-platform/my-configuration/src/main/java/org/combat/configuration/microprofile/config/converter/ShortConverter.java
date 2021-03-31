package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description ShortConverter
 * @Date: 2021/3/25 18:55
 */
public class ShortConverter extends AbstractConverter<Short> {

    @Override
    protected Short doConvert(String value) {
        return Short.valueOf(value);
    }
}
