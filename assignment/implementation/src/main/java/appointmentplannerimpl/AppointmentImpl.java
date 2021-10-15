package appointmentplannerimpl;

import appointmentplanner.api.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class AppointmentImpl implements Appointment {









    @Override
    public Duration getDuration() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Priority getPriority() {
        return null;
    }

    @Override
    public AppointmentData getAppointmentData() {
        return this;
    }

    @Override
    public AppointmentRequest getRequest() {
        return null;
    }

    @Override
    public Instant getStart() {
//        LocalDateTime.of();
//        return ZonedDateTime.of(this.ar.getStartTime(), TimeZone.getTimeZone("WET").toZoneId()).toInstant();
        return null;
    }

    @Override
    public Instant getEnd() {
        return null;
    }

    //returns startTime,
    // * endTime, description and priority like: "2019-09-12 14:00 - 15:55 ALDA Lesson
    // * (HIGH)"

    @Override
    public String toString() {
//        return "AppointmentImpl{" +
//                "duration=" + duration +
//                ", priority=" + priority +
//                ", description='" + description + '\'' +
//                '}';
        return null;
    }
}
