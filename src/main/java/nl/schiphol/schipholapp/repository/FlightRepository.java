package nl.schiphol.schipholapp.repository;

import nl.schiphol.schipholapp.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    @Query(value = "SELECT DISTINCT schedule_date FROM flight f", nativeQuery = true)
    List<Date> getDates();

    @Query(value = "SELECT * FROM flight f WHERE f.schedule_date = :scheduleDate", nativeQuery = true)
    List<Flight> findAllByDate(@Param("scheduleDate") String date);

    @Query(value = "" +
            "SELECT f.gate, d.country " +
            "FROM flight f " +
            "JOIN destination d ON f.destination = d.iata " +
            "WHERE f.schedule_date = :scheduleDate", nativeQuery = true)
    List<Object[]> getFlightsWithDestinationOnDate(@Param("scheduleDate") String date);
}
