package kc.servr.server;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * User: kclemens
 * Date: 8/17/12
 */
public class HttpConnector {

    private final String baseUri;

    private Map<String, String> headers = new HashMap<String, String>();

    public HttpConnector withHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    public HttpConnector(String baseUri) {
        this.baseUri = "http://" + baseUri;
    }

    public String get(String path) {
        return http("GET", path, null);
    }

    public String post(String path, String content) {
        return http("POST", path, content);
    }

    public String put(String path, String content) {
        return http("PUT", path, content);
    }

    public String delete(String path) {
        return http("DELETE", path, null);
    }

    private final Set<String> supportedMethod = new HashSet<String>(Arrays.asList("GET", "PUT", "POST", "DELETE"));

    private String http(String method, String path, String payload) {
        try {
            //open connection
            URLConnection connection = new URL(baseUri + path).openConnection();

            //set headers
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
            }

            //set method
            if (supportedMethod.contains(method)) {
                ((HttpURLConnection) connection).setRequestMethod(method);
            } else {
                throw new RuntimeException("Unsupported method '" + method + "' specified for HTTP!");
            }

            //send payload
            if (StringUtils.isNotEmpty(payload)) {
                connection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(payload);
                writer.flush();
            }

            //read response
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[1024];
            for (int length = reader.read(buffer); length > 0; length = reader.read(buffer)) {
                content.append(buffer, 0, length);
            }

            headers.clear();
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException("Exception while " + method + "ing to '" + baseUri + path + "'!", e);
        }
    }
}
