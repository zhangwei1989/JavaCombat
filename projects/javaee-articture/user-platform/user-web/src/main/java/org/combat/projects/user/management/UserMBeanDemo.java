package org.combat.projects.user.management;

import org.combat.projects.user.domain.User;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author zhangwei
 * @Description UserMBeanDemo
 * @Date: 2021/3/21 01:04
 */
public class UserMBeanDemo {

    public static void main(String[] args) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("org.combat.projects.user.management:type=User");

        User user = new User();

        mBeanServer.registerMBean(createUserMBean(user), objectName);

        while (true) {
            Thread.sleep(2000);
            System.out.println(user);
        }
    }

    private static Object createUserMBean(User user) {
        return new UserManager(user);
    }
}
