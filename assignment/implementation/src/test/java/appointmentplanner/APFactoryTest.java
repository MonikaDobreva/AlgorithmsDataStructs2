package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.AppointmentDataImpl;
import appointmentplannerimpl.AppointmentRequestImpl;
import appointmentplannerimpl.LocalDayPlanImpl;
import org.junit.jupiter.api.Test;
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

    private ZoneId zone = ZoneId.systemDefault();
    private LocalDate date = LocalDate.now().plusWeeks(2);
    // private Timeline timeline = ;
    @Test
    public void createLocalDayPlanTest (){
        //assertThat(apf.createLocalDayPlan())

    }

    @Test
    public void createLocalDayPlanTest2() {
        //assertThat(apf.createLocalDayPlan())

    }

    @Test
    public void createAppointmentDataTest() {
        //assertThat(apf.createAppointmentData())

    }

    @Test
    public void createAppointmentDataTest2() {
        //assertThat(apf.createAppointmentData())

    }

    @Test
    public void createAppointmentRequestTest() {
        //assertThat(apf.createAppointmentRequest())

    }

    @Test
    public void between(){

    }
}
