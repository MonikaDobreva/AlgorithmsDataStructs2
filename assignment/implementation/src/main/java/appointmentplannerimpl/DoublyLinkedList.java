package appointmentplannerimpl;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DoublyLinkedList<T> implements Iterable<T> {
    private Node<T> head, tail;
    private int size;

    public DoublyLinkedList() {
        head = new Node<T>(null);
        tail = new Node<T>(null);

        head.next = tail;
        tail.previous = head;

        head.previous = null;
        tail.next = null;

        size = 0;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void addFront(T item) {
        addAfter(item, head);
    }

    public void addEnd(T item) {
        addBefore(item, tail);
    }

    public void addBefore(T item, T nextItem) {
        var nextNode = searchItemNode(nextItem);
        addBefore(item, nextNode);
    }

    public void addBefore(T item, Node nextNode) {
        if (nextNode != null && item != null) {
            var newNode = new Node(item);

            newNode.setNext(nextNode);
            newNode.setPrevious(nextNode.previous);
            newNode.previous.setNext(newNode);
            nextNode.setPrevious(newNode);

            size++;
        }
    }

    public void addAfter(T item, T beforeItem) {
        var beforeNode = searchItemNode(beforeItem);
        addAfter(item, beforeNode);
    }

    public void addAfter(T item, Node<T> beforeNode) {
        addBefore(item, beforeNode.next);
    }

    public Node<T> searchItemNode(T item) {
        var currentNode = head;
        for (int i = 0; i < size; i++) {
            currentNode = currentNode.next;
            try {
                if (currentNode.getItem().equals(item)) {
                    return currentNode;
                }
            } catch (NullPointerException npe) {
                return null;
            }
        }
        return null;
    }

    public List<T> searchExactInstancesOf(Class item) {
        var itemList = new ArrayList();
        var currentNode = head;
        for (int i = 0; i < size; i++) {
            currentNode = currentNode.next;
            if (currentNode.getItem().getClass().equals(item)) {
                itemList.add(currentNode.getItem());
            }
        }
        return itemList;
    }

    public void remove(T item) {
        var currentNode = searchItemNode(item);
        if (currentNode != null) {
            currentNode.previous.setNext(currentNode.next);
            currentNode.next.setPrevious(currentNode.previous);

            size--;
        }
    }

    public Node<T> mergeNodesPrevious(Node node, Node previousNode, T newItem) {
        previousNode.setNext(null);
        node.setPrevious(previousNode.getPrevious());
        node.getPrevious().setNext(node);
        previousNode.setPrevious(null);
        node.setItem(newItem);
        return previousNode;
    }

    public Node<T> mergeNodesNext(Node node, Node nextNode, T newItem) {
        nextNode.setPrevious(null);
        node.setNext(nextNode.getNext());
        node.getNext().setPrevious(node);
        nextNode.setNext(null);
        node.setItem(newItem);
        return node;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator<T>(head, tail);
    }

    public Iterator<T> reverseIterator() {
        return new LinkedListBackwardsIterator<T>(head, tail);
    }

    private Stream<T> stream(Iterator iterator) {
        Spliterator<Node<T>> spliterator = Spliterators.spliteratorUnknownSize(
                iterator, Spliterator.ORDERED
        );
        return StreamSupport.stream(spliterator, false)
                .map(node -> node.getItem());
    }

    public Stream<T> stream() {
        return this.stream(iterator());
    }

    public Stream<T> reverseStream() {
        return this.stream(reverseIterator());
    }

    public class Node<T> {
        private Node<T> previous;
        private Node<T> next;
        private T item;

        public Node(T item) {
            this.item = item;
        }

        public T getItem() {
            return item;
        }

        public Node<T> getPrevious() {
            return previous;
        }

        public void setPrevious(Node<T> previous) {
            this.previous = previous;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public void setItem(T item) {
            this.item = item;
        }
    }
}
