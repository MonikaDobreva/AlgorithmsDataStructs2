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
        return false;
    }

    @Override
    public Object next() {
        return null;
    }
}
