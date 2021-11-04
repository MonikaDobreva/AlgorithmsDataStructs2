package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.DoublyLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkedListTest {
    private DoublyLinkedList<TimeSlot> list;

    @BeforeEach
    private void setUp() {
        list = new DoublyLinkedList<TimeSlot>();
    }

    @Test
    public void getSizeTest() {
        assertThat(this.list.getSize())
            .isEqualTo(0);
    }

    @Test
    public void getSizeTest2() {
        this.list.toFront(mock(TimeSlot.class));
        assertThat(this.list.getSize())
                .isNotEqualTo(0);
    }
}
