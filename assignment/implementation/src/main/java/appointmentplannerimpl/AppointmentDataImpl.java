package appointmentplannerimpl;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.Priority;

import java.time.Duration;
import java.util.Objects;

public class AppointmentDataImpl implements AppointmentData {
    private final Duration duration;
    private final Priority priority;
    private final String description;
    private static final Priority DEFAULT_PRIORITY = Priority.LOW;

    public AppointmentDataImpl(String description, Duration duration, Priority priority) throws IllegalArgumentException {
        if (description == null || duration == null || priority == null){
            throw new IllegalArgumentException("The parameters cannot be null!");
        }
        if (duration.toSeconds() < 0){
            throw new IllegalArgumentException("The duration cannot be negative!");
        }
        this.description = description;
        this.duration = duration;
        this.priority = priority;
    }

    public AppointmentDataImpl(String description, Duration duration) {
        this(description, duration, Priority.LOW);
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
                ", description = " + this.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentDataImpl that = (AppointmentDataImpl) o;
        return duration.equals(that.duration) && priority == that.priority && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, priority, description);
    }
}
