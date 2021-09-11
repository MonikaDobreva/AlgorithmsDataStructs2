package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

/**
 * Value object to return the full details of a request for an appointment.
 *
 * Note that an appointment request is NOT an appointment but only an expression
 * of intent. The use case for the AppointmentRequest is re-planning, that is remove an appointment, put another in and reapply the request.
 * 
 * To be able to remember the possible combinations, either start or time preference may be null at construction time.
 * 
 *
 * @author Pieter van den Hombergh {@code p.vandehombergh@fontysvenlo.org}
 */
public interface AppointmentRequest extends AppointmentData {

    /**
     * Get the start time of the intended appointment.
     * If the time is not specified, this method may return null.
     * @param onDay the day the time is on.
     * @return the start time as instant, potentially null.
     */
    default Instant getStart( LocalDay onDay ) {
        return onDay.ofLocalTime( getStartTime() );
    }

    /**
     * Get the requested local start time.
     *
     * @return the start time
     */
    LocalTime getStartTime();

    /**
     * Get the appointment details of this appointment.
     *
     * @return the data
     */
    AppointmentData getAppointmentData();

    /**
     * Time preference given with this appointment request. The default is
     * UNKNOWN as in unspecified.
     *
     * @return the time preference
     */
    default TimePreference getTimePreference() {
        return TimePreference.UNSPECIFIED;
    }

    @Override
    default Duration getDuration() {
        return getAppointmentData().getDuration();
    }
}
