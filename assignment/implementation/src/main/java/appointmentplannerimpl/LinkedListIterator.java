package appointmentplannerimpl;

import java.util.Iterator;

public class LinkedListIterator<T> implements Iterator {
    private AllocationNode<T> node;
    private AllocationNode<T> tail;

    public LinkedListIterator(AllocationNode<T> head, AllocationNode<T> tail){
        this.node = head;
        this.tail = tail;
    }

    @Override
    public boolean hasNext() {
        boolean next = this.node.getNext() != this.tail;
        return (next);
    }

    @Override
    public Object next() {
        this.node = this.node.getNext();
        return this.node;
    }
}
