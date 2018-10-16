package nl.schiphol.schipholapp.collect;

import nl.schiphol.schipholapp.Application;
import nl.schiphol.schipholapp.entity.Destination;
import nl.schiphol.schipholapp.service.DestinationService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class DestinationCollector extends Collector {
    private final Logger log = LoggerFactory.getLogger(DestinationCollector.class);

    private final String collectorMode = "destinations";

    private DestinationService destinationService;

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
            Destination destination = this.createDestinationObject((JSONObject) destinationObject);
            if (destination != null) {
                this.saveDestination(destination);
            }
        }
    }

    public Destination createDestinationObject(JSONObject destinationObject) {
        Object country = destinationObject.get("country");
        Object city = destinationObject.get("city");
        Object iata = destinationObject.get("iata");
        JSONObject publicName = (JSONObject) destinationObject.get("publicName");
        Object englishName = publicName.get("english");
        Object dutchName = publicName.get("dutch");

        if (country == null || city == null || iata == null || englishName == null || dutchName == null) {
            log.warn("Missing required field(s) - skipping destination");
            return null;
        }

        Destination destination = new Destination();
        destination.setCountry(country.toString());
        destination.setCity(city.toString());
        destination.setIata(iata.toString());
        destination.setEnglishName(englishName.toString());
        destination.setDutchName(dutchName.toString());
        return destination;
    }

    public boolean saveDestination(Destination destination) {
        boolean saved = false;
        log.info("Inserting destination with IATA code {}", destination.getIata());
        try {
            this.destinationService.save(destination);
            saved = true;
        } catch (DataIntegrityViolationException e) {
            log.error("Skipping already existing destination.");
        }
        return saved;
    }

    @Autowired
    public void setDestinationService(DestinationService destinationService) {
        this.destinationService = destinationService;
    }
}
