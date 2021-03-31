package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description LongConverter
 * @Date: 2021/3/25 18:53
 */
public class LongConverter extends AbstractConverter<Long> {

    @Override
    protected Long doConvert(String value) {
        return Long.valueOf(value);
    }
}
