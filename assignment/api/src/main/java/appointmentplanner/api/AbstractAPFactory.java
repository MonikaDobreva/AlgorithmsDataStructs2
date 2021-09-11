package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * This is the interface that the user (the teachers) of your implementation use
 * to obtain instances of the classes implementing the given interfaces.
 *
 * In a non modular system the the fully qualified name of the implementing
 * class must be saved in a file called
 * {@code src/main/resources/META-INF/services/appointmentplanner.api.AbstractAPFactory}.
 *
 * With a modular project the implementing class can be resolved if it is
 * declared in * a provides clause in the module-info file.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface AbstractAPFactory {

    /**
     * Factory method to create a day plan instance.
     *
     * @param day local day of this plan
     * @param startTime earliest time in plan
     * @param endTime no appointments end after this time
     * @return LocalDayPlan object.
     */
    default LocalDayPlan createLocalDayPlan( LocalDay day, LocalTime startTime, LocalTime endTime ) {
        return createLocalDayPlan( day, day.ofLocalTime( startTime ), day.ofLocalTime( endTime ) );
    }

    /**
     * Factory method to create a day plan instance.
     *
     * @param day local day of this plan
     * @param start earliest time in plan
     * @param end no appointments end after this time
     * @return LocalDayPlan object.
     */
    LocalDayPlan createLocalDayPlan( LocalDay day, Instant start, Instant end );

    /**
     * Factory method to create a day plan instance.Reuse the given boundaries
     * in a timeline for this day plan.
     *
     * @param zone of this view
     * @param date of this view
     * @param timeline to use
     * @return LocalDayPlan object.
     */
    LocalDayPlan createLocalDayPlan( ZoneId zone, LocalDate date, Timeline timeline );

    /**
     * Factory method to create an Appointment object without specified
     * startTime. The start time will be set as soon as the appointment is added
     * to the Day schedule.
     *
     * @param description of the appointment
     * @param duration of the appointment
     * @param priority of the appointment
     * @return Appointment object.
     */
    AppointmentData createAppointmentData( String description,
            Duration duration,
            Priority priority );

    /**
     * Factory method to create an Appointment object without specified
     * startTime.The start time will be set as soon as the appointment is added
     * to the Day schedule. The priority is set as LOW by default.
     *
     * @param description of the appointment
     * @param duration of the appointment
     * @return Appointment object.
     */
    default AppointmentData createAppointmentData( String description, Duration duration ) {
        return createAppointmentData( description, duration, Priority.LOW );
    }

    /**
     * Create an appointmentRequest.
     *
     * @param data of the request
     * @param startTime time of the request
     * @return the AppointmentRequest.
     */
    default AppointmentRequest createAppointmentRequest( AppointmentData data, LocalTime startTime ) {
        return createAppointmentRequest( data, startTime, TimePreference.UNSPECIFIED );
    }

    /**
     * Create a request with time preference.
     *
     * @param appData for this request
     * @param pref for time, early or late
     * @return the request
     */
    default AppointmentRequest createAppointmentRequest( AppointmentData appData, TimePreference pref ) {
        return createAppointmentRequest( appData, null, pref );
    }

    /**
     * Create a request with a preferred start time and a fallback time
     * preference.
     *
     * @param appData data of the request
     * @param prefStart preferred start time
     * @param fallBack when time not available, start this
     * @return the request
     */
    AppointmentRequest createAppointmentRequest( AppointmentData appData, LocalTime prefStart, TimePreference fallBack );

    /**
     * Create a time slot between two times.
     *
     * @param start date+time
     * @param end date+time
     * @return the time slot
     */
    TimeSlot between( Instant start, Instant end );

    /**
     * Compute time slot on a date between given start and end time.
     *
     * If endTime is less than or equal to start time, it is assumed that the
     * end time is on the next day.
     *
     * @param day local day with time zone and date
     * @param startTime time
     * @param endTime time
     * @return the slot
     */
    default TimeSlot between( LocalDay day, LocalTime startTime, LocalTime endTime ) {
        boolean endBeforeOrAtStart = endTime.compareTo( startTime ) <= 0;
        Instant start = day.ofLocalTime( startTime );
        Instant end;
        if ( endBeforeOrAtStart ) {
            end = day.plusDays( 1 ).ofLocalTime( endTime );
        } else {
            end = day.ofLocalTime( endTime );
        }
        return between( start, end );
    }
}
