package appointmentplannerimpl;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.*;

public class DoublyLinkedList<T> implements Iterable<T>{
    AllocationNode<T> head = null;
    AllocationNode<T> tail = null;
    private int size;
    private Stream<T> stream(Iterator iterator) {
        Spliterator<AllocationNode<T>> spliterator = Spliterators.spliteratorUnknownSize(
                iterator, Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false)
                .map(node -> node.getT());
    }

    public DoublyLinkedList(){
        //initialize head and tail
        this.head = new AllocationNode<T>(null);
        this.tail = new AllocationNode<T>(null);
        //set head and tail to be the same
        this.head.next = this.tail;
        this.tail.previous = this.head;
        //set head and tail to be the only thing in the list
        this.head.previous = null;
        tail.next = null;
        //size of the list is 0 at this moment
        this.size = 0;
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

    public AllocationNode<T> getHead() {
        return this.head;
    }

    public AllocationNode<T> getTail(){
        return this.tail;
    }

    public AllocationNode<T> lookForNode(T t){
        //create a node to equal the head
        AllocationNode<T> node = this.head;
        //go through the list
        for(int i = 0; i < this.size; i++){
            //the node from before will now go through the list
            //it goes through each next node after the head
            node = node.getNext();
            //if the item that we set equals the item in
            //one of the nodes fro the list it is returned
            if(node.getT().equals(t)){
                return node;
            }
            //if the item is not found null is returned
            return null;
        }
        return null;
    }

    public void removeNode(T t){
        //create a node to search for
        AllocationNode<T> node = lookForNode(t);
        //check if the given (searched for) node is not null
        //if it's null we cannot remove it
        if(node !=null){
            //set the previous of the node to be its next
            node.getPrevious().setNext(node.getNext());
            //set the next of the node to be its previous
            node.getNext().setPrevious(node.getPrevious());
            //the size of the list decreases,
            //because an item has been removed
            this.size--;
        }
    }

    public Stream<T> stream() {
        return this.stream(iterator());
    }

    public Stream<T> streamBackwards() {
        return this.stream(iteratorBackwards());
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator<T>(this.head,this.tail);
    }

    public Iterator<T> iteratorBackwards(){
        return new LinkedListBackwardsIterator<T>(this.head, this.tail);
    }
}
