package appointmentplanner;

import appointmentplanner.api.Appointment;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.AppointmentImpl;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.TimeslotImpl;
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
}
