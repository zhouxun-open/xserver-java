package xserver.util;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

public class HttpUtils {

    public static String get(String url) throws IOException, InterruptedException, ExecutionException {
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            ListenableFuture<Response> f = client.prepareGet(url).execute();
            String body = f.get().getResponseBody("UTF-8");
            return body;
        } finally {
            client.close();
        }
    }

    public static String post(String url, String param) throws IOException, InterruptedException, ExecutionException {
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            ListenableFuture<Response> f = client.preparePost(url).setBody(param.getBytes()).execute();
            String body = f.get().getResponseBody("UTF-8");
            return body;
        } finally {
            client.close();
        }
    }
}