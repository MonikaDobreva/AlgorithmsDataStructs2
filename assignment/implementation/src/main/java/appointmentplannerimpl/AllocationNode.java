package appointmentplannerimpl;

public class AllocationNode<T> {
    AllocationNode<T> previous;
    AllocationNode<T> next;
    T t;

    public AllocationNode(T t) {
        this.t = t;
    }
}
