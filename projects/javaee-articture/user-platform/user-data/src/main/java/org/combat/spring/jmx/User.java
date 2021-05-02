package org.combat.spring.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author zhangwei
 * @Description User
 * @Date: 2021/5/1 18:34
 */
@ManagedResource(objectName = "org.combat:name=User")
public class User {

    private String name;

    private int age;

    @ManagedAttribute
    public String getName() {
        return name;
    }

    @ManagedOperation
    @ManagedOperationParameter(name = "name", description = "name")
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute
    public int getAge() {
        return age;
    }

    @ManagedOperation
    @ManagedOperationParameter(name = "age", description = "age")
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
