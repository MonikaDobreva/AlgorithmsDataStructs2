package appointmentplannerimpl;

public class DoublyLinkedList<T> {
    AllocationNode<T> head = null;
    AllocationNode<T> tail = null;
    private int size;

    public void addNode(T item) {
        AllocationNode<T> newNode = new AllocationNode<>(item);
        if (this.head == null) {
            this.tail = newNode;
            this.head.previous = null;
            this.tail.next = null;
        } else {
            this.tail.next = newNode;
            newNode.previous = this.tail;
            this.tail = newNode;
            this.tail.next = null;
        }
        this.size++;
    }

    public int getSize() {
        return size;
    }
}
