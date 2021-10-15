package appointmentplanner;

import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import appointmentplannerimpl.AppointmentDataImpl;
import appointmentplannerimpl.AppointmentRequestImpl;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestImplTest {
    private Duration duration = Duration.of(2, ChronoUnit.HOURS);
    private TimePreference timePreference = TimePreference.EARLIEST;
    private LocalTime time = LocalTime.of(13,45);
    private AppointmentDataImpl ad = new AppointmentDataImpl("interview", duration);
    private AppointmentRequestImpl ar = new AppointmentRequestImpl(ad, time, timePreference);

    @Test
    public void getDescriptionTest(){
        assertThat(ar.getDescription())
                .isEqualTo("interview");

    }

    @Test
    public void getPriorityTest(){
        assertThat(ar.getPriority())
                .isEqualTo(Priority.LOW);
    }

    @Test
    public void getStartTest(){
        assertThat(ar.getStartTime())
                .isEqualTo(time);
    }

    @Test
    public void getAppDataTest(){
        assertThat(ar.getAppointmentData())
                .isEqualTo(ad);
    }

    @Test
    public void toStringTest(){

    }


}
