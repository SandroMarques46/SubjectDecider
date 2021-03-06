package test;

import ctrl.Node;
import ctrl.Turmas;
import org.junit.Test;

import java.util.LinkedList;

public class NodesTest {

    @Test
    public void timeTest1(){
        LinkedList<Node<Turmas>> original = new LinkedList<>();

        Node<Turmas> node1 = new Node<>(new Turmas("0.1",null));
        node1.next = new Node<>(new Turmas("0.2",null));

        Node<Turmas> node2 = new Node<>(new Turmas("1.1",null));
        node2.next = new Node<>(new Turmas("1.2",null));

        original.add(node1);
        original.add(node2);

        //makeNewNodeInstance();
    }

}
