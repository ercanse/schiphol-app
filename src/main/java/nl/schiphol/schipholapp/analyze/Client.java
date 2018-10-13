package nl.schiphol.schipholapp.analyze;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Client {
    private final Logger log = LoggerFactory.getLogger(Client.class);
    private final String configFile = "config/config.properties";

    private String appId;
    private String appKey;

    private int totalPages = 1;

    public Client() {
        this.loadProperties();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream(this.configFile);
            properties.load(input);
            this.appId = properties.getProperty("appId");
            this.appKey = properties.getProperty("appKey");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public JSONArray process(String resource) {
        int currentPage = 0;
        JSONArray flights = new JSONArray();
        try {
            while (currentPage < 5) {
                HttpResponse response = this.getResponse(resource, currentPage);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    flights.addAll(this.getData(resource, response));
                } else {
                    log.error("Http response code: {}\nHttp response body: {}",
                            response.getStatusLine().getStatusCode(),
                            EntityUtils.toString(response.getEntity()));
                }
                currentPage++;
            }
        } catch (IOException | ParseException e) {
            log.error(e.getMessage());
        }
        return flights;
    }

    private HttpResponse getResponse(String resource, int pageNumber) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.schiphol.nl/public-flights/" + resource + "?app_id=" + appId + "&app_key=" + appKey + "&page=" + pageNumber);
        request.addHeader("ResourceVersion", "v3");
        return httpClient.execute(request);
    }

    private JSONArray getData(String resource, HttpResponse httpResponse) throws IOException, ParseException {
        Header linkHeader = httpResponse.getHeaders("Link")[0];
        String headerValue = linkHeader.getValue();
        if (headerValue.contains("last")) {
            String[] headerValues = linkHeader.getValue().split(",");
            String lastPageLink = headerValues[0].contains("last") ? headerValues[0] : headerValues[1];

            String lastPageNumberString = lastPageLink.substring(
                    lastPageLink.indexOf("page=") + "page=".length(),
                    lastPageLink.indexOf(">"));
            this.totalPages = Integer.parseInt(lastPageNumberString);
        }

        String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
        return (JSONArray) jsonObject.get(resource);
    }
}
