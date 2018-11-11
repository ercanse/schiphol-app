package nl.schiphol.schipholapp.collect;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.service.FlightService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    public void testCreateFlightObject() throws ParseException {
        long id = 1L;
        String flightName = "The Netherlands";
        String destination = "SAW";
        String gate = "D44";
        String prefixICAO = "KLM";
        String scheduleDate = "2018-01-01";

        JSONArray destinations = new JSONArray();
        destinations.add("SAW");
        JSONObject route = new JSONObject();
        route.put("destinations", destinations);

        when(this.jsonObject.get("id")).thenReturn(id);
        when(this.jsonObject.get("flightName")).thenReturn(flightName);
        when(this.jsonObject.get("destination")).thenReturn(destination);
        when(this.jsonObject.get("gate")).thenReturn(gate);
        when(this.jsonObject.get("prefixICAO")).thenReturn(prefixICAO);
        when(this.jsonObject.get("scheduleDate")).thenReturn(scheduleDate);
        when(this.jsonObject.get("route")).thenReturn(route);

        Flight flight = this.flightCollector.createFlightObject(this.jsonObject);
        assertEquals(flightName, flight.getFlightName());
        assertEquals(destination, flight.getDestination());
        assertEquals(gate, flight.getGate());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate), flight.getScheduleDate());
    }

    @Test
    public void testSaveFlight() {
        assertTrue(this.flightCollector.saveFlight(this.flight));
    }

    @Test
    public void testSaveDuplicateFlight() {
        doThrow(new DataIntegrityViolationException("")).when(this.flightService).save(flight);
        assertFalse(this.flightCollector.saveFlight(this.flight));
    }
}
