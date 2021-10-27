package appointmentplannerimpl;

import appointmentplanner.api.TimeSlot;

import java.time.Duration;
import java.time.Instant;

public class TimeslotImpl implements TimeSlot {
    private Instant start;
    private Instant end;

    public TimeslotImpl(Instant start, Instant end) {
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
}
