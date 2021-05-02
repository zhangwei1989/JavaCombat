package org.combat.spring.jmx;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.LiveBeansView;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;

import java.io.IOException;

/**
 * @author zhangwei
 * @Description SpringJmxDemo
 * @Date: 2021/5/1 18:31
 */
@Configuration
public class SpringJmxDemo {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.register(SpringJmxDemo.class);

        context.refresh();
        System.in.read();
        context.close();
    }

    @Bean
    public AnnotationMBeanExporter beanExporter() {
        return new AnnotationMBeanExporter();
    }

    @Bean
    public User user() {
        return new User();
    }

    @Bean
    public LiveBeansView liveBeansView() {
        return new LiveBeansView();
    }

}
