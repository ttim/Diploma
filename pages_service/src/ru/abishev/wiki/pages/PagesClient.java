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
import java.util.HashMap;
import java.util.Map;

public class PagesClient {
    private final Service<HttpRequest, HttpResponse> httpClient;
    private final Map<String, PageResult> cache = new HashMap<String, PageResult>();

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
        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/?name=" + URLEncoder.encode(name));
        HttpResponse response = httpClient.apply(request).get();
        if (response.getStatus().getCode() == 200) {
            PageResult result = PageResult.fromString(response.getContent().toString(Charset.forName("utf-8")));
            result = result.isBad() ? null : result;

            if (cache.size() > 1000000) {
                cache.clear();
            }
            cache.put(name, result);

            return result;
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
