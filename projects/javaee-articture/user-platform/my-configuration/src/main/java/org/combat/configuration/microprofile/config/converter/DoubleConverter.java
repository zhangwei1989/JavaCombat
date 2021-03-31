package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description DoubleConverter
 * @Date: 2021/3/25 18:52
 */
public class DoubleConverter extends AbstractConverter<Double> {

    @Override
    protected Double doConvert(String value) {
        return Double.valueOf(value);
    }
}
