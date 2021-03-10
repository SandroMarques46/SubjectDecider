package ctrl;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public class PairTime {
    //todo talvez meter getters e setters
    private String subj;
    public LocalTime beg;
    public LocalTime end;
    public PairTime(LocalTime beg, LocalTime end){
        this.beg = beg;
        this.end = end;
    }

    public PairTime(String beg, String end) {
        this.beg = stringToLocalTime(beg);
        this.end = stringToLocalTime(end);
    }

    //todo ver se apanha todas as execoes
    private LocalTime stringToLocalTime(String date) {
        String[] parts = date.split(":");
        try {
            if (parts.length == 1) {
                return LocalTime.of(Integer.parseInt(date), 0);
            } else if (parts.length == 2) {
                return LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e){
            throw new NumberFormatException("This is not a valid hour or valid minute : " + date);
        } catch (DateTimeException e){
            throw new DateTimeException("Not a valid date");
        }
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

    public boolean overlaps(PairTime pairTime) {
        return this.beg.equals(pairTime.beg) || this.end.equals(pairTime.end)
                || this.beg.isBefore(pairTime.end) && (this.end.isAfter(pairTime.end) || this.end.isAfter(pairTime.beg))
                || this.beg.isAfter(pairTime.beg) && this.end.isBefore(pairTime.end);
    }

    public int getMinutesLength() {
        return (int) ChronoUnit.MINUTES.between(beg, end);
    }

    public boolean isOutsideOfRange(PairTime range) {
        return this.beg.isBefore(range.beg) || this.end.isAfter(range.end);
    }

    @Override
    public String toString() {
        return beg + ":" + end;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public String getSubj() {
        return subj;
    }
}
