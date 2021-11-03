package appointmentplannerimpl;

import appointmentplanner.APFactory;
import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
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
        if (appointment == null){
            throw new NullPointerException("Appointment Data is empty! line 54");
        }
        if (appointment.getDuration().toMinutes() > 1440){
            return Optional.empty();
        }
        if (this.list.getSize() == 0){
            Instant endApp;
            Instant startApp;
            if (timepreference == TimePreference.EARLIEST){
                endApp = start().plusSeconds(appointment.getDuration().toSeconds());
                TimeslotImpl t = new TimeslotImpl(start(), endApp);
                this.list.addNode(t);
                LocalTime startAr = LocalTime.ofInstant(start(), forDay.getZone());
                AppointmentRequestImpl ar = new AppointmentRequestImpl(appointment, startAr, timepreference);
                return Optional.of(new AppointmentImpl(ar, new TimeslotImpl(this.start, this.end)));
            } else if (timepreference == TimePreference.LATEST){
                startApp = end().minusSeconds(appointment.getDuration().toSeconds());
                TimeslotImpl t = new TimeslotImpl(startApp, end());
                this.list.addNode(t);
                LocalTime startAr = LocalTime.ofInstant(startApp, forDay.getZone());
                AppointmentRequestImpl ar = new AppointmentRequestImpl(appointment, startAr, timepreference);
                return Optional.of(new AppointmentImpl(ar, new TimeslotImpl(this.start, this.end)));
            }
            return Optional.empty();
        }
        if (this.list.getSize() > 0){
            Duration dur = Duration.between(this.start, this.end);
            Map<String, Optional<TimeSlot>> slotMap = new HashMap<>();

        }


        return Optional.empty();
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime) {
        return Optional.empty();
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, LocalTime startTime, TimePreference fallback) {
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

    @Override
    public List<TimeSlot> getGapsFitting(Duration duration) {
        return null;
    }

    @Override
    public boolean canAddAppointmentOfDuration(Duration duration) {
        Duration dur = Duration.between(this.start, this.end);

        return false;
    }

    @Override
    public List<TimeSlot> getGapsFittingReversed(Duration duration) {
        return null;
    }

    @Override
    public List<TimeSlot> getGapsFittingSmallestFirst(Duration duration) {
        return null;
    }

    @Override
    public List<TimeSlot> getGapsFittingLargestFirst(Duration duration) {
        return null;
    }

    @Override
    public List<TimeSlot> getMatchingFreeSlotsOfDuration(Duration minLength, List<Timeline> other) {
        return null;
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
}
