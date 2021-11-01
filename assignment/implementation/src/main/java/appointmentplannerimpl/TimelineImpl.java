package appointmentplannerimpl;

import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                return Optional.of(new AppointmentImpl(ar));
            } else if (timepreference == TimePreference.LATEST){
                startApp = end().minusSeconds(appointment.getDuration().toSeconds());
                TimeslotImpl t = new TimeslotImpl(startApp, end());
                this.list.addNode(t);
                LocalTime startAr = LocalTime.ofInstant(startApp, forDay.getZone());
                AppointmentRequestImpl ar = new AppointmentRequestImpl(appointment, startAr, timepreference);
                return Optional.of(new AppointmentImpl(ar));
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
        return null;
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
}
