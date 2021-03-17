package org.combat.projects.user.sql;

import java.lang.annotation.Documented;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

/**
 * 本地事务
 */
@Documented
public @interface LocalTransactional {

    int PROPAGATION_REQUIRED = 0;

    int PROPAGATION_REQUIRES_NEW = 3;

    int PROPAGATION_NESTED = 6;

    /**
     * @Description 事务传播
     */
    int propagation() default PROPAGATION_REQUIRED;

    /**
     * @Description 事务隔离级别
     */
    int isolation() default TRANSACTION_READ_COMMITTED;
}
