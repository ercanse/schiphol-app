package nl.schiphol.schipholapp.repository;

import nl.schiphol.schipholapp.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

}
