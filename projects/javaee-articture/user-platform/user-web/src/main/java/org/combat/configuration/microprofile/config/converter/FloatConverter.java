package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description FloatConverter
 * @Date: 2021/3/25 18:52
 */
public class FloatConverter extends AbstractConverter<Float> {

    @Override
    protected Float doConvert(String value) {
        return Float.valueOf(value);
    }
}
