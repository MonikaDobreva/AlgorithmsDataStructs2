package appointmentplannerimpl;

public class Node<T> {
    Node<T> previous;
    Node<T> next;
    T t;

    public Node(T t) {
        this.t = t;
    }
}
