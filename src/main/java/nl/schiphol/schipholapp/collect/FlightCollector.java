package nl.schiphol.schipholapp.collect;

import nl.schiphol.schipholapp.Application;
import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.service.FlightService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class FlightCollector extends Collector {
    private final Logger log = LoggerFactory.getLogger(FlightCollector.class);

    private final String collectorMode = "flights";

    private FlightService flightService;

    private String mode;
    private String apiVersion;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        this.mode = Application.getResource();
        this.apiVersion = Application.getApiVersion();
        if (this.collectorMode.equals(this.mode)) {
            this.collect(this.mode, this.apiVersion);
            this.initiateShutdown(0);
        }
    }

    void processData(JSONArray data) {
        System.out.println("Found " + data.size() + " results.");
        for (Object destinationObject : data) {
            Flight flight = this.createFlightObject((JSONObject) destinationObject);
            if (flight != null) {
//                log.info("{}, {}, {}", flight.getFlightName(), flight.getGate(), flight.getDestination());
//                this.saveDestination(flight);
            }
        }
    }

    private Flight createFlightObject(JSONObject flightObject) {
        Object gateObject = flightObject.get("gate");
        Object airlineObject = flightObject.get("prefixICAO");
        if (gateObject == null || airlineObject == null) {
            return null;
        }

        String gate = gateObject.toString();
        String flightName = flightObject.get("flightName").toString();
        JSONObject route = (JSONObject) flightObject.get("route");
        JSONArray destinations = (JSONArray) route.get("destinations");

        Flight flight = new Flight();
        flight.setFlightName(flightName);
        String destination = destinations.get(destinations.size() - 1).toString();
        if (destinations.size() > 1 && "d".equals(flightObject.get("flightDirection").toString())) {
            log.info(flightObject.get("scheduleTime").toString());
        }
        flight.setDestination(destination);
        flight.setGate(gate);
        return flight;
    }

    private void saveDestination(Flight flight) {
        try {
            this.flightService.save(flight);
        } catch (DataIntegrityViolationException e) {
            log.error("Skipping already existing tweet.");
        }
    }

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }
}
