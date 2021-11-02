package appointmentplanner;

import appointmentplannerimpl.TimeslotImpl;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

public class TimeslotImplTest {
    private Instant start = Instant.now().plusSeconds(36000);
    private Instant end = start.plusSeconds(3600);
    private TimeslotImpl timeSlot = new TimeslotImpl(start, end);

    @Test
    public void DurationTest(){
        assertThat(this.timeSlot.duration())
                .isEqualTo(Duration.ofMinutes(60));
    }

    @Test
    public void getStartTest() {
        assertThat(this.timeSlot.getStart())
                .isEqualTo(this.start);
    }

    @Test
    public void getEndTest() {
        assertThat(this.timeSlot.getEnd())
                .isEqualTo(this.end);
    }
}
