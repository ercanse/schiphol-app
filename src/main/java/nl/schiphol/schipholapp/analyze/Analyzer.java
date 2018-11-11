package nl.schiphol.schipholapp.analyze;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Analyzer {
    private final Logger log = LoggerFactory.getLogger(Analyzer.class);

    private FlightService flightService;

    public List<Map> getDestinationsByPierOnDate(String date) {
        List<Map> results = new ArrayList<>();
        this.calculateDestinationsByPierOnDate(date);
        return results;
    }

    public List<Map> calculateDestinationsByPierOnDate(String date) {
        List<Map> results = new ArrayList<>();

        List<Flight> flights = this.flightService.findAllByDate(date);

        Map<Character, Map<String, Integer>> flightsByPier = new HashMap<>();
        char pier;
        for (Flight flight : flights) {
            String iata = flight.getDestination();

            pier = flight.getGate().charAt(0);
            flightsByPier.putIfAbsent(pier, new HashMap<>());

            flightsByPier.get(pier).putIfAbsent(iata, 0);
            flightsByPier.get(pier).put(iata, flightsByPier.get(pier).get(iata) + 1);
        }

        for (Map.Entry entry : flightsByPier.entrySet()) {
            log.info("Pier: {}", entry.getKey());
            log.info("Destinations: {}\n", entry.getValue());
        }

        return results;
    }

    public List<Map> getFlightsByPierOnDate(String date) {
        List<Map> results = new ArrayList<>();

        Map<Character, Integer> flightsByPier = this.calculateFlightsByPierOnDate(date);
        for (Map.Entry entry : flightsByPier.entrySet()) {
            Map<String, Object> pierMap = new HashMap<>();
            pierMap.put("pier", entry.getKey());
            pierMap.put("flights", entry.getValue());
            results.add(pierMap);
        }

        return results;
    }

    public Map<Character, Integer> calculateFlightsByPierOnDate(String date) {
        Map<Character, Integer> flightsByPier = new HashMap<>();

        List<Flight> flights = this.flightService.findAllByDate(date);
        log.info("Found {} flights on date {}", flights.size(), date);

        char pier;
        for (Flight flight : flights) {
            pier = flight.getGate().charAt(0);
            flightsByPier.putIfAbsent(pier, 0);
            flightsByPier.put(pier, flightsByPier.get(pier) + 1);
        }

        return flightsByPier;
    }

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }
}
