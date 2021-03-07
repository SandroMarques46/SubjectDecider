package ctrl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Controller {
    private static final String OUTPUT_BYWEEKDAYS_FILE = "byWeekDays.txt";
    private static final String OUTPUT_BYSUBJECT_FILE = "bySubject.txt";
    private static final int MAX_SUBJECTS = 5;
    private HashMap<Integer, LinkedList<String>> priorities = new HashMap<>();
    private HashMap<String, LinkedList<Classes>> map = new HashMap<>();
    private static String HORARIOS_FILE = "horario.txt";
    private static String PRIO_FILE = "prioridade.txt";

    public static void main(String[] args) throws Exception {
        Controller ctrl = new Controller();
        ctrl.indexPriorities(PRIO_FILE);
        ctrl.indexFromFile(HORARIOS_FILE);
        LinkedList<String> prio = ctrl.getPriorities();
        ArrayList<Node<Classes>> list = ctrl.choose(prio);
        try {
            ctrl.removeIfConsecutiveNminutes(list, 360);
            Writer.writeToFileByWeekDays(list, OUTPUT_BYWEEKDAYS_FILE);
            Writer.writeToFileBySubject(prio, list, OUTPUT_BYSUBJECT_FILE);
        } catch (Exception e) {
            System.err.println("Error writing to file");
        }
    }

    public void insertDaysOnMap(HashMap<String, LinkedList<PairTime>> hashMap, Node<Classes> t) {
        if (!hashMap.isEmpty()) hashMap.clear();
        while (t.previous != null) t = t.previous;
        while (t != null) {
            for (ClassInfo p : t.value.info) {
                if (hashMap.get(p.week_day) == null) {
                    hashMap.put(p.week_day, new LinkedList<>(Collections.singleton(p.time)));
                } else {
                    hashMap.get(p.week_day).add(p.time);
                }
            }
            t = t.next;
        }
    }

    /**
     * Removes from "list" all the Node<Classes> that have consecutive "n" minutes on any week day.
     */
    private void removeIfConsecutiveNminutes(ArrayList<Node<Classes>> list, int n) {
        boolean break_cycle = false;
        LinkedList<Node<Classes>> nodes_to_remove = new LinkedList<>();
        LinkedList<String> days_list = Days.getOrderList();
        int count;
        for (Node<Classes> t : list) {
            HashMap<String, LinkedList<PairTime>> hashMap = new HashMap<>();
            Node<Classes> head = t;
            while (t.previous != null) t = t.previous;
            while (t != null) {
                insertDaysOnMap(hashMap, t);
                for (String str : days_list) {
                    LinkedList<PairTime> pairTime = hashMap.get(str);
                    if (pairTime != null) {
                        PairTime.order(pairTime);
                        count = 0;
                        for (int i = 1; i < pairTime.size(); i++) {
                            PairTime curr = pairTime.get(i);
                            PairTime prev = pairTime.get(i - 1);
                            if (curr.beg.equals(prev.end)) { //consecutive classes
                                count += curr.getMinutesLength() + prev.getMinutesLength();
                            } else {
                                count = 0;
                            }
                            if (count >= n) {
                                nodes_to_remove.add(head);
                                break_cycle = true;
                                break;
                            }
                        }
                    }
                    if (break_cycle) {
                        break;
                    }
                }
                if (break_cycle) {
                    break_cycle = false;
                    break;
                }
                t = t.next;
            }
        }
        for (Node<Classes> node : nodes_to_remove) {
            list.remove(node);
        }
        if (nodes_to_remove.size() == list.size()) {
            System.err.println("The condition \"remove classes with consecutive " + n + "\" removed everything");
        }
        System.out.println("Classes with over " + n + " minutes removed (" + nodes_to_remove.size() + " elements)");
        System.out.println("Total is now " + list.size());
    }

    private LinkedList<String> getPriorities() {
        LinkedList<String> prio = new LinkedList<>();
        if (!priorities.isEmpty()) {
            for (Integer integer : priorities.keySet()) {
                LinkedList<String> name = priorities.get(integer);
                if (name != null) {
                    prio.addAll(name);
                }
            }
        } else { //if priorities are empty, makes a random selection of number : MAX_SUBJECTS
            int num = MAX_SUBJECTS;
            for (String subj : map.keySet()) {
                if (num > 0) prio.add(subj);
                else break;
                num--;
            }
        }
        return prio;
    }

    private void indexPriorities(String prio_file) {
        try (BufferedReader fr = new BufferedReader(new FileReader(prio_file))) {
            String str;
            while ((str = fr.readLine()) != null) {
                String[] parts = str.split(Syntax.SEP);
                try {
                    if (priorities.get(Integer.parseInt(parts[1])) == null) {
                        priorities.put(Integer.valueOf(parts[1]), new LinkedList<>(Collections.singleton(parts[0])));
                    } else {
                        LinkedList<String> list_cadeiras = priorities.get(Integer.valueOf(parts[1]));
                        if (!list_cadeiras.contains(parts[0])) {
                            list_cadeiras.add(parts[0]);
                        } else {
                            throw new DuplicateNameException("Subject \"" + parts[0] + "\" is duplicated on file " + prio_file);
                        }
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

    private void indexFromFile(String horario_file) throws Exception {
        String str;
        try (BufferedReader fr = new BufferedReader(new FileReader(horario_file))) {
            int lineNum = 1;
            while ((str = fr.readLine()) != null) {
                str = str.trim();
                if (!str.contains(Syntax.LINE_IGNORE_SEP) && str.indexOf(Syntax.LINE_IGNORE_SEP) != 0) {
                    //NAME, TURMA, BEG. HOUR, END HOUR, WEEK DAY, TIPO, PROF
                    String[] parts = str.split(Syntax.SEP);
                    try {
                        if (parts.length != 7) {
                            throw new IllegalArgumentException();
                        }
                        ClassInfo p = new ClassInfo(parts[2], parts[3], parts[4], parts[5], parts[6]);
                        String[] turmasarr = parts[1].split(Syntax.TURMA_SEP);
                        for (String turma : turmasarr) {
                            if (map.get(parts[0]) == null) {
                                map.put(parts[0], new LinkedList<>(Collections.singletonList(new Classes(turma, new LinkedList<>(Collections.singletonList(p))))));
                            } else {
                                LinkedList<Classes> list = map.get(parts[0]);
                                add(list, turma, p);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Line " + lineNum + " is not valid : " + str);
                    }
                }
                lineNum++;
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("FILE NOT FOUND");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    private ArrayList<Node<Classes>> choose(LinkedList<String> prio) {
        ArrayList<Node<Classes>> list = findNcombsfromlist(prio);
        removeNonValidCombs(list);
        return list;
    }

    /**
     * Checks if there are subjects overplaced.
     */
    private void removeNonValidCombs(ArrayList<Node<Classes>> list) {
        boolean break_cycle = false;
        LinkedList<Node<Classes>> nodes_to_remove = new LinkedList<>();
        for (Node<Classes> t : list) {
            HashMap<String, LinkedList<PairTime>> hashMap = new HashMap<>();
            Node<Classes> head = t;
            while (t.previous != null) t = t.previous;
            while (t != null) {
                for (ClassInfo p : t.value.info) {
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
        for (Node<Classes> node : nodes_to_remove) {
            list.remove(node);
        }
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

    /**
     * From the ordered list of subjects finds the total number of valid "arranjements" (num_comb)
     * Returns a List of Lists of"Pair" of size "num_comb"
     */
    private ArrayList<Node<Classes>> findNcombsfromlist(LinkedList<String> subj_list) {
        ArrayList<Node<Classes>> list = new ArrayList<>();
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
                LinkedList<Classes> turmas = map.get(subj_list.get(i));
                int sep = list.size() / turmas.size();
                while (aux < list.size()) {
                    list.get(aux).addNode(new Node<>(turmas.get(aux2)));
                    list.set(aux, list.get(aux).next);
                    aux++;
                    if (aux == sep) {
                        aux2++;
                        sep += sep;
                    }
                }
            }
        }
        return list;
    }

    private void createListWithNnodes(ArrayList<Node<Classes>> list, String subject) {
        for (Classes t : map.get(subject)) {
            list.add(new Node<>(t));
        }
    }

    private void multiplySizeByN(ArrayList<Node<Classes>> list, int newSize) {
        if (newSize == 1) return;
        LinkedList<Node<Classes>> original = new LinkedList<>(list);
        while (list.size() < newSize) {
            list.addAll(makeNewNodeInstance(original));
        }
    }

    public static ArrayList<Node<Classes>> makeNewNodeInstance(LinkedList<Node<Classes>> original) {
        ArrayList<Node<Classes>> toReturn = new ArrayList<>();
        for (Node<Classes> node : original) {
            while (node.previous != null) node = node.previous;
            Node<Classes> newNode = new Node<>();
            newNode.value = node.value;
            while (node.next != null) {
                newNode.addVal(node.next.value);
                newNode = newNode.next;
                node = node.next;
            }
            toReturn.add(newNode);
        }
        for (int i = 0; i < toReturn.size(); i++) {
            Node<Classes> node = toReturn.get(i);
            while (node.next != null) node = node.next;
            toReturn.set(i, node);
        }
        return toReturn;
    }

    /**
     * Receives a "list" of Classes and adds the "info" associated with the "classId"
     * If that "classId" already exists, then simply adds info (addToList(info))
     * If it doesn't exist, adds to the list a new entry with that "classId" and "info" associated with.
     */
    private void add(LinkedList<Classes> list, String classId, ClassInfo info) {
        for (Classes t : list) {
            if (t.turma.equals(classId)) {
                t.addToList(info);
                return;
            }
        }
        list.add(new Classes(classId, new LinkedList<>(Collections.singletonList(info))));
    }
}
