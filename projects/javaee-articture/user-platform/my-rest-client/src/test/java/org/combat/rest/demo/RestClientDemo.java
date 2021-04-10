package org.combat.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @author zhangwei
 * @Description RestClientDemo
 * @Date: 2021/4/9 16:46
 */
public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")
                .request()
                .get();

        String content = response.readEntity(String.class);

        System.out.println(content);
    }
}
