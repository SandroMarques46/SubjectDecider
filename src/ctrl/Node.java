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
        if (isEmpty()) {
            this.value = value;
        } else {
            this.next = new Node<>(value);
            this.next.previous = this;
        }
    }

    private boolean isEmpty() {
        return this.previous == null && this.next == null && this.value == null;
    }

    public boolean isFirst() {
        return this.previous == null && this.next == null && this.value != null;
    }
    //this = destination
    public void copy(Node<E> node) {
        Node<E> thisNode = this;
        while (node.previous != null) node = node.previous;
        thisNode.value = node.value;
        while (node.next != null) {
            thisNode.addVal(node.next.value);
            thisNode = thisNode.next;
            node = node.next;
        }
    }
}