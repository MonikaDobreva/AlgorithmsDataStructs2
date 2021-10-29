package appointmentplannerimpl;

import appointmentplanner.api.LocalDay;
import appointmentplanner.api.LocalDayPlan;
import appointmentplanner.api.Timeline;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDayPlanImpl implements LocalDayPlan {
    private LocalDay day;
    private Instant start;
    private Instant end;
    private TimelineImpl timeline;

    public LocalDayPlanImpl(LocalDay day, Instant start, Instant end) {
        this.day = day;
        this.start = start;
        this.end = end;
        this.timeline = new TimelineImpl();
        TimeslotImpl t = new TimeslotImpl(this.start, this.end);
        this.timeline.list().addNode(t);

    }

    @Override
    public LocalDay getDay() {
        return this.day;
    }

    @Override
    public Instant earliest() {
        if (this.start.isBefore(Instant.now())) {
            return Instant.now();
        }
        return this.start;
    }

    @Override
    public Instant tooLate() {
        return this.end.plusMillis(1);
    }

    @Override
    public Timeline getTimeline() {
        return this.timeline;
    }
}
