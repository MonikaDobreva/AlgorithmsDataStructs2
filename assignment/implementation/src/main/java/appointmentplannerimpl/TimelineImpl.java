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

public class TimelineImpl implements Timeline {
    private DoublyLinkedList<TimeSlot> list;
    private Instant start;
    private Instant end;

    public TimelineImpl(Instant start, Instant end) {
        this.start = start;
        this.end = end;
        this.list = new DoublyLinkedList<>();
    }

    public TimelineImpl(Instant start, Instant end, DoublyLinkedList<TimeSlot> list) {
        this.start = start;
        this.end = end;
        this.list = list;

        if (this.list.getSize() == 0) {
            var fac = new APFactory();
            this.list.toFront(fac.between(start, end));
        }
    }


    public DoublyLinkedList list(){
        return this.list;
    }

    @Override
    public int getNrOfAppointments(){
        int nrApp = (int) appointmentStream().count();
        return nrApp;
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
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, TimePreference timepreference) {
        return addAppointment(forDay, appointment, null, timepreference);
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime) {
        return addAppointment(forDay, appointment, startTime, null);
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime, TimePreference fallback) {
        if (appointment == null) {
            throw new NullPointerException("The appointment cannot be null");
        }
        var appointmentDuration = appointment.getDuration();
        Map<String, Optional<TimeSlot>> timeSlotOptionalMap = new HashMap();

        if (startTime != null) {
            timeSlotOptionalMap = this.findPreferredTimeSlot(appointmentDuration, startTime, forDay, fallback);
        }
        if (fallback == null) {
            fallback = TimePreference.UNSPECIFIED;
        }

        if (startTime == null || timeSlotOptionalMap == null) {
            if (fallback == TimePreference.LATEST) {
                timeSlotOptionalMap = findLastFittingTimeSlot(appointmentDuration);
            } else {
                timeSlotOptionalMap = findFirstFittingTimeSlot(appointmentDuration);
            }
        }

        var timeSlotMap = optionalToTimeSlot(timeSlotOptionalMap);
        if (timeSlotOptionalMap.containsKey("AppointmentSlot")) {
            if (timeSlotOptionalMap.get("AppointmentSlot").isEmpty() == false) {

                timeSlotMap.replace("AppointmentSlot", buildAppointment(
                        appointment,
                        LocalTime.ofInstant(timeSlotMap.get("AppointmentSlot").getStart(), forDay.getZone()),
                        fallback,
                        timeSlotOptionalMap.get("AppointmentSlot").get()).get());

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

    Map<String, Optional<TimeSlot>> preferredTimeSlot(Duration appointmentDuration, LocalTime preferredTime, LocalDay localDay, TimePreference timePreference) {
        List<TimeSlot> gapsFittingList = new ArrayList();

        this.getGapsFitting(appointmentDuration).stream()
                .filter(timeSlot -> timeSlot.fits(appointmentDuration))
                .forEach(gapsFittingList::add);

        var preferredSlot = gapsFittingList.stream()
                .filter(timeSlot -> {
                    var startTime = timeSlot.getStartTime(localDay);
                    var endTime = timeSlot.getEndTime(localDay);
                    var endTimeAppointment = preferredTime.plusMinutes(appointmentDuration.toMinutes());
                    return (startTime.isBefore(preferredTime) || startTime.equals(preferredTime)) &&
                            (endTime.isAfter(endTimeAppointment) || endTime.equals(endTimeAppointment));
                })
                .map(timeSlot -> new TimeslotImpl(localDay.ofLocalTime(preferredTime), localDay.ofLocalTime(preferredTime.plusMinutes(appointmentDuration.toMinutes()))))
                .map(TimeSlot.class::cast)
                .findAny();

        if (preferredSlot.isEmpty()) {
            if (timePreference == TimePreference.EARLIEST_AFTER) {
                preferredSlot = gapsFittingList.stream()
                        .filter(timeSlot ->
                                timeSlot.getEndTime(localDay).minusMinutes(appointmentDuration.toMinutes()).isAfter(preferredTime)
                        )
                        .map(timeSlot -> new TimeslotImpl(timeSlot.getStart(), timeSlot.getStart().plusSeconds(appointmentDuration.toSeconds())))
                        .map(TimeSlot.class::cast)
                        .findFirst();

            } else if (timePreference == TimePreference.LATEST_BEFORE) {
                preferredSlot = gapsFittingList.stream()
                        .filter(timeSlot ->
                                timeSlot.getStartTime(localDay).plusMinutes(appointmentDuration.toMinutes()).isBefore(preferredTime)
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

    private List<TimeSlot> getGapsFitting(Duration duration, Stream<TimeSlot> timeslotStream) {
        var timeslotList = new ArrayList();
        timeslotStream.filter(timeSlot -> {
                    var timeSlotDuration = Duration.between(timeSlot.getStart(), timeSlot.getEnd());
                    if (!timeSlotDuration.minus(duration).isNegative()) {
                        return true;
                    }
                    return false;
                })
                .forEach(timeslotList::add);

        return timeslotList;
    }

    @Override
    public AppointmentRequest removeAppointment(Appointment appointment) {
        return null;
    }

    @Override
    public List<AppointmentRequest> removeAppointments(Predicate<Appointment> filter) {
        return null;
    }

    @Override
    public List<Appointment> findAppointments(Predicate<Appointment> filter) {
        var list = new ArrayList();
        appointmentStream()
                .filter(timeSlot -> filter.test(timeSlot))
                .forEach(list::add);
        return list;
    }

    @Override
    public Stream<Appointment> appointmentStream() {
        return this.list.stream()
                .filter(timeSlot -> (timeSlot instanceof Appointment))
                .map(Appointment.class::cast);
    }

    @Override
    public boolean contains(Appointment appointment) {
        return this.appointmentStream()
                .anyMatch((appSearch -> appSearch.equals(appointment)));
    }

    public Stream<TimeSlot> gapStream() {
        return this.list.stream()
                .filter(timeSlot -> (!(timeSlot instanceof Appointment)));
    }

    @Override
    public List<TimeSlot> getGapsFitting(Duration duration) {
        return getGapsFitting(duration, gapStream());
    }

    @Override
    public boolean canAddAppointmentOfDuration(Duration duration) {
        Duration dur = Duration.between(this.start, this.end);

        return false;
    }

    public Stream<TimeSlot> gapStreamBackwards() {
        return this.list.streamBackwards()
                .filter(timeslot -> (!(timeslot instanceof Appointment)));
    }

    public Stream<TimeSlot> stream() {
        return this.list.stream();
    }

    @Override
    public List<TimeSlot> getGapsFittingReversed(Duration duration) {
        return getGapsFitting(duration, gapStreamBackwards());
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

    private boolean listEmpty(Map<Timeline, List<TimeSlot>> timeLineGapList) {
        for(var timeSlotList : timeLineGapList.values()) {
            if (timeSlotList.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Instant startingEdge(Map<Timeline, List<TimeSlot>> timeLineGapList) {
        var currentStartingEdge = timeLineGapList.get(this).get(0).getStart();
        Instant currentTimeSlotStartInstant;

        for(var timeSlotList : timeLineGapList.values()) {
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
        for(var timeSlotList : timeLineGapList.values()) {
            currentTimeSlotEndInstant = timeSlotList.get(0).getEnd();
            if (currentTimeSlotEndInstant.isBefore(startingEdge) || currentTimeSlotEndInstant.equals(startingEdge)) {
                timeSlotList.remove(0);
                returnValue =  false;
            }
        }
        return returnValue;
    }

    private Instant endingEdge(Map<Timeline, List<TimeSlot>> timeLineGapList) {
        var currentEndingEdge = timeLineGapList.get(this).get(0).getEnd();
        Instant currentTimeSlotEndInstant;

        for(var timeSlotList : timeLineGapList.values()) {
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

        for(var timeSlotList : timeLineGapList.values()) {
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
    public List<TimeSlot> getMatchingFreeSlotsOfDuration(Duration minLength, List<Timeline> other) {
        List<TimeSlot> returnList = new ArrayList<>();
        Map<Timeline, List<TimeSlot>> timeLineGapList = new HashMap();
        for(var timeline : other) {
            timeLineGapList.put(timeline, timeline.getGapsFitting(minLength));
        }
        timeLineGapList.put(this, this.getGapsFitting(minLength));

        while(true) {
            if (listEmpty(timeLineGapList)) {
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

    public void putAppointment(Map<String, TimeSlot> timeSlotMap) {
        var originalNode = this.list.lookForNode(timeSlotMap.get("OriginalTimeSlot"));
        var nextTimeSlot = timeSlotMap.get("NextTimeSlot");
        TimeSlot previousTimeSlot = null;
        if (timeSlotMap.containsKey("PreviousTimeSlot")) {
            previousTimeSlot = timeSlotMap.get("PreviousTimeSlot");
        }
        var appointment = timeSlotMap.get("AppointmentSlot");
        if (appointment != null) {
            if (nextTimeSlot == null) {
                if (previousTimeSlot != null) {
                    originalNode.setT(previousTimeSlot);
                    this.list.addToBack((Appointment) appointment, previousTimeSlot);
                    if (previousTimeSlot.getStart().equals(previousTimeSlot.getEnd())) {
                        var appointmentNode = this.list.lookForNode(appointment);
                        this.list.mergeNodesPrevious(appointmentNode, originalNode, appointment);
                    }
                } else {
                    originalNode.setT(appointment);
                }
            } else {
                originalNode.setT(nextTimeSlot);
                this.list.addInFront((Appointment) appointment, nextTimeSlot);
                if (previousTimeSlot != null) {
                    this.list.addInFront(previousTimeSlot, appointment);
                }
                var appointmentNode = this.list.lookForNode(appointment);
                if (nextTimeSlot.getStart().equals(nextTimeSlot.getEnd())) {
                    this.list.mergeNodesNext(appointmentNode, originalNode, appointment);
                }
            }
        }
    }

    private Optional<Appointment> buildAppointment(AppointmentData appointmentData, LocalTime localTime, TimePreference timePreference, TimeSlot timeSlot) {
        var factory = new APFactory();
        var appointmentRequest = factory.createAppointmentRequest(appointmentData, localTime, timePreference);
        var appointmentTimeSlot = timeSlot;

        return Optional.of(new AppointmentImpl(appointmentRequest, appointmentTimeSlot));
    }

    Map<String, Optional<TimeSlot>> findPreferredTimeSlot(Duration appointmentDuration, LocalTime preferredTime, LocalDay localDay, TimePreference timePreference) {
        List<TimeSlot> gapsFittingList = new ArrayList();

        this.getGapsFitting(appointmentDuration).stream()
                .filter(timeSlot -> timeSlot.fits(appointmentDuration))
                .forEach(gapsFittingList::add);

        var preferredSlot = gapsFittingList.stream()
                .filter(timeSlot -> {
                    var startTime = timeSlot.getStartTime(localDay);
                    var endTime = timeSlot.getEndTime(localDay);
                    var endTimeAppointment = preferredTime.plusMinutes(appointmentDuration.toMinutes());
                    return (startTime.isBefore(preferredTime) || startTime.equals(preferredTime)) &&
                            (endTime.isAfter(endTimeAppointment) || endTime.equals(endTimeAppointment));
                })
                .map(timeSlot -> new TimeslotImpl(localDay.ofLocalTime(preferredTime), localDay.ofLocalTime(preferredTime.plusMinutes(appointmentDuration.toMinutes()))))
                .map(TimeSlot.class::cast)
                .findAny();

        if (preferredSlot.isEmpty()) {
            if (timePreference == TimePreference.EARLIEST_AFTER) {
                preferredSlot = gapsFittingList.stream()
                        .filter(timeSlot ->
                                timeSlot.getEndTime(localDay).minusMinutes(appointmentDuration.toMinutes()).isAfter(preferredTime)
                        )
                        .map(timeSlot -> new TimeslotImpl(timeSlot.getStart(), timeSlot.getStart().plusSeconds(appointmentDuration.toSeconds())))
                        .map(TimeSlot.class::cast)
                        .findFirst();

            } else if (timePreference == TimePreference.LATEST_BEFORE) {
                preferredSlot = gapsFittingList.stream()
                        .filter(timeSlot ->
                                timeSlot.getStartTime(localDay).plusMinutes(appointmentDuration.toMinutes()).isBefore(preferredTime)
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
//            Duration appointmentDuration,
            List<TimeSlot> gapsFitting,
            Function<TimeSlot, TimeSlot> appointmentMapper,
            Function<TimeSlot, TimeSlot> timeSlotMapper,
            boolean last) {

        var returnMap = new HashMap();

        Optional<TimeSlot> appointmentSlot = gapsFitting.stream()
                .findFirst()
                .stream()
//                .map(timeSlot -> new TimeslotImpl(timeSlot.getStart(), timeSlot.getStart().plusSeconds(appointmentDuration.toSeconds())))
                .map(appointmentMapper)
                .map(TimeSlot.class::cast)
                .findFirst();

        returnMap.put("AppointmentSlot", appointmentSlot);

        Optional<TimeSlot> newTimeSlot = gapsFitting.stream()
                .findFirst()
                .stream()
//                .map(timeSlot -> new TimeslotImpl(appointmentSlot.get().getEnd(), timeSlot.getEnd()))
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
}
