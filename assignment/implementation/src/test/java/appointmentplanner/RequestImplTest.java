package appointmentplanner;

import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;
import appointmentplannerimpl.AppointmentDataImpl;
import appointmentplannerimpl.AppointmentRequestImpl;
import static appointmentplanner.Tools.*;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class RequestImplTest {
    private Duration duration = Duration.of(2, ChronoUnit.HOURS);
    private TimePreference timePreference = TimePreference.EARLIEST;
    private LocalTime time = LocalTime.of(13,45);
    private AppointmentDataImpl ad = new AppointmentDataImpl("interview", duration);
    private AppointmentDataImpl adi = new AppointmentDataImpl("question", duration);
    private AppointmentRequestImpl first = new AppointmentRequestImpl(ad, time, timePreference);
    private AppointmentRequestImpl same = new AppointmentRequestImpl(ad, time, timePreference);
    private AppointmentRequestImpl theData = new AppointmentRequestImpl(adi, time, timePreference);
    private AppointmentRequestImpl theTime = new AppointmentRequestImpl(ad, LocalTime.now(), timePreference);
    private AppointmentRequestImpl timePref = new AppointmentRequestImpl(ad, time, TimePreference.EARLIEST_AFTER);

    @Test
    public void getDescriptionTest(){
        assertThat(this.first.getDescription())
                .isEqualTo("interview");

    }

    @Test
    public void getPriorityTest(){
        assertThat(this.first.getPriority())
                .isEqualTo(Priority.LOW);
    }

    @Test
    public void getStartTest(){
        assertThat(this.first.getStartTime())
                .isEqualTo(this.time);
    }

    @Test
    public void getAppDataTest(){
        assertThat(this.first.getAppointmentData())
                .isEqualTo(this.ad);
    }

    @Test
    public void toStringTest(){

    }

    @Test
    public void EqualsAndHashCodeTest() {
        appointmentplanner.Tools.testEqualsAndHashcode(this.first, this.same, this.theData, this.theTime, this.timePref);
    }

    @Test
    public void ExceptionTest() {
        ThrowableAssert.ThrowingCallable constructor = () -> {
            new AppointmentRequestImpl(null, this.time, this.timePreference);
        };
        assertThatCode(constructor)
                .hasMessage("There must be an appointment for the request!")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }


}
