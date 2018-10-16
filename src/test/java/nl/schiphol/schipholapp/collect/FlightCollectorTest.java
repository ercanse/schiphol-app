package nl.schiphol.schipholapp.collect;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.service.FlightService;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightCollectorTest {
    private FlightCollector flightCollector;

    @Mock
    private FlightService flightService;

    @Mock
    private Flight flight;

    @Mock
    private JSONObject jsonObject;

    @Before
    public void setUp() {
        this.flight = mock(Flight.class);
        this.flightService = mock(FlightService.class);
        this.jsonObject = mock(JSONObject.class);

        this.flightCollector = new FlightCollector();
        this.flightCollector.setFlightService(this.flightService);
    }

    @Test
    public void testCreateDestinationObject() {
        String flightName = "The Netherlands";
        String destination = "Amsterdam";
        String gate = "AMS";
        Date scheduleDate = new Date();

        when(this.jsonObject.get("flightName")).thenReturn(flightName);
        when(this.jsonObject.get("destination")).thenReturn(destination);
        when(this.jsonObject.get("gate")).thenReturn(gate);
        when(this.jsonObject.get("scheduleDate")).thenReturn(scheduleDate);

        Flight flight = this.flightCollector.createFlightObject(this.jsonObject);
        assertEquals(flightName, flight.getFlightName());
        assertEquals(destination, flight.getDestination());
        assertEquals(gate, flight.getGate());
        assertEquals(scheduleDate, flight.getDate());
    }

    @Test
    public void testSaveDestination() {
        assertTrue(this.flightCollector.saveFlight(this.flight));
    }

    @Test
    public void testSaveDuplicateDestination() {
        doThrow(new DataIntegrityViolationException("")).when(this.flightService).save(flight);
        assertFalse(this.flightCollector.saveFlight(this.flight));
    }
}
