package appointmentplannerimpl;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DoublyLinkedList<T> implements Iterable<T>{
    AllocationNode<T> head = null;
    AllocationNode<T> tail = null;
    private int size;

    public DoublyLinkedList(){
        //initialize head and tail
        head = new AllocationNode<T>(null);
        tail = new AllocationNode<T>(null);
        //set head and tail to be the same
        head.next = tail;
        tail.previous = head;
        //set head and tail to be the only thing in the list
        head.previous = null;
        tail.next = null;
        //size of the list is 0 at this moment
        size = 0;
    }

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

    @Override
    public Iterator<T> iterator() {
        return null;
    }
}
