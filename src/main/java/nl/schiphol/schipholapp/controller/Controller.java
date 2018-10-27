package nl.schiphol.schipholapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    private Logger log = LoggerFactory.getLogger(Controller.class);

    @RequestMapping("/test")
    @CrossOrigin("*")
    public ResponseEntity<List<Map>> test() {
        Map<String, Object> pierAMap = new HashMap<>();
        pierAMap.put("pier", "A");
        pierAMap.put("flights", 10);
        Map<String, Object> pierBMap = new HashMap<>();
        pierBMap.put("pier", "B");
        pierBMap.put("flights", 30);
        Map<String, Object> pierCMap = new HashMap<>();
        pierCMap.put("pier", "C");
        pierCMap.put("flights", 30);
        Map<String, Object> pierDMap = new HashMap<>();
        pierDMap.put("pier", "D");
        pierDMap.put("flights", 50);

        List<Map> results = new ArrayList<>();
        results.add(pierAMap);
        results.add(pierBMap);
        results.add(pierCMap);
        results.add(pierDMap);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
