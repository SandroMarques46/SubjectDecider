package ctrl;

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

    public void addNode(Node<E> node) {
        this.next = node;
        node.previous = this;
    }

    public void addVal(E value) {
        this.next = new Node<>(value);
        this.next.previous = this;
    }
}