package appointmentplanner;

/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University ofLength Applied Science Venlo
 */
import appointmentplanner.api.AbstractAPFactory;
import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.LocalDayPlan;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import appointmentplanner.api.TimeSlot;
import appointmentplanner.api.Timeline;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Abstract factory to separate student implementations from teachers tests. The
 * instance created by this factory will be black-box tested by the teachers
 * tests.
 *
 * Richard van den Ham {@code r.vandenham@fontys.nl} Pieter van den Hombergh
 * {@code p.vandenhombergh@fontys.nl}
 */
public class APFactory implements AbstractAPFactory {

    @Override
    public LocalDayPlan createLocalDayPlan( ZoneId zone, LocalDate date, Timeline timeline ) {
        //TODO return proper instance
        return null;
    }

    @Override
    public LocalDayPlan createLocalDayPlan( LocalDay day, Instant start, Instant end ) {
        //TODO return proper instance
        return null;
    }

    public APFactory() {
    }

    @Override
    public AppointmentData createAppointmentData( String description, Duration duration ) {
        //TODO return proper instance
        return null;
    }

    @Override
    public AppointmentData createAppointmentData( String description, Duration duration, Priority priority ) {
        //TODO return proper instance
        return null;
    }

    @Override
    public AppointmentRequest createAppointmentRequest( AppointmentData appData, LocalTime prefStart, TimePreference fallBack ) {
        //TODO return proper instance
        return null;
    }

    @Override
    public TimeSlot between( Instant start, Instant end ) {
        //TODO return proper instance
        return null;
    }
}
