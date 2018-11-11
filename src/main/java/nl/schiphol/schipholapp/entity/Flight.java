package nl.schiphol.schipholapp.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long apiId;

    private String gate;

    private String flightName;

    private String destination;

    private Date scheduleDate;

    private boolean isDeparture;

    private String airlineIata;

    private String airlineIcao;

    private long terminal;

    private String aircraftMainType;

    private String aircraftSubType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getApiId() {
        return apiId;
    }

    public void setApiId(long apiId) {
        this.apiId = apiId;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public String getFlightName() {
        return flightName;
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public boolean isDeparture() {
        return isDeparture;
    }

    public void setDeparture(boolean departure) {
        isDeparture = departure;
    }

    public String getAirlineIata() {
        return airlineIata;
    }

    public void setAirlineIata(String airlineIata) {
        this.airlineIata = airlineIata;
    }

    public String getAirlineIcao() {
        return airlineIcao;
    }

    public void setAirlineIcao(String airlineIcao) {
        this.airlineIcao = airlineIcao;
    }

    public long getTerminal() {
        return terminal;
    }

    public void setTerminal(long terminal) {
        this.terminal = terminal;
    }

    public String getAircraftMainType() {
        return aircraftMainType;
    }

    public void setAircraftMainType(String aircraftMainType) {
        this.aircraftMainType = aircraftMainType;
    }

    public String getAircraftSubType() {
        return aircraftSubType;
    }

    public void setAircraftSubType(String aircraftSubType) {
        this.aircraftSubType = aircraftSubType;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "gate='" + gate + '\'' +
                ", flightName='" + flightName + '\'' +
                ", destination='" + destination + '\'' +
                ", scheduleDate=" + scheduleDate +
                ", isDeparture=" + isDeparture +
                ", airlineIata='" + airlineIata + '\'' +
                ", airlineIcao='" + airlineIcao + '\'' +
                ", terminal=" + terminal +
                ", aircraftMainType='" + aircraftMainType + '\'' +
                ", aircraftSubType='" + aircraftSubType + '\'' +
                '}';
    }
}
