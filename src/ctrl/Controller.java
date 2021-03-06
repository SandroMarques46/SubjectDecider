package ctrl;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Controller {
    private static final String LINE_IGNORE_SEP = "//";
    private static final String TURMA_SEP = "/";
    private static final String SEP = ",";
    private static final String OUTPUT_FILE = "turmas.txt";
    private HashMap<Integer, LinkedList<String>> priorities = new HashMap<>();
    private HashMap<String, LinkedList<Turmas>> map = new HashMap<>();
    private static String HORARIOS_FILE = "horario.txt";
    private static String PRIO_FILE = "prioridade.txt";

    public static void main(String[] args) {
        Controller ctrl = new Controller();
        ctrl.indexPriorities(PRIO_FILE);
        ctrl.indexFromFile(HORARIOS_FILE);
        ctrl.choose();
    }

    private void choose() {
        LinkedList<String> prio = new LinkedList<>();
        for (int i = 1; ; i++) {
            LinkedList<String> name = priorities.get(i);
            if (name != null) {
                prio.addAll(name);
            } else break; //todo se prioridades nao tiverem seguidas ex 1,2,4 , ele vai ignorar o 4
        }
        ArrayList<Node<Turmas>> list = findNcombsfromlist(prio);
        removeNonValidCombs(list);
        writeToFileBySubject(prio, list, OUTPUT_FILE);
        writeToFileByWeekDays(prio, list, OUTPUT_FILE);
    }

    private void writeToFileByWeekDays(LinkedList<String> subjs, ArrayList<Node<Turmas>> list, String outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            LinkedList<String> days_list = Days.getOrderList();
            bw.write(LINE_IGNORE_SEP + " count : " + list.size() + "\n");
            if (subjs != null && !subjs.isEmpty()) {
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
                                bw.write(p.beg + "-" + p.end + SEP);
                            }
                        } else {
                            bw.write("---");
                        }
                        bw.write("\n");
                    }
                    bw.write("\n\n");
                }
            } else {
                throw new IllegalArgumentException("Subjects Array is empty/null!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if there are subjects overplaced.
     */
    private void removeNonValidCombs(ArrayList<Node<Turmas>> list) {
        boolean break_cycle = false;
        LinkedList<Node<Turmas>> nodes_to_remove = new LinkedList<>();
        for (Node<Turmas> t : list) {
            HashMap<String, LinkedList<PairTime>> hashMap = new HashMap<>();
            Node<Turmas> head = t;
            while (t.previous != null) t = t.previous;
            while (t != null) {
                for (Params p : t.value.info) {
                    if (hashMap.get(p.week_day) == null) {
                        hashMap.put(p.week_day, new LinkedList<>(Collections.singleton(p.time)));
                    } else {
                        if (checkValid(hashMap.get(p.week_day), p.time)) {
                            hashMap.get(p.week_day).add(p.time);
                        } else {
                            nodes_to_remove.add(head);
                            break_cycle = true;
                            break;
                        }
                    }
                }
                if (break_cycle) {
                    break_cycle = false;
                    break;
                }
                t = t.next;
            }
        }
        for (Node<Turmas> node : nodes_to_remove) {
            list.remove(node);
        }
        int x = 3;
    }

    /**
     * Checks if pair "time" can be inserted into pairList (if it doesnt overlaps other times)
     */
    private boolean checkValid(LinkedList<PairTime> pairlist, PairTime time) {
        for (PairTime pairTime : pairlist) {
            if (time.overlaps(pairTime)) return false;
        }
        return true;
    }

    private void writeToFileBySubject(LinkedList<String> subjs, ArrayList<Node<Turmas>> list, String outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(LINE_IGNORE_SEP + " count : " + list.size() + "\n");
            if (subjs != null && !subjs.isEmpty()) {
                int i = 0;
                for (Node<Turmas> t : list) {
                    while (t.previous != null) t = t.previous;
                    while (t != null) {
                        bw.write(t.value.turma + SEP + subjs.get(i++) + SEP);
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

    /**
     * From the ordered list of subjects finds the total number of valid "arranjements" (num_comb)
     * Returns a List of Lists of"Pair" of size "num_comb"
     */
    private ArrayList<Node<Turmas>> findNcombsfromlist(LinkedList<String> subj_list) {
        ArrayList<Node<Turmas>> list = new ArrayList<>();
        createListWithNnodes(list, subj_list.getFirst());
        int num_comb = list.size();
        for (int i = 1; i < subj_list.size(); i++) {
            if (map.get(subj_list.get(i)) == null) {
                System.err.println("WARNING: Missing subject \"" + subj_list.get(i) + "\" from \"" + HORARIOS_FILE + "\" while it is stated in \"" + PRIO_FILE + "\"");
            } else {
                num_comb *= map.get(subj_list.get(i)).size();
                if (list.size() < num_comb) {
                    multiplySizeByN(list, num_comb);
                }
                int aux = 0, aux2 = 0;
                LinkedList<Turmas> turmas = map.get(subj_list.get(i));
                int sep = list.size() / turmas.size();
                while (aux < list.size()) {
                    list.get(aux).add(new Node<>(turmas.get(aux2)));
                    list.set(aux, list.get(aux).next);
                    aux++;
                    if (aux == sep) {
                        aux2++;
                        sep += sep;
                    }
                }
            }
        }
        for (Node<Turmas> t : list) {
            while (t.previous != null) t = t.previous;
            while (t != null) {
                System.out.print(t.value.turma + ", ");
                t = t.next;
            }
            System.out.println();
        }
        return list;
    }

    private void createListWithNnodes(ArrayList<Node<Turmas>> list, String subject) {
        for (Turmas t : map.get(subject)) {
            list.add(new Node<>(t));
        }
    }

    private void multiplySizeByN(ArrayList<Node<Turmas>> list, int newSize) {
        if (newSize == 1) return;
        LinkedList<Node<Turmas>> original = new LinkedList<>(list);
        while (list.size() < newSize) {
            list.addAll(makeNewNodeInstance(original));
        }
    }

    public static ArrayList<Node<Turmas>> makeNewNodeInstance(LinkedList<Node<Turmas>> original) {
        ArrayList<Node<Turmas>> toReturn = new ArrayList<>();
        for (Node<Turmas> node : original) {
            while (node.previous != null) node = node.previous;
            Node<Turmas> newNode = new Node<>();
            newNode.value = node.value;
            while (node.next != null) {
                newNode.addVal(node.next.value);
                newNode = newNode.next;
                node = node.next;
            }
            toReturn.add(newNode);
        }
        for (int i = 0; i < toReturn.size(); i++) {
            Node<Turmas> node = toReturn.get(i);
            while (node.next != null) node = node.next;
            toReturn.set(i, node);
        }
        return toReturn;
    }

    private void indexPriorities(String prio_file) {
        try (BufferedReader fr = new BufferedReader(new FileReader(prio_file))) {
            String str;
            while ((str = fr.readLine()) != null) {
                String[] parts = str.split(SEP);
                //name,prio val
                try {
                    if (priorities.get(Integer.parseInt(parts[1])) == null) {
                        priorities.put(Integer.valueOf(parts[1]), new LinkedList<>(Collections.singleton(parts[0])));
                    } else {
                        LinkedList<String> list_cadeiras = priorities.get(Integer.valueOf(parts[1]));
                        if (!list_cadeiras.contains(parts[0])) {
                            list_cadeiras.add(parts[0]);
                        } else
                            throw new DuplicateNameException("Subject \"" + parts[0] + "\" is duplicated on file " + prio_file);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Cannot parse \"" + parts[1] + "\" to integer on line : \"" + str + "\"");
                } catch (DuplicateNameException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("FILE NOT FOUND");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void indexFromFile(String horario_file) {
        String str = null;
        try (BufferedReader fr = new BufferedReader(new FileReader(horario_file))) {
            while ((str = fr.readLine()) != null) {
                str = str.trim();
                if (!str.contains(LINE_IGNORE_SEP) && str.indexOf(LINE_IGNORE_SEP) != 0) {
                    //NAME, TURMA, BEG. HOUR, END HOUR, WEEK DAY, TIPO, PROF
                    String[] parts = str.split(SEP);
                    if (parts.length != 7) {
                        throw new IllegalArgumentException();
                    }
                    Params p = new Params(parts[2], parts[3], parts[4], parts[5], parts[6]);
                    String[] turmasarr = parts[1].split(TURMA_SEP);
                    for (String turma : turmasarr) {
                        if (map.get(parts[0]) == null) {
                            map.put(parts[0], new LinkedList<>(Collections.singletonList(new Turmas(turma, new LinkedList<>(Collections.singletonList(p))))));
                        } else {
                            LinkedList<Turmas> list = map.get(parts[0]);
                            add(list, turma, p);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("FILE NOT FOUND");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Line is not valid : " + str);
        }
    }

    private void add(LinkedList<Turmas> list, String turma, Params p) {
        for (Turmas t : list) {
            if (t.turma.equals(turma)) {
                t.addToList(p);
                return;
            }
        }
        list.add(new Turmas(turma, new LinkedList<>(Collections.singletonList(p))));
    }
}
