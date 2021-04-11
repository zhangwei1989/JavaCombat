package org.combat.projects.user.message;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * @author zhangwei
 * @Description MessageProducerFactory
 * @Date: 2021/4/10 18:16
 */
public class MessageProducerFactory implements ObjectFactory {

    private String queueName;

    private String connectionFactoryJNDIName;

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setConnectionFactoryJNDIName(String connectionFactoryJNDIName) {
        this.connectionFactoryJNDIName = connectionFactoryJNDIName;
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        ConnectionFactory connectionFactory  = (ConnectionFactory) nameCtx.lookup("activemq-factory");

        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("TEST.FOO");

        MessageProducer producer = session.createProducer(destination);

        return producer;
    }
}
