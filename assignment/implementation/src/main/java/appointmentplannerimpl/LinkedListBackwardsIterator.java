package appointmentplannerimpl;

import java.util.Iterator;

public class LinkedListBackwardsIterator<T> implements Iterator {
    private AllocationNode<T> head;
    private AllocationNode<T> node;

    public LinkedListBackwardsIterator(AllocationNode<T> head, AllocationNode<T> tail){
        this.head = head;
        this.node = tail;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }
}
