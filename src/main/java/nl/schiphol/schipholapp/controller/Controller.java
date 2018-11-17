package nl.schiphol.schipholapp.controller;

import nl.schiphol.schipholapp.analyze.Analyzer;
import nl.schiphol.schipholapp.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    private Logger log = LoggerFactory.getLogger(Controller.class);

    private FlightService flightService;

    private Analyzer analyzer;

    @RequestMapping("/getDates")
    @CrossOrigin("*")
    public ResponseEntity<List<Date>> getDates() {
        log.info("getDates");
        List<Date> results = this.flightService.getDates();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping("/getDestinationsByPierOnDate")
    @CrossOrigin("*")
    public ResponseEntity<Map> getDestinationsByPierOnDate(@RequestParam("date") String date) {
        log.info("getDestinationsByPierOnDate for date {}", date);
        Map results = this.analyzer.getDestinationsByPierOnDate(date);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @RequestMapping("/getFlightsByPierOnDate")
    @CrossOrigin("*")
    public ResponseEntity<List<Map>> getFlightsByPierOnDate(@RequestParam("date") String date) {
        log.info("getFlightsByPierOnDate for date {}", date);
        List<Map> results = this.analyzer.getFlightsByPierOnDate(date);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @Autowired
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }
}
