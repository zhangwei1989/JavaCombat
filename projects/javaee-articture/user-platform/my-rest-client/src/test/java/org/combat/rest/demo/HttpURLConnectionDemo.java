package org.combat.rest.demo;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author zhangwei
 * @Description HttpURLConnectionDemo
 * @Date: 2021/4/9 16:45
 */
public class HttpURLConnectionDemo {

    public static void main(String[] args) throws Throwable {
        URI uri = new URI("http://127.0.0.1:8080/user/register");
        URL url = uri.toURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (InputStream inputStream = connection.getInputStream()) {
            System.out.println(IOUtils.toString(inputStream, "UTF-8"));
        }

        connection.disconnect();
    }
}
