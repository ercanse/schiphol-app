package nl.schiphol.schipholapp.analyze;

import nl.schiphol.schipholapp.entity.Flight;
import nl.schiphol.schipholapp.service.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnalyzerTest {
    private Analyzer analyzer;

    @Mock
    private FlightService flightService;

    @Before
    public void setUp() {
        Flight flight = this.createFlight();

        this.flightService = mock(FlightService.class);
        when(this.flightService.findAllByDate(anyString())).thenReturn(Collections.singletonList(flight));

        this.analyzer = new Analyzer();
        this.analyzer.setFlightService(this.flightService);
    }

    @Test
    public void test() {
        String date = "2018-01-01";
        List<Map> results = this.analyzer.getFlightsByPierOnDate(date);

        assertEquals(1, results.size());
        Map<String, Object> result = results.get(0);
        assertEquals('D', result.get("pier"));
        assertEquals(1, result.get("flights"));
    }

    private Flight createFlight() {
        Flight flight = mock(Flight.class);
        when(flight.getGate()).thenReturn("D44");
        return flight;
    }
}
