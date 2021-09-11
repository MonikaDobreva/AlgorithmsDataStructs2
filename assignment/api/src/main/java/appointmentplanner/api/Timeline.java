/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University of Applied Science Venlo
 */
package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Timeline of planned appointments.
 *
 * <ul>
 * <li>The implementation of the Timeline must use your own linked list
 * implementation for it's internal structure. Existing implementations or
 * arrays/Lists are not allowed.</li>
 * <li>However, in the methods returning lists, collections, etc produced by
 * streams, the usual java classes of such kind are allowed. No need to reinvent
 * all wheels
 * </ul>
 *
 * @author Richard van den Ham {@code r.vandenham@fontys.nl}
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 *
 */
public interface Timeline {

    // <editor-fold defaultstate="expanded" desc="Get Timeline characteristics">
    /**
     * Returns the number of appointments on a day.
     *
     * @return Number of appointments on this timeline.
     */
    int getNrOfAppointments();

    /**
     * Get the start of this timeline as instant.
     *
     * @return the starting instant.
     */
    Instant start();

    /**
     * Get the end of this timeline.
     *
     * @return the end
     */
    Instant end();

    /**
     * Adds a new appointment to this day.Requirements:
     * <ul>
     * <li>If the appointmentData is null, an NullPointerException is
     * thrown.</li>
     * <li>An appointment can only be added between start time (including) and
     * end time (excluding) of the day.
     *
     * AppointmentData having a duration greater than the length of the day in
     * minutes will result in null to be returned.</li>
     * <li>If the day does not have free space to accommodate the appointment,
     * null is returned.</li>
     * <li>Appointments aren't allowed to overlap.</li>
     * </ul>
     *
     * @param forDay time partition to fit appointment
     * @param appointment to add
     * @param timepreference or null if not applicable.
     * @return Appointment instance with all fields set according to
     * AppointmentData, or null if the constraints of this day and the requested
     * appointment can't be met.
     */
    Optional<Appointment> addAppointment( LocalDay forDay, AppointmentData appointment, TimePreference timepreference );

    /**
     * Add appointment with a fixed time.If the requested slot is available,
     * that is used and the appointment is returned. Otherwise null is returned.
     *
     * @param forDay time partition to fit appointment
     * @param appointment to add
     * @param startTime preferred start time of the appointment
     * @return the added appointment or null on failure.
     */
    Optional<Appointment> addAppointment( LocalDay forDay, AppointmentData appointment, LocalTime startTime );

    /**
     * Create an appointment based on previous
     *
     * @param forDay time partition to fit appointment
     * @param appointmentRequest for this appointment.
     * @return the added appointment.
     */
    default Optional<Appointment> addAppointment( LocalDay forDay,
            AppointmentRequest appointmentRequest ) {
        return addAppointment( forDay, appointmentRequest.getAppointmentData(),
                appointmentRequest.getStartTime() );
    }

    /**
     * Add appointment with a fixed time. If the requested slot is available,
     * that is used and the appointment is returned. Otherwise the fall back
     * time preference is tried.
     *
     * @param forDay time partition to fit appointment
     * @param appointment to add
     * @param startTime preferred start time of the appointment
     * @param fallback time preference as fall back if the fixed time does not
     * apply.
     * @return the added appointment or null on failure.
     */
    Optional<Appointment> addAppointment( LocalDay forDay, AppointmentData appointment,
            LocalTime startTime, TimePreference fallback );

    /**
     * Removes the given appointment, returning the data of that appointment, if
     * found. This day is searched for a non-free time slot matching the
     * appointment. The returned data could be used to re-plan the appointment.
     *
     * @param appointment to remove
     * @return the data of the removed appointment and null if the appointment
     * is not found..
     */
    AppointmentRequest removeAppointment( Appointment appointment );

    /**
     * Removes appointments with description that matches a filter.
     *
     * @param filter to determine which items to remove.
     * @return the list of removed appointments
     */
    List<AppointmentRequest> removeAppointments( Predicate<Appointment> filter );

    /**
     * Finds all appointments matching given filter.
     *
     * @param filter to determine which items to select.
     * @return list of matching appointments.
     */
    List<Appointment> findAppointments( Predicate<Appointment> filter );

    /**
     * Finds all appointments for this day.
     *
     * @return list of all appointments.
     */
    default List<Appointment> getAppointments() {
        return appointmentStream().collect( Collectors.toList() );
    }

    /**
     * All appointments streamed.
     *
     * @return list of all appointments.
     */
    Stream<Appointment> appointmentStream();

    /**
     * Check if day contains appointment.
     *
     * @param appointment to search.
     * @return true if Appointment can be found, false otherwise.
     */
    boolean contains( Appointment appointment );

    // </editor-fold>
    /**
     * This method finds all time gaps that can accommodate an appointment of
     * the given duration in natural order. This method returns the gaps in
     * ascending time which is the natural order.
     *
     * @param duration the requested duration for an appointment
     *
     * @return a list of gaps in which the appointment can be scheduled.
     */
    List<TimeSlot> getGapsFitting( Duration duration );

    /**
     * Check if an appointment of a duration could be accommodated.
     *
     * @param duration of the appointment
     * @return true is there is a sufficiently big gap.
     */
    boolean canAddAppointmentOfDuration( Duration duration );

    /**
     * This method finds all time gaps that can accommodate an appointment of
     * the given duration in last to first order. This method returns the gaps
     * in descending time which is the reversed natural order.
     *
     * @param duration the requested duration for an appointment
     * @return a list of start times on which an appointment can be scheduled
     */
    List<TimeSlot> getGapsFittingReversed( Duration duration );

    /**
     * Get the gaps matching the given duration, smallest fitting first.
     *
     * @param duration required
     * @return list of all gaps fitting, smallest gap first.
     */
    List<TimeSlot> getGapsFittingSmallestFirst( Duration duration );

    /**
     * Get the gaps matching the given duration, largest fitting first.
     *
     * @param duration required
     * @return list of all gaps fitting, largest gap first.
     */
    List<TimeSlot> getGapsFittingLargestFirst( Duration duration );

    /**
     * Find matching free time slots in this and other DayaPlans.To facilitate
     * appointment proposals.
     *
     * @param minLength minimum length required.
     * @param other day plans
     * @return the list of free slots that all DayPlans share.
     */
    List<TimeSlot> getMatchingFreeSlotsOfDuration( Duration minLength, List<Timeline> other );
}
