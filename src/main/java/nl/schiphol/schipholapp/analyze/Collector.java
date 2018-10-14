package nl.schiphol.schipholapp.analyze;

import nl.schiphol.schipholapp.Application;
import nl.schiphol.schipholapp.entity.Destination;
import nl.schiphol.schipholapp.service.DestinationService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Collector implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger log = LoggerFactory.getLogger(Collector.class);

    private ApplicationContext appContext;

    private Client client;

    private DestinationService destinationService;

    private String mode;
    private String apiVersion;

    public void initiateShutdown(int returnCode) {
        SpringApplication.exit(appContext, () -> returnCode);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        this.mode = Application.getResource();
        this.apiVersion = Application.getApiVersion();
        this.collect(this.mode, this.apiVersion);
        this.initiateShutdown(0);
    }

    public void collect(String resource, String apiVersion) {
        JSONArray data = this.client.process(resource, apiVersion);
        this.processData(data);
    }

    private void processData(JSONArray data) {
        System.out.println("Found " + data.size() + " results.");
        for (Object flightObject : data) {
            JSONObject flight = (JSONObject) flightObject;

            Object country = flight.get("country");
            Object city = flight.get("city");
            Object iata = flight.get("iata");
            JSONObject publicName = (JSONObject) flight.get("publicName");
            Object englishName = publicName.get("english");
            Object dutchName = publicName.get("dutch");

            if (country == null || city == null || iata == null || englishName == null || dutchName == null) {
                continue;
            }

            Destination destination = new Destination();
            destination.setCountry(country.toString());
            destination.setCity(city.toString());
            destination.setIata(iata.toString());
            destination.setEnglishName(englishName.toString());
            destination.setDutchName(dutchName.toString());

            log.info("Inserting destination");
            this.destinationService.save(destination);
        }
    }

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Autowired
    public void setClient(Client client) {
        this.client = client;
    }

    @Autowired
    public void setDestinationService(DestinationService destinationService) {
        this.destinationService = destinationService;
    }
}
