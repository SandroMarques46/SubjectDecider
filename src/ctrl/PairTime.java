package ctrl;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public class PairTime {
    public LocalTime beg;
    public LocalTime end;
    public PairTime(LocalTime beg, LocalTime end){
        this.beg = beg;
        this.end = end;
    }

    public static void order(LinkedList<PairTime> pairTimes) {
        pairTimes.sort((o1, o2) -> {
            if (o1.beg.isBefore(o2.beg)) {
                return -1;
            } else if (o1.beg.isAfter(o2.beg)) {
                return 1;
            } else return 0; //equal
        });
    }

    public String getBeg() {
        return beg.toString();
    }

    public String getEnd() {
        return end.toString();
    }

    public boolean overlaps(PairTime pairTime) {
        return this.beg.equals(pairTime.beg) || this.end.equals(pairTime.end)
                || this.beg.isBefore(pairTime.end) && (this.end.isAfter(pairTime.end) || this.end.isAfter(pairTime.beg))
                || this.beg.isAfter(pairTime.beg) && this.end.isBefore(pairTime.end);
    }

    public long getMinutesLength() {
        return ChronoUnit.MINUTES.between(beg, end);
    }
}
