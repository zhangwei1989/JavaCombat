package org.combat.projects.user.web.listener;

import org.combat.context.ComponentContext;
import org.combat.projects.user.domain.User;
import org.combat.projects.user.sql.DBConnectionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public static final String CREATE_USERS_TABLE_DDL_SQL = "CREATE TABLE users(" +
            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
            "name VARCHAR(16) NOT NULL, " +
            "password VARCHAR(64) NOT NULL, " +
            "email VARCHAR(64), " +
            "phoneNumber VARCHAR(64)" +
            ")";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentContext context = ComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");

        testPropertyFromJNDI(context);

        testPropertyFromServletContext(sce.getServletContext());

        /*try {
            Statement statement = dbConnectionManager.getConnection().createStatement();
            System.out.println(statement.execute(CREATE_USERS_TABLE_DDL_SQL));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        testUser(dbConnectionManager.getEntityManager());*/
    }

    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        System.out.println("ServletContext property " + propertyName + ": [" + servletContext.getInitParameter(propertyName) + "]");
    }

    private void testPropertyFromJNDI(ComponentContext context) {
        String propertyName = "maxValue";
        System.out.println("JNDI property " + propertyName + ": [" + context.lookupComponent("maxValue") + "]");
    }

    private void testUser(EntityManager entityManager) {
        User user = new User();
        user.setName("小马哥 2021");
        user.setPassword("******");
        user.setEmail("mercyblitz@gmail.com");
        user.setPhoneNumber("abcdefg");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        System.out.println(entityManager.find(User.class, user.getId()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
