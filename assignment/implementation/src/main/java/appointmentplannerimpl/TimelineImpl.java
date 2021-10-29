package appointmentplannerimpl;

import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TimelineImpl implements Timeline {
    private LinkedListMine<TimeslotImpl> list;

    public TimelineImpl() {
        this.list = new LinkedListMine<>();
    }

    public LinkedListMine list(){
        return this.list;
    }

    @Override
    public int getNrOfAppointments() {
        return 0;
    }

    @Override
    public Instant start() {
        return this.list.head.t.getStart();
    }

    @Override
    public Instant end() {
        return this.list.tail.t.getEnd();
    }

    @Override
    public Optional<Appointment> addAppointment(LocalDay forDay, AppointmentData appointment, TimePreference timepreference) {
        if (appointment == null){
            throw new NullPointerException("Appointment Data is empty!");
        }
        if (appointment.getDuration().toMinutes() > 1440){
            return null;
        }
        if (this.list.getSize()<=1){
            return AppointmentImpl
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
        return null;
    }

    @Override
    public boolean contains(Appointment appointment) {
        return false;
    }

    @Override
    public List<TimeSlot> getGapsFitting(Duration duration) {
        return null;
    }

    @Override
    public boolean canAddAppointmentOfDuration(Duration duration) {
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
