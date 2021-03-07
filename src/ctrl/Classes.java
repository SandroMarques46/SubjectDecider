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
}
