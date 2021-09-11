package appointmentplanner.api;

import java.time.Duration;

/**
 * Payload of an Appointment, also the reason to have one.
 * 
 * The data include a description and the expected duration. Think
 * of a lesson taking 45 minutes.
 * 
 * Another example is having a treatment at a dentist or a beauty parlor. The duration, priority,  and
 * description is known, but there is no time or date allocated yet.
 * 
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface AppointmentData {
    /**
     * An appointment always has some no-zero length.
     * @return the duration of the appointment.
     */
    Duration getDuration();
    /**
     * There is also a non-empty description.
     * @return non empty string describing the appointment.
     */
    String getDescription();
    
    /**
     * Get the priority for the appointment.
     * @return the priority
     */
    Priority getPriority();
}
