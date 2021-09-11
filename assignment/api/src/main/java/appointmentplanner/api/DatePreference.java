/*
 * Copyright (c) 2019 Informatics Fontys FHTenL University of Applied Science Venlo.
 */
package appointmentplanner.api;

/**
 * Strategy to find a Time slot to add an Appointment.
 * 
 * @author Richard van den Ham {@code r.vandenham@fontys.nl}
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public enum DatePreference {
    UNSPECIFIED,
    /**
     * Only valid in combination with a time.
     */
    DATE_BEFORE, 
    /**
     * Only valid with a time.
     */
    DATE_AFTER;
}
