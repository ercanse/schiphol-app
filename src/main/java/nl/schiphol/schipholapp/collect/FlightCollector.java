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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                log.info(flight.toString());
                this.saveFlight(flight);
            }
        }
    }

    public Flight createFlightObject(JSONObject flightObject) {
        Object gateObject = flightObject.get("gate");
        if (gateObject == null) {
            return null;
        }

        String scheduleDateString = flightObject.get("scheduleDate").toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date scheduleDate = null;
        try {
            scheduleDate = dateFormat.parse(scheduleDateString);
        } catch (ParseException e) {
            log.info("Could not parse date {}.", scheduleDateString);
        }

        Flight flight = new Flight();
        flight.setApiId((long) flightObject.get("id"));
        flight.setGate(gateObject.toString());
        flight.setFlightName(flightObject.get("flightName").toString());

        JSONObject route = (JSONObject) flightObject.get("route");
        if (route != null) {
            JSONArray destinations = (JSONArray) route.get("destinations");
            if (!destinations.isEmpty()) {
                String destination = destinations.get(destinations.size() - 1).toString();
                flight.setDestination(destination);
            }
        }

        flight.setScheduleDate(scheduleDate);
        Object terminalObject = flightObject.get("terminal");
        if (terminalObject != null) {
            flight.setTerminal(Long.parseLong(terminalObject.toString()));
        }
        flight.setDeparture("D".equals(flightObject.get("flightDirection")));

        JSONObject aircraftType = (JSONObject) flightObject.get("aircraftType");
        if (aircraftType != null) {
            Object iataMain = aircraftType.get("iatamain");
            if (iataMain != null) {
                flight.setAircraftMainType(iataMain.toString());
            }
            Object iataSub = aircraftType.get("iatasub");
            if (iataSub != null) {
                flight.setAircraftSubType(iataSub.toString());
            }
        }

        Object prefixIataObject = flightObject.get("prefixIATA");
        if (prefixIataObject != null) {
            flight.setAirlineIata(prefixIataObject.toString());
        }
        Object prefixIcaoObject = flightObject.get("prefixICAO");
        if (prefixIcaoObject != null) {
            flight.setAirlineIcao(prefixIcaoObject.toString());
        }
        return flight;
    }

    public boolean saveFlight(Flight flight) {
        boolean saved = false;
        try {
            this.flightService.save(flight);
            saved = true;
        } catch (DataIntegrityViolationException e) {
            log.error("Skipping already existing flight.");
        }
        return saved;
    }

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }
}
