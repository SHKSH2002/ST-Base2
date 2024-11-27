package mizdooni.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class RatingTest {

    private Rating rating;
    @BeforeEach
    void setUp() {
        rating = new Rating();
    }
    @Test
    public void testGetStarCountForDecimalRoundDown() {
        rating.overall = 2.3;
        assertEquals(2, rating.getStarCount());
    }

    @Test
    public void testGetStarCountForAboveMax() {
        rating.overall = 6.2;
        assertEquals(5, rating.getStarCount());
    }

    @Test
    public void testGetStarCountForExactWholeNumber() {
        rating.overall = 3.0;
        assertEquals(3, rating.getStarCount());
    }

    @Test
    public void testGetStarCountForExactWholeNumberWithoutDecimal() {
        rating.overall = 3;
        assertEquals(3, rating.getStarCount());
    }

    @Test
    public void testGetStarCountForDecimalRoundUp() {
        rating.overall = 3.6;
        assertEquals(4, rating.getStarCount());
    }
}

