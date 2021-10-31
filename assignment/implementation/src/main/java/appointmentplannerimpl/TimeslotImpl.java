package appointmentplannerimpl;

import appointmentplanner.api.TimeSlot;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class TimeslotImpl implements TimeSlot {
    private Instant start;
    private Instant end;

    public TimeslotImpl(Instant start, Instant end) throws IllegalArgumentException {
        if (start == null){
            throw new IllegalArgumentException("The start cannot be null!");
        }
        if (end == null){
            throw new IllegalArgumentException("The end cannot be null!");
        }
        if (end.isBefore(start)){
            throw new IllegalArgumentException("The star must be before the end!");
        }
        this.start = start;
        this.end = end;
    }

    public Duration duration() {
        return Duration.between(start, end);
    }

    @Override
    public Instant getStart() {
        return this.start;
    }

    @Override
    public Instant getEnd() {
        return this.end;
    }

    @Override
    public String toString() {
        return "Start: " + this.start + ", end: " + this.end + ", duration: " + duration() + ".";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeslotImpl timeslot = (TimeslotImpl) o;
        return start.equals(timeslot.start) && end.equals(timeslot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
