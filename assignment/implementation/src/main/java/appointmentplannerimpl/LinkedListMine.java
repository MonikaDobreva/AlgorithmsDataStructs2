package appointmentplannerimpl;

public class LinkedListMine<T> {
    Node<T> head = null;
    Node<T> tail = null;
    private int size;

    public void addNode(T t){
        Node<T> node = new Node<>(t);

        if (this.head == null){
            this.head = node;
            this.tail = this.head;
            this.head.previous = null;
            this.tail.next = null;
        } else {
            this.tail.next = node;
            node.previous = this.tail;
            this.tail = node;
            this.tail.next = null;
        }

        this.size++;
    }

    public int getSize(){
        return this.size;
    }
}
