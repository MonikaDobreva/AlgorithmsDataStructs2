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
        boolean prev = this.node.getPrevious() != this.head;
        return prev;
    }

    @Override
    public Object next() {
        this.node = this.node.getPrevious();
        return this.node;
    }
}
