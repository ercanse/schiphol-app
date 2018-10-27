package nl.schiphol.schipholapp.service;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {
    private FlightRepository flightRepository;

    public List<Flight> findAll() {
        return this.flightRepository.findAll();
    }

    public List<Flight> findAllByDate(String date) {
        return flightRepository.findAllByDate(date);
    }

    public void save(Flight Flight) {
        this.flightRepository.save(Flight);
    }

    @Autowired
    public void setFlightRepository(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
}
