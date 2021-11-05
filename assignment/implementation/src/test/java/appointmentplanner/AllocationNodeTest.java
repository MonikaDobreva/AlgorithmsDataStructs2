package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.DoublyLinkedList.*;
import appointmentplannerimpl.TimeslotImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AllocationNodeTest {
    private DoublyLinkedList<TimeSlot> list;
    private TimeslotImpl timeSlot = new TimeslotImpl(Instant.now(), Instant.now().plusSeconds(3600));
    private TimeslotImpl timeslot = new TimeslotImpl(Instant.now(), Instant.now().plusSeconds(7200));

    @BeforeEach
    private void setUp() {
        this.list = new DoublyLinkedList<TimeSlot>();
    }

    @Test
    public void setGetPrevTest() {
        var node = new AllocationNode(this.timeSlot);
        this.list.getTail().setPrevious(node);

        assertThat(this.list.getTail().getPrevious())
                .isEqualTo(node);
    }

    @Test
    public void setGetNextTest() {
        DoublyLinkedList.AllocationNode node = new DoublyLinkedList.AllocationNode(this.timeSlot);
        this.list.getHead().setNext(node);

        assertThat(this.list.getHead().getNext())
                .isEqualTo(node);
    }

    @Test
    public void setTmethodTest() {
        this.list.toFront(this.timeSlot);
        var node = this.list.getHead().getNext();
        node.setT(this.timeslot);

        assertThat(node.getT())
                .isEqualTo(this.timeslot);
    }

    @Test
    public void replaceTTest() {
        var timeSlot = mock(TimeSlot.class);
        var replaceTimeSlot = mock(TimeSlot.class);
        this.list.toFront(timeSlot);

        var node = this.list.getHead().getNext();
        node.setT(replaceTimeSlot);

        assertThat(node.getT())
                .isEqualTo(replaceTimeSlot);
    }
}


