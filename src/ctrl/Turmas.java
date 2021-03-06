package ctrl;

import java.util.LinkedList;

public class Turmas {
    String turma;
    LinkedList<Params> info;

    public Turmas(String turma, LinkedList<Params> info){
        this.turma = turma;
        this.info = info;
    }

    public void addToList(Params p){
        info.add(p);
    }
}
