package appointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.AppointmentImpl;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.LinkedListBackwardsIterator;
import appointmentplannerimpl.LinkedListIterator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class LinkedListTest {
    private DoublyLinkedList<TimeSlot> doublyLinkedList;

    @BeforeEach
    private void setUp() {
        doublyLinkedList = new DoublyLinkedList<TimeSlot>();
    }

    @Test
    public void testDummyNodesCreated() {
        DoublyLinkedList<TimeSlot>.Node<TimeSlot> head = doublyLinkedList.getHead();
        DoublyLinkedList<TimeSlot>.Node<TimeSlot> tail = doublyLinkedList.getTail();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(head.getItem()).isEqualTo(null);
            softly.assertThat(tail.getItem()).isEqualTo(null);

            softly.assertThat(head.getNext()).isSameAs(tail);
            softly.assertThat(tail.getPrevious()).isSameAs(head);

            softly.assertThat(head.getPrevious()).isEqualTo(null);
            softly.assertThat(tail.getNext()).isEqualTo(null);
        });
    }

    @Test
    public void testAddNote() {
        TimeSlot mockedTimeSlot = mock(TimeSlot.class);

        //doublyLinkedList.addNote(mockedTimeSlot);
    }

    @Test
    public void addFront() {
        var timeslot = mock(TimeSlot.class);
        var secondTimeslot = mock(TimeSlot.class);
        doublyLinkedList.addFront(timeslot);
        doublyLinkedList.addFront(secondTimeslot);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(doublyLinkedList.getHead().getNext().getItem()).isEqualTo(secondTimeslot);
            softly.assertThat(doublyLinkedList.getTail().getPrevious().getItem()).isEqualTo(timeslot);
            softly.assertThat(doublyLinkedList.getSize()).isEqualTo(2);
        });
    }

    @Test
    public void addEnd() {
        var timeslot = mock(TimeSlot.class);
        var secondTimeslot = mock(TimeSlot.class);
        doublyLinkedList.addEnd(timeslot);
        doublyLinkedList.addEnd(secondTimeslot);

        SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(doublyLinkedList.getHead().getNext().getItem()).isEqualTo(timeslot);
                    softly.assertThat(doublyLinkedList.getTail().getPrevious().getItem()).isEqualTo(secondTimeslot);
                    softly.assertThat(doublyLinkedList.getSize()).isEqualTo(2);
                }
        );
    }

    @Test
    public void remove() {
        var timeslot = mock(TimeSlot.class);

        doublyLinkedList.addFront(timeslot);
        doublyLinkedList.remove(timeslot);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(doublyLinkedList.getHead().getNext()).isEqualTo(doublyLinkedList.getTail());
            softly.assertThat(doublyLinkedList.getTail().getPrevious()).isEqualTo(doublyLinkedList.getHead());
            softly.assertThat(doublyLinkedList.getSize()).isEqualTo(0);
        });
    }

    @Test
    public void removeNull() {
        doublyLinkedList.addFront(mock(TimeSlot.class));
        doublyLinkedList.remove(mock(TimeSlot.class));

        assertThat(doublyLinkedList.getSize()).isEqualTo(1);
    }

    @Test
    public void addBefore() {
        var timeslot = mock(TimeSlot.class);
        var secondTimeslot = mock(TimeSlot.class);
        var setBeforeTimeSlot = mock(TimeSlot.class);

        doublyLinkedList.addFront(timeslot);
        doublyLinkedList.addFront(secondTimeslot);
        doublyLinkedList.addBefore(setBeforeTimeSlot, timeslot);

        var setBeforeNode = doublyLinkedList.searchItemNode(setBeforeTimeSlot);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(setBeforeNode.getItem()).isEqualTo(setBeforeTimeSlot);
            softly.assertThat(setBeforeNode.getPrevious().getItem()).isEqualTo(secondTimeslot);
            softly.assertThat(setBeforeNode.getNext().getItem()).isEqualTo(timeslot);
            softly.assertThat(setBeforeNode.getNext().getPrevious()).isEqualTo(setBeforeNode);
            softly.assertThat(setBeforeNode.getPrevious().getNext()).isEqualTo(setBeforeNode);
            softly.assertThat(doublyLinkedList.getSize()).isEqualTo(3);
        });
    }

    @Test
    public void addBeforeNullItem() {
        var timeSlot = mock(TimeSlot.class);
        doublyLinkedList.addFront(timeSlot);
        doublyLinkedList.addBefore(null, timeSlot);

        assertThat(doublyLinkedList.getSize()).isEqualTo(1);
    }

    @Test
    public void addBeforeBeforeItemNull() {
        var timeSlot = mock(TimeSlot.class);
        var setBeforeTimeSlot = mock(TimeSlot.class);
        doublyLinkedList.addBefore(setBeforeTimeSlot, timeSlot);

        assertThat(doublyLinkedList.getSize()).isEqualTo(0);
    }

    @Test
    public void addAfter() {
        var timeslot = mock(TimeSlot.class);
        var secondTimeslot = mock(TimeSlot.class);
        var setAfterTimeSlot = mock(TimeSlot.class);

        doublyLinkedList.addFront(timeslot);
        doublyLinkedList.addFront(secondTimeslot);
        doublyLinkedList.addAfter(setAfterTimeSlot, secondTimeslot);

        var setAfterNode = doublyLinkedList.searchItemNode(setAfterTimeSlot);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(setAfterNode.getItem()).isEqualTo(setAfterTimeSlot);
            softly.assertThat(setAfterNode.getPrevious().getItem()).isEqualTo(secondTimeslot);
            softly.assertThat(setAfterNode.getNext().getItem()).isEqualTo(timeslot);
            softly.assertThat(setAfterNode.getNext().getPrevious()).isEqualTo(setAfterNode);
            softly.assertThat(setAfterNode.getPrevious().getNext()).isEqualTo(setAfterNode);
            softly.assertThat(doublyLinkedList.getSize()).isEqualTo(3);
        });
    }

    @Test
    public void searchItemNode() {
        var timeslot = mock(TimeSlot.class);

        doublyLinkedList.addFront(timeslot);

        assertThat(doublyLinkedList.searchItemNode(timeslot).getItem()).isEqualTo(timeslot);

    }

    @Test
    public void searchNullItemNode() {
        var timeslot = mock(TimeSlot.class);

        assertThat(doublyLinkedList.searchItemNode(timeslot)).isNull();
    }

    @Test
    public void isEmptyTrue() {
        assertThat(doublyLinkedList.isEmpty()).isTrue();
    }

    @Test
    public void isEmptyFalse() {
        doublyLinkedList.addFront(mock(TimeSlot.class));
        assertThat(doublyLinkedList.isEmpty()).isFalse();
    }

    @Disabled
    @Test
    public void searchExactInstancesOf() {
        var timeSlot = mock(TimeSlot.class);
        var appointment1 = mock(AppointmentImpl.class);
        var appointment2 = mock(AppointmentImpl.class);
        var appointment3 = mock(AppointmentImpl.class);
        var appointment4 = mock(AppointmentImpl.class);

        doublyLinkedList.addFront(timeSlot);
        doublyLinkedList.addFront(timeSlot);
        doublyLinkedList.addFront(appointment1);
        doublyLinkedList.addFront(timeSlot);
        doublyLinkedList.addFront(appointment2);
        doublyLinkedList.addFront(appointment3);
        doublyLinkedList.addFront(timeSlot);
        doublyLinkedList.addFront(appointment4);

        var foundTimeSlots = doublyLinkedList.searchExactInstancesOf(mock(AppointmentImpl.class).getClass());

        SoftAssertions.assertSoftly(softly -> {
            for(var find : foundTimeSlots) {
                softly.assertThat(find).isExactlyInstanceOf(mock(AppointmentImpl.class).getClass());
            }
        });
    }

    @Test
    public void iterator() {
        var iterator = doublyLinkedList.iterator();
        assertThat(iterator).isExactlyInstanceOf(LinkedListIterator.class);
    }

    @Test
    public void reverseIterator() {
        var reverseIterator = doublyLinkedList.reverseIterator();
        assertThat(reverseIterator).isExactlyInstanceOf(LinkedListBackwardsIterator.class);
    }

    @Test
    public void stream() {
        var firstItem = mock(TimeSlot.class);
        var secondItem = mock(Appointment.class);

        doublyLinkedList.addFront(secondItem);
        doublyLinkedList.addFront(firstItem);

        var stream = doublyLinkedList.stream();
        assertThat(stream.findFirst().get()).isEqualTo(firstItem);
    }

    @Test
    public void reverseStream() {
        var firstItem = mock(TimeSlot.class);
        var secondItem = mock(Appointment.class);

        doublyLinkedList.addFront(secondItem);
        doublyLinkedList.addFront(firstItem);

        var stream = doublyLinkedList.reverseStream();
        assertThat(stream.findFirst().get()).isEqualTo(secondItem);
    }

    @Test
    public void mergeNodesNext() {
        var firstItem = mock(TimeSlot.class);
        var secondItem = mock(Appointment.class);
        var newItem = mock(TimeSlot.class);

        doublyLinkedList.addFront(secondItem);
        doublyLinkedList.addFront(firstItem);

        var firstItemNode = doublyLinkedList.searchItemNode(firstItem);
        var secondItemNode = doublyLinkedList.searchItemNode(secondItem);

        doublyLinkedList.mergeNodesNext(firstItemNode, secondItemNode, newItem);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(doublyLinkedList.searchItemNode(firstItem)).isNull();
            softly.assertThat(doublyLinkedList.searchItemNode(secondItem)).isNull();
            softly.assertThat(doublyLinkedList.searchItemNode(newItem).getItem()).isEqualTo(newItem);
            softly.assertThat(doublyLinkedList.searchItemNode(newItem).getItem().getStart()).isEqualTo(firstItem.getStart());
            softly.assertThat(doublyLinkedList.searchItemNode(newItem).getItem().getEnd()).isEqualTo(firstItem.getEnd());
            softly.assertThat(firstItemNode.getNext()).isNotNull();
            softly.assertThat(firstItemNode.getPrevious()).isNotNull();
            softly.assertThat(secondItemNode.getNext()).isNull();
            softly.assertThat(secondItemNode.getPrevious()).isNull();
        });
    }
}

