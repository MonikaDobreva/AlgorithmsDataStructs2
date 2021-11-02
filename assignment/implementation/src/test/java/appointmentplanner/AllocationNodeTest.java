package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.AllocationNode;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.TimeslotImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class AllocationNodeTest {
    private DoublyLinkedList<TimeSlot> list;
    private TimeslotImpl timeSlot = new TimeslotImpl(Instant.now(), Instant.now().plusSeconds(3600));

    @BeforeEach
    private void setUp() {
        this.list = new DoublyLinkedList<TimeSlot>();
    }

    @Test
    public void setGetPrevTest() {
        AllocationNode node = new AllocationNode(this.timeSlot);
        this.list.getTail().setPrevious(node);

        assertThat(this.list.getTail().getPrevious())
                .isEqualTo(node);
    }

    @Test
    public void setGetNextTest() {
        AllocationNode node = new AllocationNode(this.timeSlot);
        this.list.getHead().setNext(node);

        assertThat(this.list.getHead().getNext())
                .isEqualTo(node);
    }
}


