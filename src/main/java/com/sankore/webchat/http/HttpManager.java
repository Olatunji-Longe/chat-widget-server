package com.sankore.webchat.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Tunji Longe on 7/4/2017.
 */
public class HttpManager {

    public static String buildQueryString(Map<String, Object> params) {
        try{
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (String key : params.keySet()) {
                if (first) {
                    result.append("?");
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(key);
                result.append("=");
                result.append(URLEncoder.encode(String.valueOf(params.get(key)), Consts.UTF_8.name()));
            }
            return result.toString();
        }catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        return "";
    }

    public static UrlEncodedFormEntity getFormEntity(Map<String, Object> params){
        List<NameValuePair> nameValuePair = new ArrayList<>();
        for(String key : params.keySet()){
            nameValuePair.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
        }
        return new UrlEncodedFormEntity(nameValuePair, Consts.UTF_8);
    }

    private static StringEntity getJsonEntity(Map<String, Object> params) throws UnsupportedEncodingException{
        return new StringEntity(GsonUtil.gson().toJson(params), Consts.UTF_8);
    }

    private static StringEntity buildEntity(Map<String, Object> params, ContentType contentType) throws UnsupportedEncodingException{
        if(contentType.equals(ContentType.APPLICATION_JSON)){
            return getJsonEntity(params);
        }else if(contentType.equals(ContentType.APPLICATION_FORM_URLENCODED)){
            return getFormEntity(params);
        }
        return null;
    }

    public static <T extends HttpRequestBase> T buildHttpRequest(HttpMethod method, String url, ContentType contentType, Payload payload) {
        HttpRequestBase request = null;
        payload = payload != null ? payload : new Payload();
        try{
            switch(method) {
                case GET:
                    request = new HttpGet(!payload.isEmpty() ? url + buildQueryString(payload) : url);
                    break;
                case POST:
                    HttpPost post = new HttpPost(url);
                    post.setEntity(buildEntity(payload, contentType));
                    request = post;
                    break;
                case PUT:
                    HttpPut put = new HttpPut(url);
                    put.setEntity(buildEntity(payload, contentType));
                    request = put;
                    break;
                case PATCH:
                    HttpPatch patch = new HttpPatch(url);
                    patch.setEntity(buildEntity(payload, contentType));
                    request = patch;
                    break;
                case DELETE:
                    request = new HttpDelete(!payload.isEmpty() ? url + buildQueryString(payload) : url);
                    break;
            }
            if(request != null){
                request.addHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());
                request.addHeader(HttpHeaders.ACCEPT, contentType.getMimeType());
                return (T) request;
            }
        }catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static JsonObject processRequest(HttpRequestBase request){
        try {
            String endpoint = request.getURI().toString();
            Protocol protocol = endpoint.startsWith(Protocol.https.name()) ? Protocol.https : Protocol.http;
            HttpClient client = getHttpClient(protocol);
            HttpResponse httpResponse = client.execute(request);
            String entity = EntityUtils.toString(httpResponse.getEntity());
            JsonElement element = GsonUtil.parse(entity);
            return RestUtil.response(httpResponse.getStatusLine().getStatusCode(), element);
        } catch (Exception ex) {
            return RestUtil.exception(ex);
        }
    }

    public static HttpClient getHttpClient(Protocol protocol) {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        if (protocol.equals(Protocol.https)) {
            try {
                final SSLConnectionSocketFactory sslSockFactory = new SSLConnectionSocketFactory(getTrustySSLContext(), NoopHostnameVerifier.INSTANCE);
                final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                        .register(Protocol.http.name(), new PlainConnectionSocketFactory())
                        .register(Protocol.https.name(), sslSockFactory).build();

                final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);

                return clientBuilder.setSSLSocketFactory(sslSockFactory).setConnectionManager(connManager).build();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            } catch (KeyManagementException ex) {
                ex.printStackTrace();
            }
        }
        return clientBuilder.build();
    }

    public static JsonObject processRequestAsync(HttpRequestBase request) throws IOException {
        String endpoint = request.getURI().toString();
        Protocol protocol = endpoint.startsWith(Protocol.https.name()) ? Protocol.https : Protocol.http;
        CloseableHttpAsyncClient client = getCloseableHttpAsyncClient(protocol);
        try {
            client.start(); // Start the client
            Future<HttpResponse> future = client.execute(request, null); // Execute request
            HttpResponse httpResponse = future.get(); // And wait until a response is received
            String entity = EntityUtils.toString(httpResponse.getEntity());
            JsonElement element = GsonUtil.parse(entity);
            return RestUtil.response(httpResponse.getStatusLine().getStatusCode(), element);
        } catch (Exception ex) {
            return RestUtil.exception(ex);
        }finally{
            client.close();
        }
    }

    public static CloseableHttpAsyncClient getCloseableHttpAsyncClient(Protocol protocol) {
        HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom();
        if (protocol.equals(Protocol.https)) {
            try {
                final SSLIOSessionStrategy sslioSessionStrategy = new SSLIOSessionStrategy(getTrustySSLContext(), NoopHostnameVerifier.INSTANCE);
                final Registry<SchemeIOSessionStrategy> registry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                        .register(Protocol.http.name(), NoopIOSessionStrategy.INSTANCE)
                        .register(Protocol.https.name(), sslioSessionStrategy)
                        .build();
                final PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT), registry);

                return clientBuilder.setConnectionManager(connManager).build();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            } catch (KeyManagementException ex) {
                ex.printStackTrace();
            } catch (IOReactorException ex) {
                ex.printStackTrace();
            }
        }
        return clientBuilder.build();
    }

    private static SSLContext getTrustySSLContext() throws KeyManagementException, NoSuchAlgorithmException{
        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                //System.out.println("getAcceptedIssuers =============");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                //System.out.println("checkClientTrusted =============");
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                //System.out.println("checkServerTrusted =============");
            }
        }
        }, new SecureRandom());
        return sslContext;
    }

    public static void addBasicAuthToken(HttpRequestBase request, String username, String password) throws UnsupportedEncodingException {
        String encoded = DatatypeConverter.printBase64Binary((username + ":" + password).getBytes(Consts.UTF_8.name()));
        request.addHeader("Authorization", "Basic " + encoded);
    }

    public static void addBasicAuthToken(HttpRequestBase request, String authKey, String username, String password) throws UnsupportedEncodingException {
        String encoded = DatatypeConverter.printBase64Binary((authKey + "|" + username + "|" + password).getBytes(Consts.UTF_8.name()));
        request.addHeader("Authorization", "Basic " + encoded);
    }

    public static void addBearerToken(HttpRequestBase request, String token) throws UnsupportedEncodingException {
        request.addHeader("Authorization", "Bearer " + token);
    }

}
