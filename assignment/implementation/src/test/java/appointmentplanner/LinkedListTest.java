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
    public void isEmptyTrue() {
        assertThat(this.list.getSize())
            .isEqualTo(0);
    }

    @Test
    public void isEmptyFalse() {
        this.list.toFront(mock(TimeSlot.class));
        assertThat(this.list.getSize())
                .isNotEqualTo(0);
    }
}
