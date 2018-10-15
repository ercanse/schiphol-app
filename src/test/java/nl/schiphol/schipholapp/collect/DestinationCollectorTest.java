package nl.schiphol.schipholapp.collect;

import nl.schiphol.schipholapp.entity.Destination;
import nl.schiphol.schipholapp.service.DestinationService;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DestinationCollectorTest {
    private DestinationCollector destinationCollector;

    @Mock
    private DestinationService destinationService;

    @Mock
    private Destination destination;

    @Mock
    private JSONObject jsonObject;

    @Before
    public void setUp() {
        this.destination = mock(Destination.class);
        this.destinationService = mock(DestinationService.class);
        this.jsonObject = mock(JSONObject.class);

        this.destinationCollector = new DestinationCollector();
        this.destinationCollector.setDestinationService(this.destinationService);
    }

    @Test
    public void testCreateDestinationObject() {
        String country = "The Netherlands";
        String city = "Amsterdam";
        String iata = "AMS";
        String name = "Schiphol";

        when(this.jsonObject.get("country")).thenReturn(country);
        when(this.jsonObject.get("city")).thenReturn(city);
        when(this.jsonObject.get("iata")).thenReturn(iata);

        JSONObject publicNameObject = mock(JSONObject.class);
        when(publicNameObject.get("english")).thenReturn(name);
        when(publicNameObject.get("dutch")).thenReturn(name);
        when(this.jsonObject.get("publicName")).thenReturn(publicNameObject);

        Destination destination = this.destinationCollector.createDestinationObject(this.jsonObject);
        assertEquals(country, destination.getCountry());
        assertEquals(city, destination.getCity());
        assertEquals(iata, destination.getIata());
        assertEquals(name, destination.getEnglishName());
        assertEquals(name, destination.getDutchName());
    }

    @Test
    public void testSaveDestination() {
        assertTrue(this.destinationCollector.saveDestination(this.destination));
    }

    @Test
    public void testSaveDuplicateDestination() {
        doThrow(new DataIntegrityViolationException("")).when(this.destinationService).save(destination);
        assertFalse(this.destinationCollector.saveDestination(this.destination));
    }
}
