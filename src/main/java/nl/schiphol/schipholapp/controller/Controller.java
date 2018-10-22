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
    public ResponseEntity<List<Map<String, Integer>>> test() {
        // todo: each pier as separate map
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 10);
        map.put("B", 30);
        map.put("C", 30);
        map.put("D", 50);

        List<Map<String, Integer>> list = new ArrayList<>();
        list.add(map);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
