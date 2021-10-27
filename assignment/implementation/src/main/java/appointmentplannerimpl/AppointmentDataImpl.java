package appointmentplannerimpl;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.Priority;

import java.time.Duration;

public class AppointmentDataImpl implements AppointmentData {
    private final Duration duration;
    private final Priority priority;
    private final String description;
    private static final Priority DEFAULT_PRIORITY = Priority.LOW;

    public AppointmentDataImpl(String description, Duration duration, Priority priority){
        this.description = description;
        this.duration = duration;
        this.priority = priority;
    }

    public AppointmentDataImpl(String description, Duration duration){
        this.duration = duration;
        this.description = description;
        this.priority = DEFAULT_PRIORITY;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public String toString() {
        return "duration = " + this.duration +
                ", priority = " + this.priority +
                ", description = " + this.description ;
    }
}
