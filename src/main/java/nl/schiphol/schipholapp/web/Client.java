package nl.schiphol.schipholapp.web;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class Client {
    private final Logger log = LoggerFactory.getLogger(Client.class);

    private final String configFile = "config/config.properties";

    private final String appIdProperty = "appId";
    private final String appKeyProperty = "appKey";

    private final String acceptHeader = "Accept";
    private final String acceptHeaderValue = "application/json";
    private final String appIdHeader = "app_id";
    private final String appKeyHeader = "app_key";
    private final String resourceVersionHeader = "ResourceVersion";
    private final String defaultCharSet = "UTF-8";

    private final String requestUrl = "https://api.schiphol.nl/public-flights/%s?page=%s";

    private final int maxRequestsPerMinute = 200;
    private final int waitPeriod = 60000;

    private HttpClient httpClient;

    private String appId;
    private String appKey;

    private int totalPages;
    private boolean totalPagesSet;

    public Client() {
        this.loadProperties();
        this.httpClient = HttpClients.createDefault();
        this.totalPages = 1;
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream(this.configFile);
            properties.load(input);
            this.appId = properties.getProperty(this.appIdProperty);
            this.appKey = properties.getProperty(this.appKeyProperty);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public JSONArray process(String resource, String apiVersion) {
        int currentPage = 0;
        JSONArray flights = new JSONArray();
        try {
            while (currentPage < this.totalPages) {
                if (currentPage != 0 && (currentPage % this.maxRequestsPerMinute == 0)) {
                    Thread.sleep(this.waitPeriod);
                }

                HttpResponse response = this.getResponse(resource, apiVersion, currentPage);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    flights.addAll(this.getData(resource, response));
                } else {
                    log.error("Http response code: {}\nHttp response body: {}",
                            response.getStatusLine().getStatusCode(),
                            EntityUtils.toString(response.getEntity()));
                }
                currentPage++;
            }
        } catch (IOException | ParseException | InterruptedException e) {
            log.error(e.getMessage());
        }
        return flights;
    }

    private HttpResponse getResponse(String resource, String apiVersion, int pageNumber) throws IOException {
        String requestUrl = String.format(this.requestUrl, resource, pageNumber);
        HttpGet request = new HttpGet(requestUrl);
        request.addHeader(this.acceptHeader, acceptHeaderValue);
        request.addHeader(this.appIdHeader, appId);
        request.addHeader(this.appKeyHeader, appKey);
        request.addHeader(this.resourceVersionHeader, apiVersion);
        return this.httpClient.execute(request);
    }

    private JSONArray getData(String resource, HttpResponse httpResponse) throws IOException, ParseException {
        Header linkHeader = httpResponse.getHeaders("Link")[0];
        String headerValue = linkHeader.getValue();
        if (!totalPagesSet && headerValue.contains("last")) {
            String[] headerValues = linkHeader.getValue().split(",");
            String lastPageLink = headerValues[0].contains("last") ? headerValues[0] : headerValues[1];

            String lastPageNumberString = lastPageLink.substring(
                    lastPageLink.indexOf("page=") + "page=".length(),
                    lastPageLink.indexOf(">"));
            this.totalPages = Integer.parseInt(lastPageNumberString);
            this.totalPagesSet = true;
        }

        String responseBody = EntityUtils.toString(httpResponse.getEntity(), this.defaultCharSet);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
        return (JSONArray) jsonObject.get(resource);
    }
}
