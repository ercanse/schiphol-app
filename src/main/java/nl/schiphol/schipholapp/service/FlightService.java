package nl.schiphol.schipholapp.service;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FlightService {
    private FlightRepository flightRepository;

    public List<Date> getDates() {
        return flightRepository.getDates();
    }

    public List<Flight> findAllByDate(String date) {
        return flightRepository.findAllByDate(date);
    }

    public List<Object[]> findAllWithDestinationByDate(String date) {
        return flightRepository.getFlightsWithDestinationOnDate(date);
    }

    public void save(Flight flight) {
        this.flightRepository.save(flight);
    }

    @Autowired
    public void setFlightRepository(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
}
