package ctrl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Writer{

    public static void writeToFileByWeekDays(ArrayList<Node<Turmas>> list, String outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            LinkedList<String> days_list = Days.getOrderList();
            bw.write(Syntax.LINE_IGNORE_SEP + " Number of combinations : " + list.size() + "\n");
            HashMap<String, LinkedList<PairTime>> hashMap = new HashMap<>();
            for (Node<Turmas> t : list) {
                hashMap.clear();
                while (t.previous != null) t = t.previous;
                while (t != null) {
                    for (Params p : t.value.info) {
                        if (hashMap.get(p.week_day) == null) {
                            hashMap.put(p.week_day, new LinkedList<>(Collections.singleton(p.time)));
                        } else {
                            hashMap.get(p.week_day).add(p.time);
                        }
                    }
                    t = t.next;
                }
                for (String str : days_list) {
                    LinkedList<PairTime> pairTimes = hashMap.get(str);
                    bw.write(str + ":");
                    if (pairTimes != null) {
                        PairTime.order(pairTimes);
                        for (PairTime p : pairTimes) {
                            bw.write(p.beg + "-" + p.end + Syntax.SEP);
                        }
                    } else {
                        bw.write("---");
                    }
                    bw.write("\n");
                }
                bw.write("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFileBySubject(LinkedList<String> subjs, ArrayList<Node<Turmas>> list, String outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(Syntax.LINE_IGNORE_SEP + " Number of combinations : " + list.size() + "\n");
            if (subjs != null && !subjs.isEmpty()) {
                int i = 0;
                for (Node<Turmas> t : list) {
                    while (t.previous != null) t = t.previous;
                    while (t != null) {
                        bw.write(t.value.turma + Syntax.SEP + subjs.get(i++) + Syntax.SEP);
                        for (Params p : t.value.info) {
                            bw.write(p.week_day + "=" + p.time.getBeg() + " to " + p.time.getEnd() + "\t");
                        }
                        bw.write("\n");
                        t = t.next;
                    }
                    i = 0;
                    bw.write("\n\n");
                }
            } else {
                throw new IllegalArgumentException("Subjects Array is empty/null!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
