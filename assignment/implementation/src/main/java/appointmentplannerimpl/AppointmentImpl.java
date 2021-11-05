package appointmentplannerimpl;

import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;

public class AppointmentImpl implements Appointment {

    private AppointmentRequest ar;
    private TimeSlot timeSlot;
    private AppointmentData ad;

    public AppointmentImpl(AppointmentData appointmentData, AppointmentRequest ar, TimeSlot timeSlot) throws IllegalArgumentException {
        if (ad == null) {
            throw new IllegalArgumentException("The data cannot be null!");
        }
        if (ar == null){
            throw new IllegalArgumentException("The request cannot be null!");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("The time slot cannot be null!");
        }
        this.timeSlot = timeSlot;
        this.ar = ar;
        this.ad = appointmentData;
    }

    @Override
    public Duration getDuration() {
        return this.ad.getDuration();
    }

    @Override
    public String getDescription() {
        return this.ad.getDescription();
    }

    @Override
    public Priority getPriority() {
        return this.ad.getPriority();
    }

    @Override
    public AppointmentData getAppointmentData() {
        return this.ad;
    }

    @Override
    public AppointmentRequest getRequest() {
        return this.ar;
    }

    @Override
    public Instant getStart() {
        return this.timeSlot.getStart();
    }

    @Override
    public Instant getEnd() {
        return this.timeSlot.getEnd();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentImpl that = (AppointmentImpl) o;
        return ad.equals(that.ad) && ar.equals(that.ar) && timeSlot.equals(that.timeSlot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ad, ar, timeSlot);
    }
}
