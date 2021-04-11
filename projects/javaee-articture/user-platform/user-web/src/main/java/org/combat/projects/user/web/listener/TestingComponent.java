package org.combat.projects.user.web.listener;

import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.MessageProducer;
import javax.jms.Topic;

/**
 * @author zhangwei
 * @Description TestingComponent
 * @Date: 2021/4/10 15:57
 */
@Deprecated
public class TestingComponent {

    @Resource(name = "jms/activemq-topic")
    private Topic topic;

    @Resource(name = "jms/message-producer")
    private MessageProducer messageProducer;

    @PostConstruct
    public void init() {
        System.out.println(topic);
    }

    @PostConstruct
    public void sendMessage() throws Throwable {
        String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText(text);

        messageProducer.send(message);
        System.out.printf("[Thread : %s] Sent message: %s\n ", Thread.currentThread().getName(), message.getText());
    }

}
