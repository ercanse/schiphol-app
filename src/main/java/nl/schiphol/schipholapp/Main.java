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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private final String APP_ID = "c977b768";
    private final String APP_KEY = "e568d76b73e0f891716a76f6ff0dea56";

    private int totalPages = 1;

    private Map<Character, Integer> flightsByPier;

    public Main() {
        this.flightsByPier = new HashMap<>();
    }

    public void process() {
        try {
            int currentPage = 0;
            JSONArray flights = new JSONArray();
            while (currentPage < 50) {
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
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse getResponse(int pageNumber) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.schiphol.nl/public-flights/flights?app_id=" + APP_ID + "&app_key=" + APP_KEY + "&page=" + pageNumber);
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

            Object gate = flight.get("gate");
            if (gate == null) {
                continue;
            }

            String flightDirection = flight.get("flightDirection").toString();
            String flightName = flight.get("flightName").toString();
            String scheduleDate = flight.get("scheduleDate").toString();
            String scheduleTime = flight.get("scheduleTime").toString();
            JSONObject route = (JSONObject) flight.get("route");
            JSONArray destinations = (JSONArray) route.get("destinations");

            System.out.println(flightName);
            String flightDirectionString = flightDirection.equals("d") ? "Departure:" : "Arrival:";
            System.out.print(flightDirectionString + " " + scheduleDate + " ");
            System.out.println(scheduleTime);

            System.out.println(gate);
            System.out.println(destinations);
            System.out.println();

            char pier = gate.toString().charAt(0);
            if (!this.flightsByPier.containsKey(pier)) {
                this.flightsByPier.put(pier, 0);
            }
            this.flightsByPier.put(pier, this.flightsByPier.get(pier) + 1);
        }
    }

    private void printFlightsByPier() {
        for (Map.Entry entry : this.flightsByPier.entrySet()) {
            System.out.println(entry.getKey() + " pier: " + entry.getValue() + " flights");
        }
    }
}
