package appointmentplannerimpl;

import appointmentplanner.api.LocalDay;
import appointmentplanner.api.LocalDayPlan;
import appointmentplanner.api.Timeline;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class LocalDayPlanImpl implements LocalDayPlan {
    private LocalDay day;
    private Instant start;
    private Instant end;
    private TimelineImpl timeline;
    private Instant defaultStart = LocalDay.now().ofLocalTime(LocalTime.of(0, 0));
    private Instant defaultEnd = LocalDay.now().ofLocalTime(LocalTime.of(23, 59, 59));

    public LocalDayPlanImpl(LocalDay day, Instant start, Instant end) {
        this.day = day;
        this.start = start;
        this.end = end;
        this.timeline = new TimelineImpl(start, end);
    }

    public LocalDayPlanImpl(LocalDay day) {
        this.day = day;
        this.start = this.defaultStart;
        this.end = this.defaultEnd;
        this.timeline = new TimelineImpl(start, end);
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
