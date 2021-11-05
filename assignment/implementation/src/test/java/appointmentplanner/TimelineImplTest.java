package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimelineImplTest {

    private TimeSlot timeSlot;

    private Stream illegalAppointmentStream;
    private Stream appointmentStream;
    private DoublyLinkedList<TimeSlot> appointments;
    private Instant start, end;
    private Timeline illegalInstantiatedTimeline;
    private TimeLineImpl instantiatedTimeline;

    private AppointmentData mockedAppointmentData;
    private AppointmentRequest mockedAppointmentRequest;
    private TimeSlot mockedTimeSlot;
    private AppointmentImpl appointment;
    private LocalDay localDay;

    @BeforeEach
    public void setUp() {
        var factory = new APFactory();
        this.localDay = LocalDay.now();
        this.appointments = new DoublyLinkedList<TimeSlot>();
        this.start = LocalDay.now().ofLocalTime(LocalTime.parse("08:00"));
        this.end = LocalDay.now().ofLocalTime(LocalTime.parse("18:00"));

        this.illegalInstantiatedTimeline = new TimeLineImpl(start,end,appointments);

        this.mockedAppointmentData = mock(Appointment.class);
        this.mockedAppointmentRequest = mock(AppointmentRequest.class);
        this.mockedTimeSlot = mock(TimeSlot.class);

        when(mockedAppointmentData.getDuration()).thenReturn(Duration.ofMinutes(120));

        appointment = new AppointmentImpl(
                mock(AppointmentData.class),
                mock(AppointmentRequest.class),
                mock(TimeSlot.class));

        timeSlot = new TimeslotImpl(start, end);

        this.appointments.addFront(appointment);
        this.appointments.addFront(this.timeSlot);

        illegalAppointmentStream = illegalInstantiatedTimeline.appointmentStream();

        this.instantiatedTimeline = new TimeLineImpl(start,end);

    }

    @Test
    public void getNrOfAppointments() {
        assertThat(illegalInstantiatedTimeline.getNrOfAppointments()).isEqualTo(1);
    }

    @Test
    public void appointmentStream() {
        assertThat(illegalAppointmentStream
                .allMatch(timeSlot -> timeSlot instanceof Appointment)
        ).isTrue();
    }

    @Test
    public void appointmentStreamCount() {
        assertThat(illegalAppointmentStream.count()).isEqualTo(1);
    }

    @Test
    public void contains() {
        var contains = illegalInstantiatedTimeline.contains(appointment);
        assertThat(contains).isTrue();
    }

    @Test
    public void containsFalse() {
        var contains = illegalInstantiatedTimeline.contains(new AppointmentImpl(
                mock(AppointmentData.class),
                mock(AppointmentRequest.class),
                new TimeslotImpl(start, end)
        ));

        assertThat(contains).isFalse();
    }

    @Test
    public void getGapsFittingSimple() {
        var requiredDuration = Duration.ofMinutes(60);
        var fittingTimeslots = instantiatedTimeline.getGapsFitting(requiredDuration);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(fittingTimeslots.get(0).getStart()).isEqualTo(start);
            assertThat(fittingTimeslots.get(0).getEnd()).isEqualTo(end);
        });
    }

    @Test
    public void getGapsFittingSimpleReversed() {
        var requiredDuration = Duration.ofMinutes(60);
        var fittingTimeslots = instantiatedTimeline.getGapsFittingReversed(requiredDuration);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(fittingTimeslots.get(0).getStart()).isEqualTo(start);
            assertThat(fittingTimeslots.get(0).getEnd()).isEqualTo(end);
        });
    }

    @Test
    public void getGapsFittingSimpleFalse() {
        var requiredDuration = Duration.ofHours(24);
        var fittingTimeslots = instantiatedTimeline.getGapsFitting(requiredDuration);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(fittingTimeslots.isEmpty()).isTrue();
        });
    }

    @Test
    public void canAddAppointmentOfDuration() {
        var fits = instantiatedTimeline.canAddAppointmentOfDuration(Duration.ofMinutes(60));

        assertThat(fits).isTrue();
    }

    @Test
    public void putAppointment() {
        var factory = new APFactory();
        var originalTimeSlot = factory.between(start, end);
        var appointmentDuration = Duration.ofMinutes(120);
        var timeSlot = factory.between(start.plusSeconds(appointmentDuration.toSeconds()), end);

        var paramMap = new HashMap();
        paramMap.put("OriginalTimeSlot", originalTimeSlot);
        paramMap.put("AppointmentSlot", appointment);
        paramMap.put("NextTimeSlot", timeSlot);

        this.instantiatedTimeline.putAppointment(paramMap);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(this.instantiatedTimeline.gapStream().anyMatch((ts -> ts.equals(originalTimeSlot)))).isFalse();
            assertThat(this.instantiatedTimeline.contains(appointment)).isTrue();
            assertThat(this.instantiatedTimeline.gapStream().anyMatch((ts -> ts.equals(timeSlot)))).isTrue();
        });
    }
    @Test
    public void putAppointmentNullAppointment() {
        var factory = new APFactory();
        var originalTimeSlot = factory.between(start, end);
        var appointmentDuration = Duration.ofMinutes(120);
        var timeSlot = factory.between(start.plusSeconds(appointmentDuration.toSeconds()), end);
        var paramMap = new HashMap();

        paramMap.put("OriginalTimeSlot", originalTimeSlot);
        paramMap.put("AppointmentSlot", null);
        paramMap.put("TimeSlot", timeSlot);

        this.instantiatedTimeline.putAppointment(paramMap);
        var test = this.instantiatedTimeline.gapStream().peek(System.out::println).count();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.instantiatedTimeline.contains(appointment)).isFalse();
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch((ts -> ts.equals(timeSlot)))).isFalse();
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch((ts -> ts.equals(originalTimeSlot)))).isTrue();
        });
    }

    @ParameterizedTest
    @CsvSource({
            "13,00",
            "15,00",
            "8,00"
    })
    public void addAppointmentLocalTime(int hours, int minutes) {
        var localTime = LocalTime.of(hours, minutes);
        var localDay = LocalDay.now();

        var appointment = this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, localTime, TimePreference.EARLIEST_AFTER).get();
        assertThat(this.instantiatedTimeline.findAppointments((val1 -> val1.equals(appointment))).get(0)).isEqualTo(appointment);
    }

    @Test
    public void getGapsMultipleGaps() {
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, LocalTime.of(9, 0));
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, LocalTime.of(12, 0));
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, LocalTime.of(15, 0));
        assertThat(instantiatedTimeline.getGapsFitting(Duration.ofMinutes(60)).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @CsvSource({
            "13,00,EARLIEST_AFTER",
            "15,00,LATEST_BEFORE",
            "8,00,LATEST_BEFORE",
            "8,00,EARLIEST_AFTER"
    })
    public void addAppointmentLocalTimeNoPreferredSlot(int hours, int minutes, TimePreference timePreference) {
        var localTime = LocalTime.of(hours, minutes);
        var localDay = LocalDay.now();

        this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, localTime, timePreference).get();
        var appointment = this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, localTime, timePreference).get();
        assertThat(this.instantiatedTimeline.findAppointments((val1 -> val1.equals(appointment))).get(0)).isEqualTo(appointment);
    }

    @ParameterizedTest
    @CsvSource({
            "UNSPECIFIED, true",
            "EARLIEST, true",
            "UNSPECIFIED, false",
            "EARLIEST, false"
    })
    public void addAppointmentTimePreferenceUnspecifiedFirst(TimePreference timePreference, boolean fullParams) {
        var localDay = LocalDay.now();
        when(mockedAppointmentData.getDuration()).thenReturn(Duration.ofMinutes(120));

        Appointment returnedAppointment;
        if (fullParams) {
            returnedAppointment = this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, null, timePreference).get();
            this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, null, null).get();
        } else {
            returnedAppointment = this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, timePreference).get();
            this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, (TimePreference) null).get();
        }
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(returnedAppointment.getAppointmentData()).isEqualTo(mockedAppointmentData);
            softly.assertThat(this.instantiatedTimeline.appointmentStream().findFirst().get().getAppointmentData()).isEqualTo(mockedAppointmentData);
            softly.assertThat(this.instantiatedTimeline.appointmentStream().count()).isEqualTo(2);
            softly.assertThat(this.instantiatedTimeline.appointmentStream().findFirst().get().getRequest().getStartTime()).isEqualTo(LocalTime.ofInstant(start, localDay.getZone()));
        });
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"
    })
    public void addAppointmentTimePreferenceUnspecifiedLatest(boolean fullParams) {
        when(mockedAppointmentData.getDuration()).thenReturn(Duration.ofMinutes(120));
        var timePreference = TimePreference.LATEST;

        Appointment returnedAppointment;
        if (fullParams) {
            returnedAppointment = this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, null, timePreference).get();
        } else {
            returnedAppointment = this.instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, timePreference).get();
        }
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(returnedAppointment.getAppointmentData()).isEqualTo(mockedAppointmentData);
            softly.assertThat(this.instantiatedTimeline.appointmentStream().findFirst().get().getAppointmentData()).isEqualTo(mockedAppointmentData);
            softly.assertThat(this.instantiatedTimeline.appointmentStream().count()).isEqualTo(1);
            softly.assertThat(this.instantiatedTimeline.appointmentStream().findFirst().get().getRequest().getStartTime())
                    .isEqualTo(LocalTime.ofInstant(end.minusSeconds(mockedAppointmentData.getDuration().toSeconds()), localDay.getZone()));
        });
    }

    @Test
    public void removeAppointmentAppointmentItem() {
        var returnedAppointment = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        this.instantiatedTimeline.removeAppointment(returnedAppointment);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.instantiatedTimeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointment))).isFalse();
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch(timeSlot -> timeSlot.getStart().equals(this.start)));
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch(timeSlot -> timeSlot.getEnd().equals(this.end)));
        });
    }

    @Test
    public void removeAppointmentAppointmentItemAppointmentAfter() {
        var returnedAppointment = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var returnedAppointmentNotRemoved = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        this.instantiatedTimeline.removeAppointment(returnedAppointment);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.instantiatedTimeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointment))).isFalse();
            softly.assertThat(this.instantiatedTimeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointmentNotRemoved))).isTrue();
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch(timeSlot -> timeSlot.getStart().equals(this.start) && timeSlot.getEnd().equals(returnedAppointmentNotRemoved.getStart())));
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch(timeSlot -> timeSlot.getEnd().equals(this.end) && timeSlot.getStart().equals(returnedAppointmentNotRemoved.getEnd())));
        });
    }

    @Test
    public void removeAppointmentAppointmentItemAppointmentBefore() {
        var returnedAppointmentNotRemoved = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var returnedAppointment = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        this.instantiatedTimeline.removeAppointment(returnedAppointment);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(this.instantiatedTimeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointment))).isFalse();
            softly.assertThat(this.instantiatedTimeline.appointmentStream().anyMatch(timeSlot -> timeSlot.equals(returnedAppointmentNotRemoved))).isTrue();
            softly.assertThat(this.instantiatedTimeline.gapStream().anyMatch(timeSlot -> timeSlot.getEnd().equals(this.end) && timeSlot.getStart().equals(returnedAppointmentNotRemoved.getEnd())));
        });
    }

    @Test
    public void removeAppointmentBackMultiple() {
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app1 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app2 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();

        instantiatedTimeline.removeAppointment(app1);
        instantiatedTimeline.removeAppointment(app2);

        assertThat(instantiatedTimeline.getGapsFitting(mockedAppointmentData.getDuration()).size()).isEqualTo(1);
    }

    @Test
    public void removeAppointmentMiddleMultiple() {
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app1 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app2 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();

        instantiatedTimeline.removeAppointment(app1);
        instantiatedTimeline.removeAppointment(app2);

        assertThat(instantiatedTimeline.getGapsFitting(mockedAppointmentData.getDuration()).size()).isEqualTo(2);
    }

    @Test
    public void testGapCountAfterRemovingSomeAppsInFullDay() {
        var secondTimeLine = new TimeLineImpl(localDay.ofLocalTime(LocalTime.parse("08:30")), localDay.ofLocalTime(LocalTime.parse("17:30")));

        var mockedAppointmentData50 = mock(AppointmentData.class);
        when(mockedAppointmentData50.getDuration()).thenReturn(Duration.ofMinutes(50));
        when(mockedAppointmentData50.getDescription()).thenReturn("Appointment");

        var mockedGapAppointmentData60 = mock(AppointmentData.class);
        when(mockedGapAppointmentData60.getDuration()).thenReturn(Duration.ofMinutes(60));
        when(mockedGapAppointmentData60.getDescription()).thenReturn("gap");

        var mockedGapAppointmentData10 = mock(AppointmentData.class);
        when(mockedGapAppointmentData10.getDuration()).thenReturn(Duration.ofMinutes(10));
        when(mockedGapAppointmentData10.getDescription()).thenReturn("gap");

        var mockedAppointmentData60 = mock(AppointmentData.class);
        when(mockedAppointmentData60.getDuration()).thenReturn(Duration.ofMinutes(60));
        when(mockedAppointmentData60.getDescription()).thenReturn("Appointment");

        secondTimeLine.addAppointment(localDay, mockedAppointmentData50, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedGapAppointmentData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedGapAppointmentData60, LocalTime.of(9,30));
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60, TimePreference.EARLIEST);

        secondTimeLine.removeAppointments((val1) -> val1.getDescription().contains("gap"));

        assertThat(secondTimeLine.getGapsFitting(Duration.ZERO).size()).isEqualTo(1);
    }

    @Test
    public void removeAppointmentPredicateItem() {
        var returnedAppointment = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        Predicate<Appointment> predicate = (val1) -> (val1.getEnd().equals(returnedAppointment.getEnd()));

        var appointmentList = this.instantiatedTimeline.removeAppointments(predicate);
        var appointmentStream = this.instantiatedTimeline.appointmentStream();

        assertThat(appointmentStream.anyMatch(appointment -> appointmentList.contains(appointment))).isFalse();
    }

    @Test
    public void findAppointments() {
        var expected = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var actual = this.instantiatedTimeline.findAppointments(val1 -> val1.equals(expected)).get(0);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getGapsFittingSmallestFirst() {
        var mockedAppointmentData60Duration = mock(AppointmentData.class);
        when(mockedAppointmentData60Duration.getDuration()).thenReturn(Duration.ofMinutes(60));
        var app1 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app2 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.UNSPECIFIED).get();
        var app3 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.UNSPECIFIED).get();
        var app4 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app5 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.UNSPECIFIED).get();
        var app6 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();

        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app1.getEnd()));
        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app2.getEnd()));
        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app4.getEnd()));
        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app6.getEnd()));

        var orderList = instantiatedTimeline.getGapsFittingSmallestFirst(Duration.ofMinutes(120));


        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(orderList.get(0).duration()).isEqualTo(Duration.ofMinutes(120));
            softly.assertThat(orderList.get(1).duration()).isEqualTo(Duration.ofMinutes(180));
            softly.assertThat(orderList.get(2).duration()).isEqualTo(Duration.ofMinutes(180));
        });
    }

    @Test
    public void getGapsFittingLargestFirst() {
        var mockedAppointmentData60Duration = mock(AppointmentData.class);
        when(mockedAppointmentData60Duration.getDuration()).thenReturn(Duration.ofMinutes(60));
        var app1 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app2 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.UNSPECIFIED).get();
        var app3 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.UNSPECIFIED).get();
        var app4 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();
        var app5 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.UNSPECIFIED).get();
        var app6 =instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.UNSPECIFIED).get();

        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app1.getEnd()));
        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app2.getEnd()));
        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app4.getEnd()));
        instantiatedTimeline.removeAppointments(val1 -> val1.getEnd().equals(app6.getEnd()));

        var orderList = instantiatedTimeline.getGapsFittingLargestFirst(Duration.ofMinutes(120));


        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(orderList.get(0).duration()).isEqualTo(Duration.ofMinutes(180));
            softly.assertThat(orderList.get(1).duration()).isEqualTo(Duration.ofMinutes(180));
            softly.assertThat(orderList.get(2).duration()).isEqualTo(Duration.ofMinutes(120));
        });
    }

    @Test
    public void matchingFreeSlotsOfDuration() {
        var mockedAppointmentData60Duration = mock(AppointmentData.class);
        when(mockedAppointmentData60Duration.getDuration()).thenReturn(Duration.ofMinutes(60));

        var secondTimeLine = new TimeLineImpl(start, end);
        var app1 = secondTimeLine.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        var app2 = secondTimeLine.addAppointment(localDay, mockedAppointmentData, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData, TimePreference.LATEST);
        secondTimeLine.removeAppointment(app1.get());
        secondTimeLine.removeAppointment(app2.get());

        app1 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData60Duration, TimePreference.EARLIEST);
        app2 = instantiatedTimeline.addAppointment(localDay, mockedAppointmentData, TimePreference.EARLIEST);

        instantiatedTimeline.removeAppointment(app1.get());
        instantiatedTimeline.removeAppointment(app2.get());

        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = instantiatedTimeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(120), timeLineList);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(list.get(0).getStartTime(localDay)).isEqualTo(LocalTime.of(10,0));
            softly.assertThat(list.get(0).getEndTime(localDay)).isEqualTo(LocalTime.of(16,0));

        });
    }
    @Test
    public void matchingFreeSlotsOfDurationNoCommonGapsOneGap() {
        var mockedAppointmentData300Duration = mock(AppointmentData.class);
        when(mockedAppointmentData300Duration.getDuration()).thenReturn(Duration.ofMinutes(300));

        var secondTimeLine = new TimeLineImpl(start, end);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData300Duration, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData300Duration, TimePreference.LATEST);

        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = instantiatedTimeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(120), timeLineList);

        assertThat(list.size()).isEqualTo(0);
    }
    @Disabled
    @Test
    public void matchingFreeSlotsOfDuration0DurationGap() {
        var mockedAppointmentData300Duration = mock(AppointmentData.class);
        when(mockedAppointmentData300Duration.getDuration()).thenReturn(Duration.ofMinutes(300));

        var secondTimeLine = new TimeLineImpl(start, end);
        secondTimeLine.addAppointment(localDay, mockedAppointmentData300Duration, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, mockedAppointmentData300Duration, TimePreference.EARLIEST);

        var start0 = localDay.ofLocalTime(LocalTime.parse("18:00"));
        var timeSlot = new TimeslotImpl(start0, start0);
        var tempList = new ArrayList<>();
        var secondTimeLineList = secondTimeLine.getList();
        secondTimeLineList.addEnd(timeSlot);
        var instantiatedTimeLineList = instantiatedTimeline.getList();
        instantiatedTimeLineList.addEnd(timeSlot);
        var timeLineList = new ArrayList();

        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = instantiatedTimeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(0), timeLineList);

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void matchingFreeSlotsOfDurationEdging0Gap() {
        var start0 = localDay.ofLocalTime(LocalTime.parse("18:00"));
        var secondTimeLine = new TimeLineImpl(start0, start0);

        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);
        List<TimeSlot> list = instantiatedTimeline.getMatchingFreeSlotsOfDuration(Duration.ZERO, timeLineList);

        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void matchingFreeSlotsOfDurationNoCommonGapsMultipleGaps() {
        var secondTimeLine = new TimeLineImpl(start, end);

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

        secondTimeLine.addAppointment(localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, appointmentData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, appointmentData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, appointmentData5, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, gapData10, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, appointmentData5, TimePreference.EARLIEST);
        secondTimeLine.addAppointment(localDay, gapData60, TimePreference.EARLIEST);

        instantiatedTimeline.addAppointment(localDay, appointmentData30, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, gapData30, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, appointmentData10, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, gapData10, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, appointmentData10, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, gapData10, TimePreference.EARLIEST);
        instantiatedTimeline.addAppointment(localDay, appointmentData30, TimePreference.EARLIEST);

        secondTimeLine.removeAppointments((val1) -> val1.getDescription().equals("gap"));
        instantiatedTimeline.removeAppointments((val1) -> val1.getDescription().equals("gap"));
        var timeLineList = new ArrayList();
        timeLineList.add(secondTimeLine);

        List<TimeSlot> list = instantiatedTimeline.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(10), timeLineList);

        assertThat(list.size()).isEqualTo(4);
    }

    @Test
    public void noCommonFree3() {
        var fac = new APFactory();
        var T08_30 = LocalTime.of(8, 30);
        LocalDayPlan sd1 = new LocalDayPlanImpl(localDay, localDay.ofLocalTime(LocalTime.parse("08:30")), localDay.ofLocalTime(LocalTime.parse("17:30")));
        LocalDayPlan sd2 = new LocalDayPlanImpl(localDay, localDay.ofLocalTime(LocalTime.parse("08:30")), localDay.ofLocalTime(LocalTime.parse("17:30")));
        sd1.addAppointment(fac.createAppointmentData("all day", Duration.ofHours(9), Priority.LOW), T08_30);
        sd2.addAppointment(fac.createAppointmentData("all afternoon", Duration.ofHours(6), Priority.LOW), LocalTime.of(11, 30));
        List<TimeSlot> matchingFreeSlotsOfDuration = sd2.getMatchingFreeSlotsOfDuration(Duration.ofMinutes(15), List.of(sd1));
        assertThat(matchingFreeSlotsOfDuration)
                .as("dayplans <%s> and <%s> should have no common gaps", sd1, sd2)
                .isEmpty();
    }
}
