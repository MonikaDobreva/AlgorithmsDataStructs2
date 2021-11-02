package appointmentplanner;

import appointmentplanner.api.Priority;
import appointmentplannerimpl.AppointmentDataImpl;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DataTest {
    private Duration duration = Duration.of(2, ChronoUnit.HOURS);
    private AppointmentDataImpl ad = new AppointmentDataImpl("interview", duration);

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

}
