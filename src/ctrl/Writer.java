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
    public class Pair{
        String subj;
        PairTime pairTime;
        public Pair(String subj, PairTime pairTime){
            this.subj = subj;
            this.pairTime = pairTime;
        }
    }
    public static final String FOLDER = "OUTPUT/";

    public static void writeToFileByWeekDays(LinkedList<String> subjs, ArrayList<Node<Classes>> list, String outputFile) throws Exception {
        createFolder();
        Controller ctrl = new Controller();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FOLDER + outputFile))) {
            LinkedList<String> days_list = Days.getOrderList();
            bw.write(Syntax.LINE_IGNORE_SEP + " Number of combinations : " + list.size() + "\n");
            HashMap<String, LinkedList<PairTime>> hashMap = new HashMap<>();
            for (Node<Classes> t : list) {
                ctrl.insertDaysOnMap(hashMap, t,subjs);
                writeClasses(bw,subjs,t);
                for (String str : days_list) {
                    LinkedList<PairTime> pairTimes = hashMap.get(str);
                    bw.write(str + ":");
                    if (pairTimes != null) {
                        PairTime.order(pairTimes);
                        for (PairTime p : pairTimes) {
                            bw.write("("+p.getSubj()+")"+p.beg + "-" + p.end + Syntax.SEP);
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

    private static void writeClasses(BufferedWriter bw, LinkedList<String> subjs, Node<Classes> t) throws IOException {
        int idx = 0;
        while (t != null){
            try {
                bw.write(subjs.get(idx++) + ":" + t.value.turma + "   ");
            } catch (ArrayIndexOutOfBoundsException e){
                throw new ArrayIndexOutOfBoundsException("Subjects list is smaller than Node list size!");
            }
            t = t.next;
        }
        bw.write("\n");
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

}
