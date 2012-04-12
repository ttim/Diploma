package ru.abishev.wiki.pages;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;
import org.jboss.netty.handler.codec.http.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class PagesClient {
    private final Service<HttpRequest, HttpResponse> httpClient;

    public PagesClient(int port) {
        httpClient = ClientBuilder.safeBuild(
                ClientBuilder.get()
                        .codec(Http.get())
                        .hosts(new InetSocketAddress("localhost", port))
                        .hostConnectionLimit(1));
    }

    public PagesClient() {
        this(PagesServer.PAGES_SERVER_PORT);
    }

    public PageResult getPageForName(String name) throws IOException {
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/?name=" + URLEncoder.encode(name));
        HttpResponse response = httpClient.apply(request).get();
        if (response.getStatus().getCode() == 200) {
            PageResult result = PageResult.fromString(response.getContent().toString(Charset.forName("utf-8")));
            return result.isBad() ? null : result;
        } else {
            throw new IOException();
        }
    }

    public void release() {
        httpClient.release();
    }

    public static void main(String[] args) throws IOException {
        PagesClient client = new PagesClient();
        try {
            System.out.println(client.getPageForName("Hello world"));
        } finally {
            client.release();
        }
    }
}
