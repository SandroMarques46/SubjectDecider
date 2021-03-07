package test;

import ctrl.Node;
import ctrl.Classes;
import org.junit.Test;

import java.util.LinkedList;

public class NodesTest {

    @Test
    public void timeTest1(){
        LinkedList<Node<Classes>> original = new LinkedList<>();

        Node<Classes> node1 = new Node<>(new Classes("0.1",null));
        node1.next = new Node<>(new Classes("0.2",null));

        Node<Classes> node2 = new Node<>(new Classes("1.1",null));
        node2.next = new Node<>(new Classes("1.2",null));

        original.add(node1);
        original.add(node2);

        //makeNewNodeInstance();
    }

}
