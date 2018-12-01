package nl.schiphol.schipholapp.analyze;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.service.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalyzerTest {
    private Analyzer analyzer;

    @Mock
    private FlightService flightService;

    private char pier;

    private String gate;

    private String country;

    @Before
    public void setUp() {
        this.pier = 'D';
        this.gate = "D44";
        this.country = "Turkey";
        Flight flight = this.createFlight();

        this.flightService = mock(FlightService.class);
        when(this.flightService.findAllByDate(anyString())).thenReturn(Collections.singletonList(flight));

        Object[] results = new Object[2];
        results[0] = this.gate;
        results[1] = this.country;
        when(this.flightService.findAllWithDestinationByDate(anyString())).thenReturn(Collections.singletonList(results));

        this.analyzer = new Analyzer();
        this.analyzer.setFlightService(this.flightService);
    }

    @Test
    public void testCalculateDestinationsByPierOnDate() {
        String date = "2018-01-01";
        Map<Character, Map<String, Integer>> result = this.analyzer.calculateDestinationsByPierOnDate(date);
        assertTrue(result.containsKey(this.pier));

        Map<String, Integer> map = result.get(this.pier);
        assertTrue(map.containsKey(this.country));
        assertEquals((int) map.get(this.country), 1);
    }

    @Test
    public void testCalculateFlightsByPierOnDate() {
        String date = "2018-01-01";
        Map<Character, Integer> result = this.analyzer.calculateFlightsByPierOnDate(date);
        assertTrue(result.containsKey(this.pier));
        assertEquals(1, (int) result.get(this.pier));
    }

    @Test
    public void testGetFlightsByPierOnDate() {
        String date = "2018-01-01";
        List<Map> results = this.analyzer.getFlightsByPierOnDate(date);

        assertEquals(1, results.size());
        Map<String, Object> result = results.get(0);
        assertEquals('D', result.get("pier"));
        assertEquals(1, result.get("flights"));
    }

    private Flight createFlight() {
        Flight flight = mock(Flight.class);
        when(flight.getGate()).thenReturn(this.gate);
        return flight;
    }
}
