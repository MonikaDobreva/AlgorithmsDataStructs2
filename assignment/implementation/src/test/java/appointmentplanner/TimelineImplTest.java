package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Test
    public void matchingFreeSlotsOfDurationTest() {
        var mockedAppointmentData60Duration = mock(AppointmentData.class);
        when(mockedAppointmentData60Duration.getDuration()).thenReturn(Duration.ofMinutes(60));

        var secondTimeLine = new TimelineImpl(this.start, this.end);
        var app1 = secondTimeLine.addAppointment(this.localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        var app2 = secondTimeLine.addAppointment(this.localDay, this.appointmentData, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, this.appointmentData, TimePreference.LATEST);
        secondTimeLine.removeAppointment(app1.get());
        secondTimeLine.removeAppointment(app2.get());

        app1 = this.timeline.addAppointment(this.localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        app2 = this.timeline.addAppointment(this.localDay, this.appointmentData, TimePreference.EARLIEST);

        this.timeline.removeAppointment(app1.get());
        this.timeline.removeAppointment(app2.get());

        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = this.timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(120), timeLineList);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(list.get(0).getStartTime(this.localDay)).isEqualTo(LocalTime.of(10,0));
            softly.assertThat(list.get(0).getEndTime(this.localDay)).isEqualTo(LocalTime.of(16,0));

        });
    }
    @Test
    public void matchingFreeSlotsOfDurationTest2() {
        var mockedAppointmentData300Duration = mock(AppointmentData.class);
        when(mockedAppointmentData300Duration.getDuration()).thenReturn(Duration.ofMinutes(300));

        var secondTimeLine = new TimelineImpl(this.start, this.end);
        secondTimeLine.addAppointment(this.localDay, mockedAppointmentData300Duration, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, mockedAppointmentData300Duration, TimePreference.LATEST);

        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = this.timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(120), timeLineList);

        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void matchingFreeSlotsOfDurationTest3() {
        var mockedAppointmentData300Duration = mock(AppointmentData.class);
        when(mockedAppointmentData300Duration.getDuration()).thenReturn(Duration.ofMinutes(300));

        var secondTimeLine = new TimelineImpl(this.start, this.end);
        secondTimeLine.addAppointment(this.localDay, mockedAppointmentData300Duration, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, mockedAppointmentData300Duration, TimePreference.EARLIEST);

        var start0 = localDay.ofLocalTime(LocalTime.parse("18:00"));
        var timeSlot = new TimeslotImpl(start0, start0);
        var tempList = new ArrayList<>();
        var secondTimeLineList = secondTimeLine.list();
        secondTimeLineList.toBack(timeSlot);
        var instantiatedTimeLineList = this.timeline.list();
        instantiatedTimeLineList.toBack(timeSlot);
        var timeLineList = new ArrayList();

        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = this.timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(0), timeLineList);

        assertThat(list.size())
                .isEqualTo(1);
    }

    @Test
    public void matchingFreeSlotsOfDurationTest4() {
        var start0 = this.localDay.ofLocalTime(LocalTime.parse("18:00"));
        var secondTimeLine = new TimelineImpl(start0, start0);

        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);
        List<TimeSlot> list = this.timeline.getMatchingFreeSlotsOfDuration(Duration.ZERO, timeLineList);

        assertThat(list.size())
                .isEqualTo(0);
    }

    @Test
    public void matchingFreeSlotsOfDurationTest5() {
        var secondTimeLine = new TimelineImpl(this.start, this.end);

        var gapData10 = mock(AppointmentData.class);
        when(gapData10.getDuration()).thenReturn(Duration.ofMinutes(10));
        when(gapData10.getDescription()).thenReturn("gap");
        var gapData30 = mock(AppointmentData.class);
        when(gapData30.getDuration()).thenReturn(Duration.ofMinutes(30));
        when(gapData30.getDescription()).thenReturn("gap");
        var gapData60 = mock(AppointmentData.class);
        when(gapData60.getDuration()).thenReturn(Duration.ofMinutes(60));
        when(gapData60.getDescription()).thenReturn("gap");

        var appointmentData5 = mock(AppointmentData.class);
        when(appointmentData5.getDuration()).thenReturn(Duration.ofMinutes(5));
        when(appointmentData5.getDescription()).thenReturn("App");
        var appointmentData10 = mock(AppointmentData.class);
        when(appointmentData10.getDuration()).thenReturn(Duration.ofMinutes(10));
        when(appointmentData10.getDescription()).thenReturn("App");
        var appointmentData30 = mock(AppointmentData.class);
        when(appointmentData30.getDuration()).thenReturn(Duration.ofMinutes(30));
        when(appointmentData30.getDescription()).thenReturn("App");

        secondTimeLine.addAppointment(this.localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, appointmentData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, appointmentData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, appointmentData5, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, appointmentData5, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(this.localDay, gapData60, TimePreference.EARLIEST);

        this.timeline.addAppointment(this.localDay, appointmentData30, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, gapData30, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, appointmentData10, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, gapData10, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, appointmentData10, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, gapData10, TimePreference.EARLIEST);
        this.timeline.addAppointment(this.localDay, appointmentData30, TimePreference.EARLIEST);

        secondTimeLine.removeAppointments((val1) -> val1.getDescription().equals("gap"));
        this.timeline.removeAppointments((val1) -> val1.getDescription().equals("gap"));
        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = this.timeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(10), timeLineList);

        assertThat(list.size())
                .isEqualTo(4);
    }

    @Test
    public void noCommonFree() {
        var fac = new APFactory();
        var T08_30 = LocalTime.of(8, 30);
        LocalDayPlan ldp = new LocalDayPlanImpl(this.localDay, this.localDay.ofLocalTime(LocalTime.parse("08:30")), this.localDay.ofLocalTime(LocalTime.parse("17:30")));
        LocalDayPlan ldp2 = new LocalDayPlanImpl(this.localDay, this.localDay.ofLocalTime(LocalTime.parse("08:30")), this.localDay.ofLocalTime(LocalTime.parse("17:30")));
        ldp.addAppointment(fac.createAppointmentData("all day", Duration.ofHours(9), Priority.LOW), T08_30);
        ldp2.addAppointment(fac.createAppointmentData("all afternoon", Duration.ofHours(6), Priority.LOW), LocalTime.of(11, 30));
        List<TimeSlot> matchingFreeSlotsOfDuration = ldp2.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(15), List.of(ldp));
        assertThat(matchingFreeSlotsOfDuration)
                .as("dayplans <%s> and <%s> should have no common gaps", ldp, ldp2)
                .isEmpty();
    }
}
