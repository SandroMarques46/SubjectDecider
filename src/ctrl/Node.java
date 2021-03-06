package ctrl;

import java.util.Comparator;

public class Node<E> {
    public E value;
    public Node<E> next;
    public Node<E> previous;

    public Node() {
        this.value = null;
    }

    public Node(E value) {
        this.value = value;
    }

    public void add(E value, Comparator<E> cmp) {
        Node<E> newNode = new Node<E>(value);
        if (this.isEmpty()) { // se a lista tiver vazia
            this.next = newNode;
            this.previous = newNode;
            newNode.next = this;
            newNode.previous = this;
        } else if (this.isFirst()) {
            Node<E> nullNode = new Node<E>(null); // criacao de sentinela
            this.next = nullNode;
            this.previous = nullNode;
            nullNode.next = this;
            nullNode.previous = this;
            this.add(value, cmp); //recursividade cmp.compare(ite.next.value,value)>0
        } else if (cmp.compare(this.value, value) > 0) {
            Node<E> ite = this;
            while (ite.previous.value != null && cmp.compare(ite.previous.value, value) > 0) {
                ite = ite.previous;
            }
            if (ite.previous.value != null && cmp.compare(this.previous.value, value) == 0)
                return; // se encontrar repetidos
            Node<E> ite_previous = ite.previous;
            ite_previous.next = newNode;
            ite.previous = newNode;
            newNode.next = ite;
            newNode.previous = ite_previous;
        } else {
            Node<E> ite = this;
            while (ite.next.value != null && cmp.compare(ite.next.value, value) < 0) {
                ite = ite.next;
            }
            if (ite.next.value != null && cmp.compare(ite.next.value, value) == 0) return; // se encontrar repetidos
            Node<E> ite_next = ite.next;
            ite.next = newNode;
            ite_next.previous = newNode;
            newNode.next = ite_next;
            newNode.previous = ite;
        }
    }

    public void add(Node<E> newNode, Comparator<E> cmp) {
        E value = newNode.value;
        if (this.isEmpty()) { // se a lista tiver vazia
            this.next = newNode;
            this.previous = newNode;
            newNode.next = this;
            newNode.previous = this;
        } else if (this.isFirst()) { // se sรณ tiver 1 elemento nenhums :
            if (this.value != value) { // e se n for repetido
                Node<E> nullNode = new Node<E>(null); // criacao de sentinela
                this.next = nullNode;
                this.previous = nullNode;
                nullNode.next = this;
                nullNode.previous = this;
                this.add(newNode, cmp); //recursividade cmp.compare(ite.next.value,value)>0
            }
        } else if (cmp.compare(this.value, value) > 0) {
            Node<E> ite = this;
            while (ite.previous.value != null && cmp.compare(ite.previous.value, value) > 0) {
                ite = ite.previous;
            }
            if (ite.previous.value != null && cmp.compare(this.previous.value, value) == 0)
                return; // se encontrar repetidos
            Node<E> ite_previous = ite.previous;
            ite_previous.next = newNode;
            ite.previous = newNode;
            newNode.next = ite;
            newNode.previous = ite_previous;
        } else {
            Node<E> ite = this;
            while (ite.next.value != null && cmp.compare(ite.next.value, value) < 0) {
                ite = ite.next;
            }
            if (ite.next.value != null && cmp.compare(ite.next.value, value) == 0) return; // se encontrar repetidos
            Node<E> ite_next = ite.next;
            ite.next = newNode;
            ite_next.previous = newNode;
            newNode.next = ite_next;
            newNode.previous = ite;
        }
    }

    public boolean isEmpty() {
        return this.value == null && (this.next != null || this.next.value == null);
    }

    public boolean isFirst() {
        return this.previous == null;
    }

    public void remove() {
        Node previousNode = this.previous;
        Node nextNode = this.next;
        previousNode.next = nextNode;
        nextNode.previous = previousNode;
    }

    public String toString() {
        if (this.isEmpty()) return "";
        Node it = this;
        while (it.previous != null && it.previous.value != null) {
            it = it.previous;
        }
        StringBuilder sb = new StringBuilder();
        while (it != null && it.value != null) {
            sb.append("Value: " + it.value + "; ");
            it = it.next;
        }
        return sb.toString();
    }


    public void print() {
        if (this.isEmpty()) return;
        Node it = this;
        while (it.previous != null && it.previous.value != null) {
            it = it.previous;
        }
        while (it != null && it.value != null) {
            System.out.println("Value: " + it.value);
            it = it.next;
        }
    }

    public void add(Node<E> eNode) {
        this.next = eNode;
        eNode.previous = this;
    }

    public void addVal(E value) {
        this.next = new Node<>(value);
        this.next.previous = this;
    }
}