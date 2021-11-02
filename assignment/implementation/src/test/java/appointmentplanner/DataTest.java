package appointmentplanner;

import appointmentplanner.api.Priority;
import appointmentplannerimpl.AppointmentDataImpl;
import org.junit.jupiter.api.Test;
import static appointmentplanner.Tools.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DataTest {
    private Duration duration = Duration.of(2, ChronoUnit.HOURS);
    private AppointmentDataImpl ad = new AppointmentDataImpl("interview", duration);
    private Duration dur = Duration.ofMinutes(45);

    private AppointmentDataImpl first = new AppointmentDataImpl("testing", dur, Priority.HIGH);
    private AppointmentDataImpl same = new AppointmentDataImpl("testing", dur, Priority.HIGH);
    private AppointmentDataImpl theDuration = new AppointmentDataImpl("testing", Duration.ofSeconds(1200), Priority.HIGH);
    private AppointmentDataImpl description = new AppointmentDataImpl("fishing",dur, Priority.HIGH);
    private AppointmentDataImpl priority = new AppointmentDataImpl("testing", dur, Priority.LOW);

    @Test
    public void getDescriptionTest(){
        assertThat(this.ad.getDescription())
                .isEqualTo("interview");

    }

    @Test
    public void getPriorityTest(){
        assertThat(this.ad.getPriority())
                .isEqualTo(Priority.LOW);
    }

    @Test
    public void getDurationTest(){
        assertThat(this.ad.getDuration())
                .isEqualTo(this.duration);
    }

    @Test
    public void toStringTest(){
        assertThat(this.ad.toString())
                .isEqualTo("duration = " + this.duration +
                        ", priority = " + Priority.LOW +
                        ", description = interview");
    }

    @Test
    public void EqualsAndHashCodeTest() {
        appointmentplanner.Tools.testEqualsAndHashcode(this.first, this.same, this.theDuration, this.description, this.priority);
    }

}
