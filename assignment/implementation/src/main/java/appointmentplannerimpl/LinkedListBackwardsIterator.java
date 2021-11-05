package appointmentplannerimpl;

import java.util.Iterator;

public class LinkedListBackwardsIterator<T> implements Iterator {
    private DoublyLinkedList.AllocationNode<T> head;
    private DoublyLinkedList.AllocationNode<T> node;

    public LinkedListBackwardsIterator(DoublyLinkedList.AllocationNode<T> head, DoublyLinkedList.AllocationNode<T> tail){
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
