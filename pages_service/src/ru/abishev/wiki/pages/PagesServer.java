package ru.abishev.wiki.pages;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.List;

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
import ru.abishev.utils.CsvUtils;
import ru.abishev.utils.StringPool;

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

    private State state = new State(new File("./data/pages-index.csv"));

    public Future<HttpResponse> apply(HttpRequest request) {
        String uri = request.getUri();
        // ?name=name
        if (uri.startsWith("/?name=")) {
            String name = URLDecoder.decode(uri.substring("/?name=".length()));
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
        System.out.println("Server is running!");
    }

    public static class State {
        // todo: read from file index!
        private static final int MAX_TITLES_COUNT = 10500000;
        private static final int MAX_PAGE_ID = 35500000;

        // init this values from file!
        private StringPool pool = new StringPool(MAX_TITLES_COUNT);
        int[] pageIdToTitle = new int[MAX_PAGE_ID];
        int[] titleToPageId = new int[MAX_TITLES_COUNT];
        int[] redirects = new int[MAX_PAGE_ID];

        public State(File pagesIndexFile) {
            int badCount = 0;
            int badNotRedirectsCount = 0;
            // read pages file
            // two steps
            System.out.println("First step");
            int count = 0;
            for (List<String> page : CsvUtils.readCsv(pagesIndexFile, '|', '"')) {
                if (count++ % 500000 == 0) {
                    System.out.println("Line num: " + count);
                }

                int id = Integer.parseInt(page.get(0));
                String title = page.get(1);
                if (pool.getId(title) != -1) {
                    // ignore for now, but it should not be
                    badCount++;
                    if (page.size() > 2) {
                        badNotRedirectsCount++;
                    }
                }
                int titleId = pool.add(title);

                pageIdToTitle[id] = titleId;
                titleToPageId[titleId] = id;
            }
            System.out.println("Bad titles count: " + badCount + "; not redirects: " + badNotRedirectsCount);

            System.out.println("Second step");
            // analyse redirects
            count = 0;
            int badRedirectsCount = 0, redirectsCount = 0;
            for (List<String> page : CsvUtils.readCsv(pagesIndexFile, '|', '"')) {
                if (count++ % 500000 == 0) {
                    System.out.println("Line num: " + count);
                }

                int id = Integer.parseInt(page.get(0));
                if (page.size() > 2) {
                    String redirectTo = page.get(2);
                    if (redirectTo.length() > 0) {
                        if (redirectTo.contains("#")) {
                            redirectTo = redirectTo.substring(0, redirectTo.indexOf("#"));
                        }
                        redirects[id] = pool.getId(redirectTo);
                        if (redirects[id] == -1) {
//                            if (badRedirectsCount == 100) {
//                                System.out.println(page.get(0));
//                            }
                            badRedirectsCount++;
                        }
                        redirectsCount++;
                    }
                }
            }

            System.out.println("Redirects count: " + redirectsCount + "; bad redirects count: " + badRedirectsCount);
        }

        PageResult doRequest(String name) {
            // should be with preprocessing...
            return findByName(name);
        }

        PageResult findByName(String name) {
            int titleId = pool.getId(name);
            if (titleId == -1) {
                return PageResult.BAD_RESULT;
            }
            int steps = 0;
            while (redirects[titleToPageId[titleId]] != 0 && (steps++ < 100)) {
                if (redirects[titleToPageId[titleId]] == -1) {
                    return PageResult.BAD_RESULT;
                }
                titleId = redirects[titleToPageId[titleId]];
            }
            if (steps >= 100) {
                System.out.println("Steps >= 100 on " + name);
                return PageResult.BAD_RESULT;
            }
            int firstPageId = titleToPageId[pool.getId(name)];
            boolean isRedirect = redirects[firstPageId] != 0;
            return new PageResult(firstPageId, name, isRedirect, isRedirect ? pool.getById(redirects[firstPageId]) : "", titleToPageId[titleId], pool.getById(titleId));
        }
    }
}
