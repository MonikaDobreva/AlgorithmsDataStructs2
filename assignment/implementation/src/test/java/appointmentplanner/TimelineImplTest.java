package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimelineImplTest {
    private TimeSlot timeSlot;
    private AppointmentData appointmentData;
    private AppointmentRequest appointmentRequest;
    private TimeSlot otherTimeSlot;
    private AppointmentImpl appointment;
    private LocalDay localDay;

    private Timeline noTimeline;
    private TimelineImpl timeline;
    private Stream noStream;
    private Stream appointmentStream;
    private DoublyLinkedList<TimeSlot> list;
    private Instant start, end;

    @BeforeEach
    public void setUp() {
        var fac = new APFactory();
        this.localDay = LocalDay.now();
        this.list = new DoublyLinkedList<TimeSlot>();

        this.start = LocalDay.now().ofLocalTime(LocalTime.parse("08:00"));
        this.end = LocalDay.now().ofLocalTime(LocalTime.parse("18:00"));
        this.noTimeline = new TimelineImpl(this.start, this.end, this.list);
        this.noStream = this.noTimeline.appointmentStream();

        this.appointmentData = mock(Appointment.class);
        this.appointmentRequest = mock(AppointmentRequest.class);
        this.otherTimeSlot = mock(TimeSlot.class);
        this.timeline = new TimelineImpl(this.start, this.end);

        when(this.appointmentData.getDuration()).thenReturn(Duration.ofMinutes(120));

        this.appointment = new AppointmentImpl(
                mock(AppointmentRequest.class),
                mock(TimeSlot.class));

        this.timeSlot = new TimeslotImpl(this.start, this.end);

        this.list.toFront(this.appointment);
        this.list.toFront(this.timeSlot);
    }

    @Test
    public void getNrOfAppointmentsTest() {
        assertThat(this.noTimeline.getNrOfAppointments()).isEqualTo(1);
    }

    @Test
    public void appointmentStreamTest() {
        assertThat(this.noStream
                .allMatch(timeSlot -> timeSlot instanceof Appointment)
        ).isTrue();
    }

    @Test
    public void appointmentStreamTest2() {
        assertThat(this.noStream.count()).isEqualTo(1);
    }

    @Test
    public void containsTest() {
        var contains = this.noTimeline.contains(this.appointment);
        assertThat(contains).isTrue();
    }

    @Test
    public void containsTest2() {
        //create a boolean to check if an appointment is in the list (timeline)
        var contains = this.noTimeline.contains(new AppointmentImpl( //obviously we are checking for a brand new appointment
                mock(AppointmentRequest.class),
                new TimeslotImpl(this.start, this.end)
        ));
        //so we want to receive the boolean as false
        assertThat(contains).isFalse();
    }

    @Test
    public void findAppointments() {
        var expected = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var actual = this.timeline.findAppointments(val -> val.equals(expected)).get(0);

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    public void putAppointmentTest() {
        var factory = new APFactory();
        var originalTimeSlot = factory.between(this.start, this.end);
        var appointmentDuration = Duration.ofMinutes(120);
        var timeSlot = factory.between(this.start.plusSeconds(appointmentDuration.toSeconds()), this.end);

        var paramMap = new HashMap();
        paramMap.put("OriginalTimeSlot", originalTimeSlot);
        paramMap.put("AppointmentSlot", this.appointment);
        paramMap.put("NextTimeSlot", timeSlot);

        this.timeline.putAppointment(paramMap);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(this.timeline.gapStream().anyMatch((ts -> ts.equals(originalTimeSlot)))).isFalse();
            assertThat(this.timeline.contains(this.appointment)).isTrue();
            assertThat(this.timeline.gapStream().anyMatch((ts -> ts.equals(timeSlot)))).isTrue();
        });
    }

    @Test
    public void putAppointmentTest2() {
        var factory = new APFactory();
        var originalTimeSlot = factory.between(start, end);
        var appointmentDuration = Duration.ofMinutes(120);
        var timeSlot = factory.between(start.plusSeconds(appointmentDuration.toSeconds()), end);
        var paramMap = new HashMap();

        paramMap.put("OriginalTimeSlot", originalTimeSlot);
        paramMap.put("AppointmentSlot", null);
        paramMap.put("TimeSlot", timeSlot);

        this.timeline.putAppointment(paramMap);
        var test = this.timeline.gapStream().peek(System.out::println).count();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.timeline.contains(appointment)).isFalse();
            softly.assertThat(this.timeline.gapStream().anyMatch((ts -> ts.equals(timeSlot)))).isFalse();
            softly.assertThat(this.timeline.gapStream().anyMatch((ts -> ts.equals(originalTimeSlot)))).isTrue();
        });
    }

    @Test
    public void removeAppointmentTest() {
        var returnedAppointment = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        this.timeline.removeAppointment(returnedAppointment);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.timeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointment))).isFalse();
            softly.assertThat(this.timeline.gapStream().anyMatch(timeSlot -> timeSlot.getStart().equals(this.start)));
            softly.assertThat(this.timeline.gapStream().anyMatch(timeSlot -> timeSlot.getEnd().equals(this.end)));
        });
    }

    @Test
    public void removeAppointmentTest2() {
        var returnedAppointment = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var returnedAppointmentNotRemoved = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        this.timeline.removeAppointment(returnedAppointment);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.timeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointment))).isFalse();
            softly.assertThat(this.timeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointmentNotRemoved))).isTrue();
            softly.assertThat(this.timeline.gapStream().anyMatch(timeSlot -> timeSlot.getStart().equals(this.start) && timeSlot.getEnd().equals(returnedAppointmentNotRemoved.getStart())));
            softly.assertThat(this.timeline.gapStream().anyMatch(timeSlot -> timeSlot.getEnd().equals(this.end) && timeSlot.getStart().equals(returnedAppointmentNotRemoved.getEnd())));
        });
    }

    @Test
    public void removeAppointmentTest3() {
        var returnedAppointmentNotRemoved = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var returnedAppointment = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        this.timeline.removeAppointment(returnedAppointment);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.timeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointment))).isFalse();
            softly.assertThat(this.timeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointmentNotRemoved))).isTrue();
            softly.assertThat(this.timeline.gapStream().anyMatch(timeSlot -> timeSlot.getEnd().equals(this.end) && timeSlot.getStart().equals(returnedAppointmentNotRemoved.getEnd())));
        });
    }

    @Test
    public void removeAppointmentTest4() {
        this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var app1 = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var app2 = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();

        this.timeline.removeAppointment(app1);
        this.timeline.removeAppointment(app2);

        assertThat(this.timeline.getGapsFitting(this.appointmentData.getDuration()).size()).isEqualTo(1);
    }

    @Test
    public void removeAppointmentTest5() {
        this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var app1 = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        var app2 = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();
        this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.UNSPECIFIED).get();

        this.timeline.removeAppointment(app1);
        this.timeline.removeAppointment(app2);

        assertThat(this.timeline.getGapsFitting(this.appointmentData.getDuration()).size()).isEqualTo(2);
    }

    @Test
    public void getGapsFittingTest() {
        var requiredDuration = Duration.ofMinutes(60);
        var fittingTimeslots = this.timeline.getGapsFitting(requiredDuration);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(fittingTimeslots.get(0).getStart()).isEqualTo(this.start);
            assertThat(fittingTimeslots.get(0).getEnd()).isEqualTo(this.end);
        });
    }

    @Test
    public void getGapsFittingTest2() {
        var requiredDuration = Duration.ofMinutes(60);
        var fittingTimeslots = this.timeline.getGapsFittingReversed(requiredDuration);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(fittingTimeslots.get(0).getStart()).isEqualTo(this.start);
            assertThat(fittingTimeslots.get(0).getEnd()).isEqualTo(this.end);
        });
    }

    @Test
    public void getGapsFittingTest3() {
        var requiredDuration = Duration.ofHours(24);
        var fittingTimeslots = this.timeline.getGapsFitting(requiredDuration);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(fittingTimeslots.isEmpty()).isTrue();
        });
    }

    @Test
    public void canAddAppointmentOfDurationTest() {
        var fits = this.timeline.canAddAppointmentOfDuration(Duration.ofMinutes(60));

        assertThat(fits).isTrue();
    }
}
