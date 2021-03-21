package org.combat.projects.user.management;

import org.combat.projects.user.domain.User;

import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 * @author zhangwei
 * @Description StandardMBeanDemo
 * @Date: 2021/3/21 01:23
 */
public class StandardMBeanDemo {

    public static void main(String[] args) throws Exception {
        StandardMBean standardMBean = new StandardMBean(new UserManager(new User()), UserManagerMBean.class);

        MBeanInfo mBeanInfo = standardMBean.getMBeanInfo();
        System.out.println(mBeanInfo);
    }
}
