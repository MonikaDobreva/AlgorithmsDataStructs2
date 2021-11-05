package appointmentplannerimpl;

import java.util.Iterator;

public class LinkedListBackwardsIterator<T> implements Iterator {
    private DoublyLinkedList.Node head;
    private DoublyLinkedList.Node node;

    public LinkedListBackwardsIterator(DoublyLinkedList.Node head, DoublyLinkedList.Node tail){
        this.head = head;
        this.node = tail;
    }

    @Override
    public boolean hasNext() {
        boolean prev = this.node.getPrevious() != this.head;
        return prev;
    }

    @Override
    public Object next() {
        this.node = this.node.getPrevious();
        return this.node;
    }
}
