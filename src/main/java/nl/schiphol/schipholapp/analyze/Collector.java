package nl.schiphol.schipholapp.analyze;

import nl.schiphol.schipholapp.Application;
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
        this.printData(data);
    }

    private void printData(JSONArray data) {
        System.out.println("Found " + data.size() + " results.");
        for (Object flightObject : data) {
            JSONObject flight = (JSONObject) flightObject;
            Object iata = flight.get("iata");
            if (iata != null) {
                log.info(iata.toString());
            }
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
}
