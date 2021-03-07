package ctrl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Writer {
    public static final String FOLDER = "OUTPUT/";

    public static void writeToFileByWeekDays(ArrayList<Node<Classes>> list, String outputFile) throws Exception {
        createFolder();
        Controller ctrl = new Controller();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FOLDER + outputFile))) {
            LinkedList<String> days_list = Days.getOrderList();
            bw.write(Syntax.LINE_IGNORE_SEP + " Number of combinations : " + list.size() + "\n");
            HashMap<String, LinkedList<PairTime>> hashMap = new HashMap<>();
            for (Node<Classes> t : list) {
                ctrl.insertDaysOnMap(hashMap, t);
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

    private static void createFolder() throws Exception {
        File directory = new File(FOLDER);
        if (! directory.exists()){
            if(!directory.mkdir()){
                throw new Exception("Something went wron while creating folder : " + FOLDER);
            }
            // If required to make the entire directory path including parents use .mkdirs()
        }
    }

    public static void writeToFileBySubject(LinkedList<String> subjs, ArrayList<Node<Classes>> list, String outputFile) throws Exception {
        createFolder();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FOLDER + outputFile))) {
            bw.write(Syntax.LINE_IGNORE_SEP + " Number of combinations : " + list.size() + "\n");
            if (subjs != null && !subjs.isEmpty()) {
                int i = 0;
                for (Node<Classes> t : list) {
                    while (t.previous != null) t = t.previous;
                    while (t != null) {
                        bw.write(t.value.turma + Syntax.SEP + subjs.get(i++) + Syntax.SEP);
                        for (ClassInfo p : t.value.info) {
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
