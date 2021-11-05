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

    public void toFront(T t) {
        addToBack(t, this.head);
    }

    public void addInFront(T t, T nextT) {
        var nextAllocationNode = lookForNode(nextT);
        addInFront(t, nextAllocationNode);
    }

    public void addInFront(T t, AllocationNode nextNode) {
        //check if the input is not null
        if (nextNode != null && t != null) {
            //initialize a new node
            AllocationNode anotherNode = new AllocationNode(t);
            //the given node is set to be the next of the node created above
            anotherNode.setNext(nextNode);
            anotherNode.setPrevious(nextNode.previous);
            anotherNode.previous.setNext(anotherNode);
            nextNode.setPrevious(anotherNode);

            this.size++;
        }
    }

    public void addToBack(T t, T frontT) {
        var frontNode = lookForNode(frontT);
        addToBack(t, frontNode);
    }

    public void addToBack(T t, AllocationNode<T> backNode) {
        addInFront(t, backNode.next);
    }

    public void toBack(T t) {
        addInFront(t, this.tail);
    }

    public AllocationNode<T> mergeNodesNext(AllocationNode node, AllocationNode nextNode, T t) {
        nextNode.setPrevious(null);
        node.setNext(nextNode.getNext());
        node.getNext().setPrevious(node);
        nextNode.setNext(null);
        node.setT(t);
        return node;
    }

    public AllocationNode<T> mergeNodesPrevious(AllocationNode node, AllocationNode previousNode, T t) {
        previousNode.setNext(null);
        node.setPrevious(previousNode.getPrevious());
        node.getPrevious().setNext(node);
        previousNode.setPrevious(null);
        node.setT(t);
        return previousNode;
    }

    public AllocationNode<T> lookForTNode(T t) {
        var node = this.head;
        for (int i = 0; i < this.size; i++) {
            node = node.next;
            try {
                if (node.getT().equals(t)) {
                    return node;
                }
            } catch(NullPointerException np) {
                return null;
            }
        }
        return null;
    }

    public List<T> lookForInstancesOf(Class t) {
        var tList = new ArrayList();
        var node = this.head;
        for(int i = 0; i < size; i++) {
            node = node.next;
            if (node.getT().getClass().equals(t)) {
                tList.add(node.getT());
            }
        }
        return tList;
    }
}
