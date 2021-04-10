package org.combat.rest.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import java.security.KeyStore;
import java.util.Map;

/**
 * @author zhangwei
 * @Description DefaultClientBuilder
 * @Date: 2021/4/9 16:38
 */
public class DefaultClientBuilder extends ClientBuilder {

    private Configuration configuration;

    @Override
    public ClientBuilder withConfig(Configuration config) {
        this.configuration = config;
        return this;
    }

    @Override
    public ClientBuilder sslContext(SSLContext sslContext) {
        throw newUnsupportedSSLexception();
    }

    @Override
    public ClientBuilder keyStore(KeyStore keyStore, char[] password) {
        throw newUnsupportedSSLexception();
    }

    @Override
    public ClientBuilder trustStore(KeyStore trustStore) {
        throw newUnsupportedSSLexception();
    }

    @Override
    public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
        throw newUnsupportedSSLexception();
    }

    @Override
    public Client build() {
        return new DefaultClient();
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public ClientBuilder property(String name, Object value) {
        return null;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass) {
        return null;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, int priority) {
        return null;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
        return null;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        return null;
    }

    @Override
    public ClientBuilder register(Object component) {
        return null;
    }

    @Override
    public ClientBuilder register(Object component, int priority) {
        return null;
    }

    @Override
    public ClientBuilder register(Object component, Class<?>... contracts) {
        return null;
    }

    @Override
    public ClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
        return null;
    }

    private UnsupportedOperationException newUnsupportedSSLexception() {
        return new UnsupportedOperationException("Current implementation does not support SSL features");
    }
}
