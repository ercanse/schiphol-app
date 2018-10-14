package nl.schiphol.schipholapp.service;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {
    private FlightRepository flightRepository;

    public List<Flight> findAll() {
        return this.flightRepository.findAll();
    }

    public Flight findById(int id) {
        Optional<Flight> assetFlowStatus = this.flightRepository.findById(id);
        return assetFlowStatus.orElse(null);
    }

    public void save(Flight Flight) {
        this.flightRepository.save(Flight);
    }

    @Autowired
    public void setFlightRepository(FlightRepository assetFlowStatusRepository) {
        this.flightRepository = assetFlowStatusRepository;
    }
}
