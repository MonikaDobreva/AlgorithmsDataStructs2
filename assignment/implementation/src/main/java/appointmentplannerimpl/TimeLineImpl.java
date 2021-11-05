package appointmentplannerimpl;


import appointmentplanner.APFactory;
import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TimeLineImpl implements Timeline {
    private Instant start;
    private Instant end;
    private DoublyLinkedList<TimeSlot> appointments;

    public TimeLineImpl(Instant start, Instant end) {
        this(start, end, new DoublyLinkedList<>());
    }

    public TimeLineImpl(Instant start, Instant end, DoublyLinkedList<TimeSlot> appointments) {
        this.start = start;
        this.end = end;
        this.appointments = appointments;

        if (this.appointments.getSize() == 0) {
            var factory = new APFactory();
            this.appointments.addFront(factory.between(start, end));
        }
    }

    @Override
    public int getNrOfAppointments() {
        return (int) appointmentStream().count();
    }

    @Override
    public Instant start() {
        return this.start;
    }

    @Override
    public Instant end() {
        return this.end;
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay localDay, AppointmentData appointmentData, TimePreference fallBack) {
        return addAppointment(localDay, appointmentData, null, fallBack);
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime) {
        return addAppointment(forDay, appointment, startTime, null);
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime localTime, TimePreference fallback) {
        if (appointment == null) {
            throw new NullPointerException("AppointmentData must not be null");
        }
        var appointmentDuration = appointment.getDuration();
        Map<String, Optional<TimeSlot>> timeSlotOptMap = new HashMap();

        if (localTime != null) {
            timeSlotOptMap = this.findPreferredTimeSlot(appointmentDuration, localTime, forDay, fallback);
        }
        if (fallback == null) {
            fallback = TimePreference.UNSPECIFIED;
        }

        if (localTime == null || timeSlotOptMap == null) {
            switch (fallback) {
                case LATEST:
                    timeSlotOptMap = findLastFittingTimeSlot(appointmentDuration);
                    break;
                default:
                    timeSlotOptMap = findFirstFittingTimeSlot(appointmentDuration);

            }
        }


        var timeSlotMap = optionalToTimeSlot(timeSlotOptMap);
        if (timeSlotOptMap.containsKey("AppointmentSlot")) {
            if (!timeSlotOptMap.get("AppointmentSlot").isEmpty()) {

                timeSlotMap.replace("AppointmentSlot", buildAppointment(
                        appointment,
                        LocalTime.ofInstant(timeSlotMap.get("AppointmentSlot").getStart(), forDay.getZone()),
                        fallback,
                        timeSlotOptMap.get("AppointmentSlot").get()).get());

                putAppointment(timeSlotMap);
                return Optional.of((Appointment) timeSlotMap.get("AppointmentSlot"));
            }
        }
        return Optional.empty();
    }

    private Map<String, TimeSlot> optionalToTimeSlot(Map<String, Optional<TimeSlot>> timeSlotOptMap) {
        var timeSlotMap = new HashMap();
        Optional<TimeSlot> timeSlotOpt;

        if (timeSlotOptMap.containsKey("NextTimeSlot")) {
            timeSlotOpt = timeSlotOptMap.get("NextTimeSlot");
            if (!timeSlotOpt.isEmpty()) {
                timeSlotMap.put("NextTimeSlot", timeSlotOpt.get());
            } else {
                timeSlotMap.put("NextTimeSlot", null);
            }
        }
        timeSlotOpt = timeSlotOptMap.get("AppointmentSlot");
        if (!timeSlotOpt.isEmpty()) {
            timeSlotMap.put("AppointmentSlot", timeSlotOpt.get());
        } else {
            timeSlotMap.put("AppointmentSlot", null);
        }

        timeSlotOpt = timeSlotOptMap.get("OriginalTimeSlot");
        if (!timeSlotOpt.isEmpty()) {
            timeSlotMap.put("OriginalTimeSlot", timeSlotOpt.get());
        } else {
            timeSlotMap.put("OriginalTimeSlot", null);
        }

        if (timeSlotOptMap.containsKey("PreviousTimeSlot")) {
            timeSlotOpt = timeSlotOptMap.get("PreviousTimeSlot");
            if (!timeSlotOpt.isEmpty()) {
                timeSlotMap.put("PreviousTimeSlot", timeSlotOpt.get());
            } else {
                timeSlotMap.put("PreviousTimeSlot", null);
            }
        }
        return timeSlotMap;
    }

    Map<String, Optional<TimeSlot>> findPreferredTimeSlot(Duration appointmentDuration, LocalTime preferredTime, LocalDay forDay, TimePreference fallBack) {
        List<TimeSlot> gapsFittingList = new ArrayList();

        this.getGapsFitting(appointmentDuration).stream()
                .filter(timeSlot -> timeSlot.fits(appointmentDuration))
                .forEach(gapsFittingList::add);

        var preferredSlot = gapsFittingList.stream()
                .filter(timeSlot -> {
                    var startTime = timeSlot.getStartTime(forDay);
                    var endTime = timeSlot.getEndTime(forDay);
                    var endTimeAppointment = preferredTime.plusMinutes(appointmentDuration.toMinutes());
                    return (startTime.isBefore(preferredTime) || startTime.equals(preferredTime)) &&
                            (endTime.isAfter(endTimeAppointment) || endTime.equals(endTimeAppointment));
                })
                .map(timeSlot -> new TimeslotImpl(forDay.ofLocalTime(preferredTime), forDay.ofLocalTime(preferredTime.plusMinutes(appointmentDuration.toMinutes()))))
                .map(TimeSlot.class::cast)
                .findAny();

        if (preferredSlot.isEmpty()) {
            if (fallBack == TimePreference.EARLIEST_AFTER) {
                preferredSlot = gapsFittingList.stream()
                        .filter(timeSlot ->
                                timeSlot.getEndTime(forDay).minusMinutes(appointmentDuration.toMinutes()).isAfter(preferredTime)
                        )
                        .map(timeSlot -> new TimeslotImpl(timeSlot.getStart(), timeSlot.getStart().plusSeconds(appointmentDuration.toSeconds())))
                        .map(TimeSlot.class::cast)
                        .findFirst();

            } else if (fallBack == TimePreference.LATEST_BEFORE) {
                preferredSlot = gapsFittingList.stream()
                        .filter(timeSlot ->
                                timeSlot.getStartTime(forDay).plusMinutes(appointmentDuration.toMinutes()).isBefore(preferredTime)
                        )
                        .map(timeSlot -> new TimeslotImpl(timeSlot.getEnd().minusSeconds(appointmentDuration.toSeconds()), timeSlot.getEnd()))
                        .map(TimeSlot.class::cast)
                        .reduce((val1, val2) -> val2);

            }
        }
        if (!preferredSlot.isEmpty()) {
            var appointmentSlot = preferredSlot;
            HashMap<String, Optional<TimeSlot>> returnMap = new HashMap();
            returnMap.put("AppointmentSlot", preferredSlot);
            returnMap.put("OriginalTimeSlot", gapsFittingList.stream()
                    .filter(timeSlot -> (timeSlot.fits(appointmentSlot.get())))
                    .findAny());

            var nextStart = preferredSlot.get().getEnd();
            var nextEnd = returnMap.get("OriginalTimeSlot").get().getEnd();
            if (!(nextStart.isAfter(nextEnd) || nextStart.equals(nextEnd))) {
                returnMap.put("NextTimeSlot", Optional.of(new TimeslotImpl(nextStart, nextEnd)));
            }

            var previousStart = returnMap.get("OriginalTimeSlot").get().getStart();
            var previousEnd = preferredSlot.get().getStart();
            if (!(previousStart.isAfter(previousEnd) || previousStart.equals(previousEnd))) {
                returnMap.put("PreviousTimeSlot", Optional.of(new TimeslotImpl(previousStart, previousEnd)));
            }

            return returnMap;
        }
        return null;
    }

    public Map<String, Optional<TimeSlot>> findLastFittingTimeSlot(Duration appointmentDuration) {
        var gapsFitting = this.getGapsFittingReversed(appointmentDuration);
        Function<TimeSlot, TimeSlot> appointmentMapper = (timeSlot) ->
                new TimeslotImpl(timeSlot.getEnd().minusSeconds(appointmentDuration.toSeconds()), timeSlot.getEnd());

        Function<TimeSlot, TimeSlot> timeSlotMapper = (timeSlot) -> {
            var appointmentTimeSlot = new TimeslotImpl(timeSlot.getEnd().minusSeconds(appointmentDuration.toSeconds()), timeSlot.getEnd());
            return new TimeslotImpl(timeSlot.getStart(), appointmentTimeSlot.getStart());
        };

        return this.findFittingTimeSlot(gapsFitting, appointmentMapper, timeSlotMapper, true);
    }

    public Map<String, Optional<TimeSlot>> findFirstFittingTimeSlot(Duration appointmentDuration) {
        var gapsFitting = this.getGapsFitting(appointmentDuration);

        Function<TimeSlot, TimeSlot> appointmentMapper = (timeSlot) ->
                new TimeslotImpl(timeSlot.getStart(), timeSlot.getStart().plusSeconds(appointmentDuration.toSeconds()));

        Function<TimeSlot, TimeSlot> timeSlotMapper = (timeSlot) -> {
            var appointmentTimeSlot = new TimeslotImpl(timeSlot.getStart(), timeSlot.getStart().plusSeconds(appointmentDuration.toSeconds()));
            return new TimeslotImpl(appointmentTimeSlot.getEnd(), timeSlot.getEnd());
        };

        return this.findFittingTimeSlot(gapsFitting, appointmentMapper, timeSlotMapper, false);
    }

    private Map<String, Optional<TimeSlot>> findFittingTimeSlot(
            List<TimeSlot> gapsFitting,
            Function<TimeSlot, TimeSlot> appointmentMapper,
            Function<TimeSlot, TimeSlot> timeSlotMapper,
            boolean last) {

        var returnMap = new HashMap();

        Optional<TimeSlot> appointmentSlot = gapsFitting.stream()
                .findFirst()
                .stream()
                .map(appointmentMapper)
                .map(TimeSlot.class::cast)
                .findFirst();

        returnMap.put("AppointmentSlot", appointmentSlot);

        Optional<TimeSlot> newTimeSlot = gapsFitting.stream()
                .findFirst()
                .stream()
                .map(timeSlotMapper)
                .map(TimeSlot.class::cast)
                .findFirst();

        if (last) {
            returnMap.put("PreviousTimeSlot", newTimeSlot);
        } else {
            returnMap.put("NextTimeSlot", newTimeSlot);
        }

        Optional<TimeSlot> originalTimeSlot = gapsFitting.stream()
                .findFirst();

        returnMap.put("OriginalTimeSlot", originalTimeSlot);

        return returnMap;
    }

    public void putAppointment(Map<String, TimeSlot> timeSlotMap) {
        var originalNode = this.appointments.searchItemNode(timeSlotMap.get("OriginalTimeSlot"));
        var nextTimeSlot = timeSlotMap.get("NextTimeSlot");
        TimeSlot previousTimeSlot = null;
        if (timeSlotMap.containsKey("PreviousTimeSlot")) {
            previousTimeSlot = timeSlotMap.get("PreviousTimeSlot");
        }
        var appointment = timeSlotMap.get("AppointmentSlot");
        if (appointment != null) {
            if (nextTimeSlot == null) {
                if (previousTimeSlot != null) {
                    originalNode.setItem(previousTimeSlot);
                    this.appointments.addAfter((Appointment) appointment, previousTimeSlot);
                    if (previousTimeSlot.getStart().equals(previousTimeSlot.getEnd())) {
                        var appointmentNode = appointments.searchItemNode(appointment);
                        appointments.mergeNodesPrevious(appointmentNode, originalNode, appointment);
                    }
                } else {
                    originalNode.setItem(appointment);
                }
            } else {
                originalNode.setItem(nextTimeSlot);
                this.appointments.addBefore(appointment, nextTimeSlot);
                if (previousTimeSlot != null) {
                    this.appointments.addBefore(previousTimeSlot, appointment);
                }
                var appointmentNode = appointments.searchItemNode(appointment);
                if (nextTimeSlot.getStart().equals(nextTimeSlot.getEnd())) {
                    appointments.mergeNodesNext(appointmentNode, originalNode, appointment);
                }
            }
        }
    }

    private Optional<Appointment> buildAppointment(AppointmentData appointmentData, LocalTime localTime, TimePreference timePreference, TimeSlot timeSlot) {
        var factory = new APFactory();
        var appointmentRequest = factory.createAppointmentRequest(appointmentData, localTime, timePreference);
        var appointmentTimeSlot = timeSlot;

        return Optional.of(new AppointmentImpl(appointmentData, appointmentRequest, appointmentTimeSlot));
    }

    @Override
    public AppointmentRequest removeAppointment(Appointment appointment) {
        Predicate<Appointment> equalsPredicate = app1 -> app1.equals(appointment);
        var appointments = removeAppointments(equalsPredicate);
        if (appointments.size() > 0) {
            return appointments.get(0);
        }
        return null;
    }

    @Override
    public List<AppointmentRequest> removeAppointments(Predicate<Appointment> filter) {
        var returnList = new ArrayList();
        wholeStream()
                .map(timeSlot -> {
                    if (!(timeSlot instanceof Appointment)) {
                    } else if (filter.test((Appointment) timeSlot)) {
                        returnList.add(((Appointment) timeSlot).getRequest());
                        var factory = new APFactory();
                        var timeSlotNode = this.appointments.searchItemNode(timeSlot);
                        var timeSlotNodePrevious = timeSlotNode.getPrevious();
                        var timeSlotNodeNext = timeSlotNode.getNext();
                        Instant startPoint = timeSlotNode.getItem().getStart();
                        Instant endPoint = timeSlotNode.getItem().getEnd();
                        var mergedNodes = false;

                        if (!(timeSlotNodePrevious.getItem() instanceof Appointment) && timeSlotNodePrevious.getItem() instanceof TimeSlot) {
                            startPoint = timeSlotNodePrevious.getItem().getStart();
                            var tempNode = this.appointments.mergeNodesPrevious(timeSlotNode, timeSlotNodePrevious, factory.between(startPoint, endPoint));
                            mergedNodes = true;
                        }
                        if (!(timeSlotNodeNext.getItem() instanceof Appointment) && timeSlotNodeNext.getItem() instanceof TimeSlot) {
                            endPoint = timeSlotNodeNext.getItem().getEnd();
                            timeSlotNode = this.appointments.mergeNodesNext(timeSlotNode, timeSlotNodeNext, factory.between(startPoint, endPoint));
                            mergedNodes = true;
                        }
                        if (!mergedNodes) {
                            timeSlotNode.setItem(factory.between(startPoint, endPoint));
                        }
                        return timeSlotNode.getItem();
                    }
                    return null;
                })
                .count();

        return returnList;
    }

    @Override
    public List<Appointment> findAppointments(Predicate<Appointment> predicate) {
        var list = new ArrayList();
        appointmentStream()
                .filter(timeSlot -> predicate.test(timeSlot))
                .forEach(list::add);
        return list;
    }

    @Override
    public List<TimeSlot> getGapsFitting(Duration duration) {
        return getGapsFitting(duration, gapStream());
    }

    @Override
    public List<TimeSlot> getGapsFittingReversed(Duration duration) {
        return getGapsFitting(duration, gapStreamReversed());
    }

    private List<TimeSlot> getGapsFitting(Duration duration, Stream<TimeSlot> timeSlotStream) {
        var timeSlotList = new ArrayList();
        timeSlotStream
                .filter(timeSlot -> {
                    var timeSlotDuration = Duration.between(timeSlot.getStart(), timeSlot.getEnd());
                    if (!timeSlotDuration.minus(duration).isNegative()) {
                        return true;
                    }
                    return false;
                })
                .forEach(timeSlotList::add);

        return timeSlotList;
    }

    @Override
    public boolean canAddAppointmentOfDuration(Duration duration) {
        return getGapsFitting(duration).size() > 0;
    }

    @Override
    public List<TimeSlot> getGapsFittingSmallestFirst(Duration duration) {
        var gapList = getGapsFitting(duration);
        Collections.sort(gapList, Comparator.comparing(TimeSlot::duration));
        return gapList;
    }

    @Override
    public List<TimeSlot> getGapsFittingLargestFirst(Duration duration) {
        var gapList = getGapsFitting(duration);
        Collections.sort(gapList, Comparator.comparing(TimeSlot::duration));
        Collections.reverse(gapList);
        return gapList;
    }

    @Override
    public List<TimeSlot> getMatchingFreeSlotsOfDuration(Duration minLength, List<Timeline> otherList) {
        List<TimeSlot> returnList = new ArrayList<>();
        //Map timelines and timeline gaps
        Map<Timeline, List<TimeSlot>> timeLineGapList = new HashMap();
        for (var timeline : otherList) {
            timeLineGapList.put(timeline, timeline.getGapsFitting(minLength));
        }
        timeLineGapList.put(this, this.getGapsFitting(minLength));

        while (true) {
            //If any otherList is empty, stop
            if (anyListEmpty(timeLineGapList)) {
                break;
            }

            Instant startingEdge = startingEdge(timeLineGapList);

            if (!allViableStartingEdge(timeLineGapList, startingEdge)) {
                continue;
            }

            Instant endingEdge = endingEdge(timeLineGapList);

            allViableEndingEdge(timeLineGapList, startingEdge, endingEdge, minLength, returnList);
        }

        return returnList;
    }

    private boolean anyListEmpty(Map<Timeline, List<TimeSlot>> timeLineGapList) {
        for (var timeSlotList : timeLineGapList.values()) {
            if (timeSlotList.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Instant startingEdge(Map<Timeline, List<TimeSlot>> timeLineGapList) {
        var currentStartingEdge = timeLineGapList.get(this).get(0).getStart();
        Instant currentTimeSlotStartInstant;

        for (var timeSlotList : timeLineGapList.values()) {
            currentTimeSlotStartInstant = timeSlotList.get(0).getStart();
            if (currentTimeSlotStartInstant.isAfter(currentStartingEdge)) {
                currentStartingEdge = currentTimeSlotStartInstant;
            }
        }

        return currentStartingEdge;
    }

    private boolean allViableStartingEdge(Map<Timeline, List<TimeSlot>> timeLineGapList, Instant startingEdge) {
        var returnValue = true;
        Instant currentTimeSlotEndInstant;
        for (var timeSlotList : timeLineGapList.values()) {
            currentTimeSlotEndInstant = timeSlotList.get(0).getEnd();
            if (currentTimeSlotEndInstant.isBefore(startingEdge) || currentTimeSlotEndInstant.equals(startingEdge)) {
                timeSlotList.remove(0);
                returnValue = false;
            }
        }
        return returnValue;
    }

    private Instant endingEdge(Map<Timeline, List<TimeSlot>> timeLineGapList) {
        var currentEndingEdge = timeLineGapList.get(this).get(0).getEnd();
        Instant currentTimeSlotEndInstant;

        for (var timeSlotList : timeLineGapList.values()) {
            currentTimeSlotEndInstant = timeSlotList.get(0).getEnd();
            if (currentTimeSlotEndInstant.isBefore(currentEndingEdge)) {
                currentEndingEdge = currentTimeSlotEndInstant;
            }
        }

        return currentEndingEdge;
    }

    private void allViableEndingEdge(Map<Timeline, List<TimeSlot>> timeLineGapList, Instant startingEdge, Instant endingEdge, Duration duration, List<TimeSlot> returnList) {
        var factory = new APFactory();
        var startEndEdgeTimeSlot = factory.between(startingEdge, endingEdge);

        Instant currentTimeSlotEndInstant;

        for (var timeSlotList : timeLineGapList.values()) {
            currentTimeSlotEndInstant = timeSlotList.get(0).getEnd();
            if (currentTimeSlotEndInstant.isBefore(endingEdge) || currentTimeSlotEndInstant.equals(endingEdge)) {
                timeSlotList.remove(0);
            }
        }

        if (startEndEdgeTimeSlot.fits(duration)) {
            returnList.add(startEndEdgeTimeSlot);
        }
    }

    @Override
    public Stream<Appointment> appointmentStream() {
        return appointments.stream()
                .filter(timeSlot -> (timeSlot instanceof Appointment))
                .map(Appointment.class::cast);
    }

    public Stream<TimeSlot> gapStream() {
        return appointments.stream()
                .filter(timeSlot -> (!(timeSlot instanceof Appointment)));
    }

    public Stream<TimeSlot> gapStreamReversed() {
        return appointments.reverseStream()
                .filter(timeSlot -> (!(timeSlot instanceof Appointment)));
    }

    public Stream<TimeSlot> wholeStream() {
        return this.appointments.stream();
    }

    @Override
    public boolean contains(Appointment appointment) {
        return this.appointmentStream()
                .anyMatch((ap -> ap.equals(appointment)));
    }

    public DoublyLinkedList getList() {
        return this.appointments;
    }
}