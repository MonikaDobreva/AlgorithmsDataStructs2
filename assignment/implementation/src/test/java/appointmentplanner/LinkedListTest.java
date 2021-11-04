package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.DoublyLinkedList;
import org.junit.jupiter.api.BeforeEach;

public class LinkedListTest {
    private DoublyLinkedList<TimeSlot> list;

    @BeforeEach
    private void setUp() {
        list = new DoublyLinkedList<TimeSlot>();
    }
}
