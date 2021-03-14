package ctrl;

import java.util.LinkedList;

public class Classes {
    String turma;
    LinkedList<ClassInfo> info;

    public Classes(String turma, LinkedList<ClassInfo> info){
        this.turma = turma;
        this.info = info;
    }

    public void addToList(ClassInfo p){
        info.add(p);
    }

    @Override
    public String toString() {
        return turma;
    }

    private boolean hasMultiple() { //e.g "41D/42D/51N"
        return turma.contains("/");
    }

    public String[] getAllClasses() {
        return turma.split("/");
    }
}
