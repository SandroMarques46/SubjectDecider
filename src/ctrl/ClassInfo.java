package ctrl;

import java.time.LocalTime;

public class ClassInfo {
    //BEG. HOUR, END HOUR, WEEK DAY, TIPO, PROF
    String week_day;
    public PairTime time;
    String type;
    String prof;

    public ClassInfo(String beg, String end, String week_day, String type, String prof) {
        this.time = new PairTime(stringToLocalTime(beg),stringToLocalTime(end));
        this.week_day = week_day;
        this.type = type;
        this.prof = prof;
    }

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
        }
    }
}
