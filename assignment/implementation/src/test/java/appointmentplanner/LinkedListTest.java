package appointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

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

    @Test
    public void remove() {
        var timeslot = mock(TimeSlot.class);

        this.list.toFront(timeslot);
        this.list.removeNode(timeslot);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.list.getHead().getNext()).isEqualTo(this.list.getTail());
            softly.assertThat(this.list.getTail().getPrevious()).isEqualTo(this.list.getHead());
            softly.assertThat(this.list.getSize()).isEqualTo(0);
        });
    }

    @Test
    public void removeNodeTest() {
        this.list.toFront(mock(TimeSlot.class));
        this.list.removeNode(mock(TimeSlot.class));

        assertThat(this.list.getSize())
                .isEqualTo(1);
    }

    @Test
    public void toFrontTest() {
        var timeslot = mock(TimeSlot.class);
        var timeslot2 = mock(TimeSlot.class);
        this.list.toFront(timeslot);
        this.list.toFront(timeslot2);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.list.getHead().getNext().getT()).isEqualTo(timeslot2);
            softly.assertThat(this.list.getTail().getPrevious().getT()).isEqualTo(timeslot);
            softly.assertThat(this.list.getSize()).isEqualTo(2);
        });
    }

    @Test
    public void toBackTest() {
        var timeslot = mock(TimeSlot.class);
        var timeslot2 = mock(TimeSlot.class);
        this.list.toBack(timeslot);
        this.list.toBack(timeslot2);

        SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(this.list.getHead().getNext().getT()).isEqualTo(timeslot);
                    softly.assertThat(this.list.getTail().getPrevious().getT()).isEqualTo(timeslot2);
                    softly.assertThat(this.list.getSize()).isEqualTo(2);
                }
        );
    }

    @Test
    public void iteratorTest() {
        var iterator = this.list.iterator();
        assertThat(iterator)
                .isExactlyInstanceOf(LinkedListIterator.class);
    }

    @Test
    public void backwardsIteratorTest() {
        var iteratorBackwards = this.list.iteratorBackwards();
        assertThat(iteratorBackwards)
                .isExactlyInstanceOf(LinkedListBackwardsIterator.class);
    }

    @Test
    public void streamTest() {
        var t = mock(TimeSlot.class);
        var t2 = mock(Appointment.class);

        this.list.toFront(t2);
        this.list.toFront(t);

        var stream = this.list.stream();
        assertThat(stream.findFirst().get()).isEqualTo(t);
    }

    @Test
    public void reverseStream() {
        var t = mock(TimeSlot.class);
        var t2 = mock(Appointment.class);

        this.list.toFront(t2);
        this.list.toFront(t);

        var stream = this.list.streamBackwards();
        assertThat(stream.findFirst().get()).isEqualTo(t2);
    }

    @Test
    public void addInFrontTest() {
        Instant start = LocalDay.now().at(20,30);
        Instant end = start.plusSeconds(560);
        Instant endd = start.plusSeconds(890);
        var timeslot = new TimeslotImpl(start, end);
        var secondTimeslot = new TimeslotImpl(end, endd);
        var setInFrontTimeSlot = new TimeslotImpl(endd, endd.plusSeconds(45));

        this.list.toFront(timeslot);
        this.list.toFront(secondTimeslot);
        this.list.addInFront(setInFrontTimeSlot, timeslot);

        var setInFrontNode = this.list.lookForNode(setInFrontTimeSlot);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(setInFrontNode.getT()).isEqualTo(setInFrontTimeSlot);
            softly.assertThat(setInFrontNode.getPrevious().getT()).isEqualTo(secondTimeslot);
            softly.assertThat(setInFrontNode.getNext().getT()).isEqualTo(timeslot);
            softly.assertThat(setInFrontNode.getNext().getPrevious()).isEqualTo(setInFrontNode);
            softly.assertThat(setInFrontNode.getPrevious().getNext()).isEqualTo(setInFrontNode);
            softly.assertThat(this.list.getSize()).isEqualTo(3);
        });
    }

    @Test
    public void addInFrontTest2() {
        var timeSlot = mock(TimeSlot.class);
        this.list.toFront(timeSlot);
        this.list.addInFront(null, timeSlot);

        assertThat(this.list.getSize())
                .isEqualTo(1);
    }

    @Test
    public void addInFrontTest3() {
        var timeSlot = mock(TimeSlot.class);
        var setInFrontTimeSlot = mock(TimeSlot.class);
        this.list.addInFront(setInFrontTimeSlot, timeSlot);

        assertThat(this.list.getSize())
                .isEqualTo(0);
    }

    @Test
    public void addToBackTest() {
        var timeslot = mock(TimeSlot.class);
        var timeslot2 = mock(TimeSlot.class);
        var setToBackTimeSlot = mock(TimeSlot.class);

        this.list.toFront(timeslot);
        this.list.toFront(timeslot2);
        this.list.addToBack(setToBackTimeSlot, timeslot2);

        var setToBackNode = this.list.lookForNode(setToBackTimeSlot);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(setToBackNode.getT()).isEqualTo(setToBackTimeSlot);
            softly.assertThat(setToBackNode.getPrevious().getT()).isEqualTo(timeslot2);
            softly.assertThat(setToBackNode.getNext().getT()).isEqualTo(timeslot);
            softly.assertThat(setToBackNode.getNext().getPrevious()).isEqualTo(setToBackNode);
            softly.assertThat(setToBackNode.getPrevious().getNext()).isEqualTo(setToBackNode);
            softly.assertThat(this.list.getSize()).isEqualTo(3);
        });
    }

    @Test
    public void lookForTNodeTest() {
        var timeslot = mock(TimeSlot.class);

        this.list.toFront(timeslot);

        assertThat(this.list.lookForTNode(timeslot).getT()).isEqualTo(timeslot);

    }

    @Test
    public void lookForTNodeTest2() {
        var timeslot = mock(TimeSlot.class);

        assertThat(this.list.lookForTNode(timeslot)).isNull();
    }

    @Test
    public void lookForInstancesOfTest() {
        var timeSlot = mock(TimeSlot.class);
        var appointment1 = mock(Appointment.class);
        var appointment2 = mock(Appointment.class);
        var appointment3 = mock(Appointment.class);
        var appointment4 = mock(Appointment.class);

        this.list.toFront(timeSlot);
        this.list.toFront(timeSlot);
        this.list.toFront(appointment1);
        this.list.toFront(timeSlot);
        this.list.toFront(appointment2);
        this.list.toFront(appointment3);
        this.list.toFront(timeSlot);
        this.list.toFront(appointment4);

        var foundTimeSlots = this.list.lookForInstancesOf(mock(Appointment.class).getClass());

        SoftAssertions.assertSoftly(softly -> {
            for(var find : foundTimeSlots) {
                softly.assertThat(find).isExactlyInstanceOf(mock(Appointment.class).getClass());
            }
        });
    }

    @Test
    public void mergeNodesNextTest() {
        var t = mock(TimeSlot.class);
        var t2 = mock(Appointment.class);
        var t3 = mock(TimeSlot.class);

        this.list.toFront(t2);
        this.list.toFront(t);

        var node = this.list.lookForTNode(t);
        var node2 = this.list.lookForTNode(t2);

        this.list.mergeNodesNext(node, node2, t3);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.list.lookForTNode(t)).isNull();
            softly.assertThat(this.list.lookForTNode(t2)).isNull();
            softly.assertThat(this.list.lookForTNode(t3).getT()).isEqualTo(t3);
            softly.assertThat(this.list.lookForTNode(t3).getT().getStart()).isEqualTo(t.getStart());
            softly.assertThat(this.list.lookForTNode(t3).getT().getEnd()).isEqualTo(t.getEnd());
            softly.assertThat(node.getNext()).isNotNull();
            softly.assertThat(node.getPrevious()).isNotNull();
            softly.assertThat(node2.getNext()).isNull();
            softly.assertThat(node2.getPrevious()).isNull();
        });
    }

    @Test
    public void createTest() {
        AllocationNode head = this.list.getHead();
        AllocationNode tail = this.list.getTail();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(head.getT()).isEqualTo(null);
            softly.assertThat(tail.getT()).isEqualTo(null);

            softly.assertThat(head.getNext()).isSameAs(tail);
            softly.assertThat(tail.getPrevious()).isSameAs(head);

            softly.assertThat(head.getPrevious()).isEqualTo(null);
            softly.assertThat(tail.getNext()).isEqualTo(null);
        });
    }
}
