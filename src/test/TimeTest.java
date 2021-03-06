package test;

import ctrl.PairTime;
import org.junit.Test;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class TimeTest {

    @Test
    public void timeTest1(){
        LocalTime start = LocalTime.of(9,30);
        LocalTime end = LocalTime.of(12,30);

        long hoursBetween = ChronoUnit.HOURS.between(start, end);
        long minutesBetween = ChronoUnit.MINUTES.between(start, end);

        assertEquals(hoursBetween,3);
        assertEquals(minutesBetween,180);
    }

    @Test
    public void timeTest2(){
        LocalTime start = LocalTime.of(8,0);
        LocalTime end = LocalTime.of(9,30);

        long hoursBetween = ChronoUnit.HOURS.between(start, end);
        long minutesBetween = ChronoUnit.MINUTES.between(start, end);

        assertEquals(hoursBetween,1);
        assertEquals(minutesBetween,90);
    }
    @Test
    public void nonOverlapTimeTest() {
        PairTime pairTime1 = new PairTime(LocalTime.of(8, 0), LocalTime.of(9, 30));
        PairTime pairTime2 = new PairTime(LocalTime.of(14, 0), LocalTime.of(15, 30));
        assertFalse(pairTime1.overlaps(pairTime2) || pairTime2.overlaps(pairTime1));

        pairTime1 = new PairTime(LocalTime.of(12,30),LocalTime.of(14,0));
        pairTime2 = new PairTime(LocalTime.of(9,30),LocalTime.of(12,30));
        assertFalse(pairTime1.overlaps(pairTime2) || pairTime2.overlaps(pairTime1));

        pairTime1 = new PairTime(LocalTime.of(12,30),LocalTime.of(14,0));
        pairTime2 = new PairTime(LocalTime.of(14,0),LocalTime.of(15,30));
        assertFalse(pairTime1.overlaps(pairTime2) || pairTime2.overlaps(pairTime1));
    }

    @Test
    public void pairTest(){
        PairTime pairTime1 = new PairTime(LocalTime.of(8,0),LocalTime.of(9,30));
        PairTime pairTime2 = new PairTime(LocalTime.of(8,0),LocalTime.of(11,0));
        assertTrue(pairTime1.overlaps(pairTime2) && pairTime2.overlaps(pairTime1));

        pairTime1 = new PairTime(LocalTime.of(9,30),LocalTime.of(11,0));
        pairTime2 = new PairTime(LocalTime.of(8,0),LocalTime.of(11,0));
        assertTrue(pairTime1.overlaps(pairTime2) && pairTime2.overlaps(pairTime1)); //same "end"

        pairTime1 = new PairTime(LocalTime.of(9,30),LocalTime.of(11,0));
        pairTime2 = new PairTime(LocalTime.of(8,0),LocalTime.of(12,30));
        assertTrue(pairTime1.overlaps(pairTime2) && pairTime2.overlaps(pairTime1)); //same "end"

        pairTime1 = new PairTime(LocalTime.of(9,30),LocalTime.of(14,0));
        pairTime2 = new PairTime(LocalTime.of(8,0),LocalTime.of(12,30));
        assertTrue(pairTime1.overlaps(pairTime2) && pairTime2.overlaps(pairTime1));

        pairTime1 = new PairTime(LocalTime.of(8,0),LocalTime.of(14,0));
        pairTime2 = new PairTime(LocalTime.of(9,30),LocalTime.of(12,30));
        assertTrue(pairTime1.overlaps(pairTime2) && pairTime2.overlaps(pairTime1));
    }

}
