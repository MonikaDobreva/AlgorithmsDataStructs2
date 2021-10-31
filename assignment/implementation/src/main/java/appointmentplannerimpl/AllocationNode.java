package appointmentplannerimpl;

public class AllocationNode<T> {
    AllocationNode<T> previous;
    AllocationNode<T> next;
    T t;

    public AllocationNode(T t) {
        this.t = t;
    }

    //setting the next item
    public void setNext(AllocationNode<T> next){
        this.next = next;
    }
    //setting the prevoius item
    public void setPrevious(AllocationNode<T> prev){
        this.previous = prev;
    }

    public AllocationNode<T> getNext(){
        return this.next;
    }

    public AllocationNode<T> getPrevious(){
        return this.previous;
    }
}
