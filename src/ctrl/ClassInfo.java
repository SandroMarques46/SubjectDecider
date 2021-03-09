package ctrl;

import java.time.LocalTime;

public class ClassInfo {
    //BEG. HOUR, END HOUR, WEEK DAY, TIPO, PROF
    String week_day;
    public PairTime time;
    String type;
    String prof;

    public ClassInfo(String beg, String end, String week_day, String type, String prof) {
        this.time = new PairTime(beg,end);
        this.week_day = week_day;
        this.type = type;
        this.prof = prof;
    }


}
