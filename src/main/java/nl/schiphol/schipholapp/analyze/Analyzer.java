package nl.schiphol.schipholapp.analyze;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Analyzer implements ApplicationListener<ApplicationReadyEvent> {
    private Client client;

    private Map<Character, Integer> flightsByPier;

    private Map<Character, Map<String, Integer>> flightsByAirlineByPier;

    public Analyzer() {
        this.flightsByPier = new HashMap<>();
        this.flightsByAirlineByPier = new HashMap<>();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        JSONArray flights = this.client.process("flights", "v3");
//        this.printFlights(flights);
//        this.printFlightsByPier();
//        this.printFlightsByAirlineByPier();
    }

    @Autowired
    public void setClient(Client client) {
        this.client = client;
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
