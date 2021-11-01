package appointmentplannerimpl;

import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;

public class AppointmentImpl implements Appointment {

    private AppointmentRequestImpl ar;
    private TimeSlot timeSlot;

    public AppointmentImpl(AppointmentRequestImpl ar, TimeSlot timeSlot) throws IllegalArgumentException {
        if (ar == null){
            throw new IllegalArgumentException("The request cannot be null!");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("The time slot cannot be null!");
        }
        this.timeSlot = timeSlot;
        this.ar = ar;
    }

    @Override
    public Duration getDuration() {
        return this.ar.getDuration();
    }

    @Override
    public String getDescription() {
        return this.ar.getDescription();
    }

    @Override
    public Priority getPriority() {
        return this.ar.getPriority();
    }

    @Override
    public AppointmentData getAppointmentData() {
        return this.ar.getAppointmentData();
    }

    @Override
    public AppointmentRequest getRequest() {
        return this.ar;
    }

    @Override
    public Instant getStart() {
        return LocalDay.now().ofLocalTime(this.ar.getStartTime());
    }

    @Override
    public Instant getEnd() {
        return LocalDay.now().ofLocalTime(this.ar.getStartTime().plus(this.ar.getDuration()));
    }

    @Override
    public String toString() {
        return "Priority:" + getPriority();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentImpl that = (AppointmentImpl) o;
        return ar.equals(that.ar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ar);
    }
}
