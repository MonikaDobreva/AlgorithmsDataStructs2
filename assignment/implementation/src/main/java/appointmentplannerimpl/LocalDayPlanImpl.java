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
    private Timeline timeline;

    public LocalDayPlanImpl(LocalDay day, Instant start, Instant end) throws IllegalArgumentException {
        if (end.isBefore(start)){
            throw new IllegalArgumentException("The end must be after the start!");
        }
        this.timeline = new TimelineImpl(start, end);
        this.day = day;
    }

    public LocalDayPlanImpl(ZoneId zoneID, LocalDate date, Timeline timeline){
        this.day = new LocalDay(zoneID, date);
        this.timeline = timeline;
    }

    @Override
    public LocalDay getDay() {
        return this.day;
    }

    @Override
    public Instant earliest() {
        return this.timeline.start();
    }

    @Override
    public Instant tooLate() {
        return this.timeline.end();
    }

    @Override
    public Timeline getTimeline() {
        return this.timeline;
    }
}
