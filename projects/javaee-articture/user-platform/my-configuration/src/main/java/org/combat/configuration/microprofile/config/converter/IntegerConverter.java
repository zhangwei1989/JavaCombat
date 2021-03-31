package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description IntegerConverter
 * @Date: 2021/3/25 18:53
 */
public class IntegerConverter extends AbstractConverter<Integer> {

    @Override
    protected Integer doConvert(String value) {
        return Integer.valueOf(value);
    }
}
