package nl.schiphol.schipholapp;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {
    private String appId;
    private String appKey;

    private int totalPages = 1;

    private Map<Character, Integer> flightsByPier;

    private Map<Character, Map<String, Integer>> flightsByAirlineByPier;

    public Main() {
        this.loadProperties();
        this.flightsByPier = new HashMap<>();
        this.flightsByAirlineByPier = new HashMap<>();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream("config/config.properties");
            properties.load(input);
            this.appId = properties.getProperty("appId");
            this.appKey = properties.getProperty("appKey");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() {
        try {
            int currentPage = 0;
            JSONArray flights = new JSONArray();
            while (currentPage < 5) {
                HttpResponse response = this.getResponse(currentPage);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    flights.addAll(this.getFlights(response));
                } else {
                    System.out.println(
                            "Oops something went wrong\nHttp response code: " + response.getStatusLine().getStatusCode() + "\nHttp response body: "
                                    + EntityUtils.toString(response.getEntity()));
                }
                currentPage++;
            }
            this.printFlights(flights);
            this.printFlightsByPier();
            System.out.println();
            this.printFlightsByAirlineByPier();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse getResponse(int pageNumber) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.schiphol.nl/public-flights/flights?app_id=" + appId + "&app_key=" + appKey + "&page=" + pageNumber);
        request.addHeader("ResourceVersion", "v3");
        return httpClient.execute(request);
    }

    private JSONArray getFlights(HttpResponse httpResponse) throws IOException, ParseException {
        Header linkHeader = httpResponse.getHeaders("Link")[0];
        String headerValue = linkHeader.getValue();
        if (headerValue.contains("last")) {
            String[] headerValues = linkHeader.getValue().split(",");
            String lastPageLink = headerValues[0].contains("last") ? headerValues[0] : headerValues[1];

            String lastPageNumberString = lastPageLink.substring(lastPageLink.indexOf("page=") + "page=".length(), lastPageLink.indexOf(">"));
            this.totalPages = Integer.parseInt(lastPageNumberString);
        }

        String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
        return (JSONArray) jsonObject.get("flights");
    }

    private void printFlights(JSONArray flights) {
        System.out.println("Found " + flights.size() + " flights.");
        for (Object flightObject : flights) {
            JSONObject flight = (JSONObject) flightObject;

            Object gateObject = flight.get("gate");
            Object airlineObject = flight.get("prefixICAO");
            if (gateObject == null || airlineObject == null) {
                continue;
            }

            String gate = gateObject.toString();
            String flightDirection = flight.get("flightDirection").toString();
            String flightName = flight.get("flightName").toString();
            String scheduleDate = flight.get("scheduleDate").toString();
            String scheduleTime = flight.get("scheduleTime").toString();
            String airline = airlineObject.toString();

            JSONObject route = (JSONObject) flight.get("route");
            JSONArray destinations = (JSONArray) route.get("destinations");

            System.out.println(flightName);
            String flightDirectionString = flightDirection.equals("d") ? "Departure:" : "Arrival:";
            System.out.print(flightDirectionString + " " + scheduleDate + " ");
            System.out.println(scheduleTime);

            System.out.println(gate);
            System.out.println(destinations);
            System.out.println();

            char pier = gate.charAt(0);
            this.flightsByPier.putIfAbsent(pier, 0);
            this.flightsByPier.put(pier, this.flightsByPier.get(pier) + 1);

            this.flightsByAirlineByPier.putIfAbsent(pier, new HashMap<>());
            Map<String, Integer> map = this.flightsByAirlineByPier.get(pier);
            map.putIfAbsent(airline, 0);
            map.put(airline, map.get(airline) + 1);
        }
    }

    private void printFlightsByPier() {
        for (Map.Entry entry : this.flightsByPier.entrySet()) {
            System.out.println(entry.getKey() + " pier: " + entry.getValue() + " flights");
        }
    }

    private void printFlightsByAirlineByPier() {
        for (Map.Entry entry : this.flightsByAirlineByPier.entrySet()) {
            System.out.println(entry.getKey() + " pier: " + entry.getValue() + " flights");
        }
    }
}
