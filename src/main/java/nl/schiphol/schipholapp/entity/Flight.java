package nl.schiphol.schipholapp.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Flight {
    @Id
    private int id;

    private String gate;

    private String flightName;

    private String destination;

    private Date scheduleDate;

    private boolean isDeparture;

    private String iata;

    private String icao;

    private int terminal;

    private String iataMain;

    private String iataSub;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public int getTerminal() {
        return terminal;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }

    public String getIataMain() {
        return iataMain;
    }

    public void setIataMain(String iataMain) {
        this.iataMain = iataMain;
    }

    public String getIataSub() {
        return iataSub;
    }

    public void setIataSub(String iataSub) {
        this.iataSub = iataSub;
    }
}
