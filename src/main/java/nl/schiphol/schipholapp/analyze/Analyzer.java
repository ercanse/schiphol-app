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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Analyzer {
    private String appId;
    private String appKey;

    private int totalPages = 1;

    private Map<Character, Integer> flightsByPier;

    private Map<Character, Map<String, Integer>> flightsByAirlineByPier;

    private SortedMap<String, Integer> flightsByDestination;

    public Analyzer() {
        this.loadProperties();
        this.flightsByPier = new HashMap<>();
        this.flightsByAirlineByPier = new HashMap<>();
        this.flightsByDestination = new TreeMap<>();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream input = new FileInputStream("config/config.properties");
            properties.load(input);
            this.appId = properties.getProperty("appId");
            this.appKey = properties.getProperty("appKey");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() {
        try {
            int currentPage = 0;
            JSONArray flights = new JSONArray();
            while (currentPage < this.totalPages) {
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
            this.printFlightsByDestination();
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

    private JSONObject getDestinationResponse(String airportCode) throws IOException, ParseException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.schiphol.nl/public-flights/destinations/" + airportCode + "?app_id=" + appId + "&app_key=" + appKey);
        request.addHeader("ResourceVersion", "v1");
        HttpResponse httpResponse = httpClient.execute(request);

        String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);

        return jsonObject;
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

            boolean isDeparture = flightDirection.equals("D");

            JSONObject route = (JSONObject) flight.get("route");
            JSONArray destinations = (JSONArray) route.get("destinations");

//            System.out.println(flightName);
            String flightDirectionString = isDeparture ? "Departure:" : "Arrival:";
//            System.out.print(flightDirectionString + " " + scheduleDate + " ");
//            System.out.println(scheduleTime);

//            System.out.println(gate);
//            System.out.println(destinations);
//            System.out.println();

            char pier = gate.charAt(0);
            this.flightsByPier.putIfAbsent(pier, 0);
            this.flightsByPier.put(pier, this.flightsByPier.get(pier) + 1);

            this.flightsByAirlineByPier.putIfAbsent(pier, new HashMap<>());
            Map<String, Integer> map = this.flightsByAirlineByPier.get(pier);
            map.putIfAbsent(airline, 0);
            map.put(airline, map.get(airline) + 1);

            if (isDeparture) {
                try {
                    JSONObject jsonObject = this.getDestinationResponse(destinations.get(destinations.size() - 1).toString());
                    String destination = jsonObject.get("country") + ", " + jsonObject.get("city");
                    this.flightsByDestination.putIfAbsent(destination, 0);
                    this.flightsByDestination.put(destination, this.flightsByDestination.get(destination) + 1);
                } catch (IOException | ParseException e) {
                    System.out.println("ERROR! " + e.getMessage());
                }
            }
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

    private void printFlightsByDestination() {
        SortedMap<String, Integer> sortedMap = this.sortMapByValue(this.flightsByDestination);
        for (Map.Entry entry : sortedMap.entrySet()) {
            int value = (int)entry.getValue();
            if (value < 10) {
                break;
            }
            System.out.println(entry.getKey() + " : " + value + " flights");
        }
    }

    private TreeMap<String, Integer> sortMapByValue(SortedMap<String, Integer> map) {
        Comparator<String> comparator = new ValueComparator(map);
        TreeMap<String, Integer> result = new TreeMap<>(comparator);
        result.putAll(map);
        return result;
    }

    class ValueComparator implements Comparator<String> {
        HashMap<String, Integer> map = new HashMap<>();

        public ValueComparator(SortedMap<String, Integer> map) {
            this.map.putAll(map);
        }

        @Override
        public int compare(String s1, String s2) {
            if (map.get(s1) >= map.get(s2)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
