package org.combat.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Comparator;

/**
 * @author zhangwei
 * @Description ConfigSourceOrdinalComparator - 优先级比较器
 * @Date: 2021/3/24 23:43
 */
public class ConfigSourceOrdinalComparator implements Comparator<ConfigSource> {

    public static final Comparator<ConfigSource> INSTANCE = new ConfigSourceOrdinalComparator();

    private ConfigSourceOrdinalComparator() {
    }

    @Override
    public int compare(ConfigSource o1, ConfigSource o2) {
        return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
    }
}
