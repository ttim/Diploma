package ru.abishev.wiki.pages;

import java.net.InetSocketAddress;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

public class PagesServer extends Service<HttpRequest, HttpResponse> {
    public static final int PAGES_SERVER_PORT = 8674;

    private static HttpResponse forbiddenResponse() {
        HttpResponse response =
                new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
        return response;
    }

    private static HttpResponse stringResponse(String content) {
        HttpResponse response =
                new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setContent(ChannelBuffers.wrappedBuffer(content.getBytes()));
        return response;
    }

    private State state = new State();

    public Future<HttpResponse> apply(HttpRequest request) {
        String uri = request.getUri();
        // ?name=name
        if (uri.startsWith("/?name=")) {
            String name = uri.substring("/?name=".length());
            return Future.value(stringResponse(state.doRequest(name).toString()));
        } else {
            return Future.value(forbiddenResponse());
        }
    }

    public static void main(String[] args) {
        ServerBuilder.safeBuild(new PagesServer(),
                ServerBuilder.get()
                        .codec(Http.get())
                        .name("PagesServer")
                        .bindTo(new InetSocketAddress("localhost", PAGES_SERVER_PORT)));
    }

    private class State {
        PageResult doRequest(String name) {
            // todo: implement
            return new PageResult(777, name, true, "redittitle", 7, "finaltitle");
        }
    }
}
