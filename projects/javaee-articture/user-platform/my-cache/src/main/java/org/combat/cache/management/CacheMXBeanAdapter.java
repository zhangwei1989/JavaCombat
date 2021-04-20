package org.combat.cache.management;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.management.CacheMXBean;
import java.util.Objects;

/**
 * @author zhangwei
 * @Description CacheMXBeanAdapter
 * @Date: 2021/4/16 20:35
 */
class CacheMXBeanAdapter implements CacheMXBean {

    private final CompleteConfiguration<?, ?> configuration;

    public CacheMXBeanAdapter(CompleteConfiguration<?, ?> configuration) throws NullPointerException {
        Objects.requireNonNull(configuration, "The argument 'configuration' must not be null");
        this.configuration = configuration;
    }

    @Override
    public String getKeyType() {
        return configuration.getKeyType().getName();
    }

    @Override
    public String getValueType() {
        return configuration.getValueType().getName();
    }

    @Override
    public boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    @Override
    public boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    @Override
    public boolean isStoreByValue() {
        return configuration.isStoreByValue();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    @Override
    public boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }

}
