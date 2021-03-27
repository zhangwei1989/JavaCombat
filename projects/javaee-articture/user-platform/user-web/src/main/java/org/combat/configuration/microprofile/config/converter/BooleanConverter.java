package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description BooleanConverter
 * @Date: 2021/3/25 18:50
 */
public class BooleanConverter extends AbstractConverter<Boolean> {

    @Override
    protected Boolean doConvert(String value) {
        return Boolean.parseBoolean(value);
    }
}
