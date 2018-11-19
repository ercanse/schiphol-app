package nl.schiphol.schipholapp.collect;

import nl.schiphol.schipholapp.web.Client;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public abstract class Collector implements ApplicationListener<ApplicationReadyEvent> {
    private ApplicationContext appContext;

    private Client client;

    private String mode;
    private String apiVersion;

    public void initiateShutdown(int returnCode) {
        SpringApplication.exit(appContext, () -> returnCode);
    }

    public void collect(String resource, String apiVersion) {
        JSONArray data = this.client.process(resource, apiVersion);
        this.processData(data);
    }

    abstract void processData(JSONArray data);

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Autowired
    public void setClient(Client client) {
        this.client = client;
    }
}
