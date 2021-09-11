package appointmentplanner.api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;

/**
 * Timezone and day based view on timeline. This is the classic view on a day
 * plan or calender in which entries are added and viewed in local time.
 *
 * A LocalDayPlan has two local time boundaries for allowed times, start time
 * and end time, which default to 0:00 (inclusive) and 24:00 exclusive.
 *
 * The implementer should provide a constructor to provide other start and end
 * times.
 *
 *
 * The implementer should implement a toString that shows all appointments based
 * on local time. It is left to the discretion of the implementer to also show
 * the gaps.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public interface LocalDayPlan {

    /**
     * LocalDay specifies date and time zone.
     *
     * @return the day,
     */
    LocalDay getDay();

    /**
     * Start, inclusive.
     *
     * @return lower limit
     */
    Instant earliest();

    /**
     * End, exclusive.
     *
     * @return upper limit
     */
    Instant tooLate();

    /**
     * Get the time line to combine appointments in different time zones.
     *
     * @return the timeline kept by this local day plan
     */
    Timeline getTimeline();

    /**
     * Get the allowed first time for this day.
     *
     * @return the earliest time in this plan
     */
    default LocalTime getStartTime() {
        return getDay().timeOfInstant( earliest() );
    }

    /**
     * Get the allowed latest time for this day.
     *
     * @return that latest time in this plan
     */
    default LocalTime getEndTime() {
        return getDay().timeOfInstant( tooLate() );
    }

    /**
     * Add an appointment with start time and fallback time preference.
     *
     * @param appointmentData date
     * @param start           if not null, preferred start time
     * @param fallback        if start cannot be met, use pref for fallback
     * @return an optional of nullable, which is present when the appoint was
     *         allocated.
     */
    default Optional<Appointment> addAppointment( AppointmentData appointmentData, LocalTime start, TimePreference fallback ) {
        return getTimeline().addAppointment( getDay(), appointmentData, start, fallback );
    }

    /**
     * Add and appointment with fix start time. This request can fail (Optional
     * is not present) if the start time is not available.
     *
     * @param appointmentData data
     * @param startTime       fixed time
     * @return optional of nullable
     */
    default Optional<Appointment> addAppointment( AppointmentData appointmentData, LocalTime startTime ) {
        return getTimeline().addAppointment( getDay(), appointmentData, startTime );
    }

    /**
     * Add and appointment with time preference. This request can fail (Optional
     * is not present) if the data's duration does not fit in the (alread
     * planned day.
     *
     * @param appointmentData data
     * @param pref            time preference
     * @return optional of nullable
     */
    default Optional<Appointment> addAppointment( AppointmentData appointmentData, TimePreference pref ) {
        return getTimeline().addAppointment( getDay(), appointmentData, pref );
    }

    /**
     * Remove an appointment.
     *
     * @param appointment not null
     * @return the appointment request to optionally re-plan it.
     */
    default AppointmentRequest removeAppointment( Appointment appointment ) {
        return getTimeline().removeAppointment( appointment );
    }

    /**
     * Remove all appointments that meet a predicate.
     *
     * @param filter when to remove
     * @return a list of all AppointmentRequests that lead to the appointments.
     */
    default List<AppointmentRequest> removeAppointments( Predicate<Appointment> filter ) {
        return getTimeline().removeAppointments( filter );
    }

    /**
     * Get all the appointments of this LocalDayPlan
     *
     * @return the list of appointments
     */
    default List<Appointment> getAppointments() {
        return getTimeline().getAppointments();
    }

    /**
     * Find common gaps of some length between this and other day plans in
     * natural order. The
     * other day plans may be in different time zones, although
     *
     * @param dur        minimum duration of the slots
     * @param otherPlans that could have common gaps
     * @return the list of gaps this and each of ther other plans have with a
     *         minimum length of duration
     */
    default List<TimeSlot> getMatchingFreeSlotsOfDuration( Duration dur, List<LocalDayPlan> otherPlans ) {
        return getTimeline().getMatchingFreeSlotsOfDuration( dur, otherPlans.stream().map( LocalDayPlan::getTimeline ).collect( toList() ) );
    }

    /**
     * Find the gaps in this LocalDayPlan that meet or exceed the given
     * duration in natural start time order. Note that invoking this
     * method with Duration.ZERO should get all gaps.
     *
     * @param dur minimum
     *            duration of the gaps
     *
     * @return the list of gaps in this day meeting or exceeding the given
     *         duration
     */
    default List<TimeSlot> getGapsFitting( Duration dur ) {
        return getTimeline().getGapsFitting( dur );
    }

    /**
     * Find the gaps in this LocalDayPlan that meet or exceed the given
     * duration in reversed start time order. Note that invoking this
     * method with Duration.ZERO should get all gaps.
     *
     * @param dur minimum duration of the gaps
     * @return the list of gaps in this day meeting or exceeding the given
     *         duration
     */
    default List<TimeSlot> getGapsFittingReversed( Duration dur ) {
        return getTimeline().getGapsFittingReversed( dur );
    }

    /**
     * Get the gaps fitting the given duration, in descending duration order.
     * @param dur to meet
     * @return a list, non null
     */
    default List<TimeSlot> getGapsFittingLargestFirst( Duration dur ) {
        return getTimeline().getGapsFittingLargestFirst( dur );
    }

    /**
     * Get the gaps fitting the given duration, in ascending duration order.
     * @param dur to meet
     * @return a list, non null
     */
    default List<TimeSlot> getGapsFittingSmallestFirst( Duration dur ) {
        return getTimeline().getGapsFittingSmallestFirst( dur );
    }

    /**
     * Is there a gap thet meets the given duration.
     * 
     * @param dur to meet
     * @return true if there is such a gap.
     */
    default boolean canAddAppointmentOfDuration( Duration dur ) {
        return getTimeline().canAddAppointmentOfDuration( dur );
    }

    /**
     * Find the appointment meeting the predicate.
     * @param filter to test the appointments
     * @return a non null list.
     */
    default List<Appointment> findAppointments( Predicate<Appointment> filter ) {
        return getTimeline().findAppointments( filter );
    }

    /**
     * Return a string containing the local date, the time zone and all
     * appointments in natural order, presented with time as local time.
     *
     * @return a string representation of the local day plan.
     */
    @Override
    String toString();

    /**
     * Does his day plan contain the the given appointment
     * @param appointment to test
     * @return true if found
     */
    default boolean contains( Appointment appointment ) {
        return getTimeline().contains( appointment );
    }

    /**
     * What is the (start) date of this plan.
     * @return the date according to this' time zone 
     */
    default LocalDate getDate() {
        return getDay().getDate();
    }

    /**
     * Get the number of appointments of this day plan.
     * @return the count
     */
    default int getNrOfAppointments() {
        return getTimeline().getNrOfAppointments();
    }

    /**
     * Get the instant at the given hour and minute of this local day plan.
     * @param hour sic
     * @param minute sic
     * @return the point in time as Instant
     */
    default Instant at( int hour, int minute ) {
        return getDay().at( hour, minute );
    }
}
