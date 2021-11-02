package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.AppointmentDataImpl;
import appointmentplannerimpl.AppointmentRequestImpl;
import appointmentplannerimpl.LocalDayPlanImpl;
import appointmentplannerimpl.TimelineImpl;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.*;

public class APFactoryTest {
    //(ZoneId zone, LocalDate date, Timeline timeline )
    //( LocalDay day, Instant start, Instant end )
    //Instant start, Instant end )
    //(AppointmentData appData, LocalTime prefStart, TimePreference fallBack )
    //String description, Duration duration, Priority priority
    //String description, Duration duration )
    private APFactory apf = new APFactory();

    private Duration dur = Duration.ofMinutes(90);

    private ZoneId zone = ZoneId.systemDefault();
    private LocalDate date = LocalDate.now().plusWeeks(2);
    private Timeline timeline = mock(Timeline.class);
    private AppointmentDataImpl ad = new AppointmentDataImpl("fishing", Duration.ofMinutes(90), Priority.MEDIUM);
    private AppointmentDataImpl adi = new AppointmentDataImpl("dentist", Duration.ofHours(3), Priority.LOW);
    private AppointmentRequestImpl ar = new AppointmentRequestImpl(this.adi, LocalTime.now().plusHours(36), TimePreference.LATEST);
    private Instant s = Instant.now().plusSeconds(360000);
    private Instant e = s.plusSeconds(363600);

    @Test
    public void createLocalDayPlanTest (){
        assertThat(this.apf.createLocalDayPlan(this.zone, this.date, this.timeline))
                .isNotNull();
    }

    @Test
    public void createAppointmentDataTest() {
        assertThat(this.apf.createAppointmentData("fishing", this.dur, Priority.MEDIUM))
                .isEqualTo(this.ad);
    }

    @Test
    public void createAppointmentDataTest2() {
        assertThat(this.apf.createAppointmentData("dentist", Duration.ofHours(3)))
                .isEqualTo(this.adi);
    }

    @Test
    public void createAppointmentRequestTest() {
        assertThat(this.apf.createAppointmentRequest(this.adi, LocalTime.now().plusHours(36), TimePreference.LATEST))
                .isEqualTo(this.ar);
    }

    @Test
    public void betweenTest(){
        assertThat(this.apf.between(this.s, this.e).duration())
                .isEqualTo(Duration.ofHours(1));
    }
}
