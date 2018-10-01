package nl.schiphol.schipholapp;

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

public class Main {
    private final String APP_ID = "c977b768";
    private final String APP_KEY = "e568d76b73e0f891716a76f6ff0dea56";

    public void process() {
        try {
            HttpResponse response = this.getResponse();
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JSONArray flights = this.getFlights(response);
                this.printFlights(flights);
            } else {
                System.out.println(
                        "Oops something went wrong\nHttp response code: " + response.getStatusLine().getStatusCode() + "\nHttp response body: "
                                + EntityUtils.toString(response.getEntity()));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Oops something went wrong\nPlease insert your APP_ID and APP_KEY as arguments");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private HttpResponse getResponse() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.schiphol.nl/public-flights/flights?app_id=" + APP_ID + "&app_key=" + APP_KEY);
        request.addHeader("ResourceVersion", "v3");
        return httpClient.execute(request);
    }

    private JSONArray getFlights(HttpResponse httpResponse) throws IOException, ParseException {
        String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
        return (JSONArray) jsonObject.get("flights");
    }

    private void printFlights(JSONArray flights) {
        System.out.println("found " + flights.size() + " flights");
        for (Object flightObject : flights) {
            JSONObject flight = (JSONObject) flightObject;

            Object scheduleTime = flight.get("scheduleTime");
            Object gate = flight.get("gate");
            JSONObject route = (JSONObject) flight.get("route");
            JSONArray destinations = (JSONArray) route.get("destinations");

            System.out.println(scheduleTime);
            System.out.println(gate);
            System.out.println(destinations);
            System.out.println();
        }
    }
}
