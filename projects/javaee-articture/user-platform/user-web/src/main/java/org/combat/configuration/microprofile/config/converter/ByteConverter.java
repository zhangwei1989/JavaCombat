package org.combat.configuration.microprofile.config.converter;

/**
 * @author zhangwei
 * @Description ByteConverter
 * @Date: 2021/3/25 18:50
 */
public class ByteConverter extends AbstractConverter<Byte> {

    @Override
    protected Byte doConvert(String value) {
        return Byte.valueOf(value);
    }
}
