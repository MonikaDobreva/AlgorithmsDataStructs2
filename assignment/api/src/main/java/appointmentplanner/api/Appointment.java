/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University of Applied Science Venlo
 */
package appointmentplanner.api;

/**
 * Appointment that can be scheduled using the appointment planner.
 *
 * An appointment is a tupple of appointment data, day and start time.
 *
 * Having an appointment implies that there has been a successful allocation on
 * a day at a time. The appointment is only valid when it is allocated on some
 * time line.
 *
 * Hints to the implementer:
 * <ul><li>You should override toString(). toString() returns startTime,
 * endTime, description and priority like: "2019-09-12 14:00 - 15:55 ALDA Lesson
 * (HIGH)". This will make your testing and debugging live so much easier.
 * </li>
 * </ul>
 *
 * @author Richard van den Ham {@code r.vandenham@fontys.nl}
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface Appointment extends TimeSlot, AppointmentData {

    /**
     * Get the priority of this appointment.
     *
     * @return Priority.
     */
    @Override
    Priority getPriority();

    /**
     * Get the appointment data for this appointment.
     *
     * @return the data
     */
    AppointmentData getAppointmentData();

    /**
     * Get the request that led to this appointment.
     *
     * @return the request.
     */
    AppointmentRequest getRequest();
}
