package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.TimeslotImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AllocationNodeTest {
    private DoublyLinkedList<TimeSlot> doublyLinkedList;

    private DoublyLinkedList<TimeSlot> list;
    private TimeslotImpl timeSlot = new TimeslotImpl(Instant.now(), Instant.now().plusSeconds(3600));
    private TimeslotImpl timeslot = new TimeslotImpl(Instant.now(), Instant.now().plusSeconds(7200));

    @BeforeEach
    private void setUp() {
        doublyLinkedList = new DoublyLinkedList<>();
    }

    @Disabled
    @Test
    public void testSetPrevious() {
        DoublyLinkedList.Node mockedNode = mock(DoublyLinkedList.Node.class);

        doublyLinkedList.getTail().setPrevious(mockedNode);

        assertThat(doublyLinkedList.getTail().getPrevious())
                .isEqualTo(mockedNode);
    }

    @Disabled
    @Test
    public void testSetNext() {
        DoublyLinkedList.Node mockedNode = mock(DoublyLinkedList.Node.class);

        doublyLinkedList.getHead().setNext(mockedNode);

        assertThat(doublyLinkedList.getHead().getNext())
                .isEqualTo(mockedNode);
    }

    @Test
    public void replaceItem() {
        var mockedTimeSlot = mock(TimeSlot.class);
        var replacedMockedTimeSlot = mock(TimeSlot.class);
        doublyLinkedList.addFront(mockedTimeSlot);

        var node = doublyLinkedList.getHead().getNext();
        node.setItem(replacedMockedTimeSlot);

        assertThat(node.getItem()).isEqualTo(replacedMockedTimeSlot);
    }
}
