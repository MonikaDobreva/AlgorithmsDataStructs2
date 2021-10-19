package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.AppointmentDataImpl;
import appointmentplannerimpl.AppointmentRequestImpl;
import appointmentplannerimpl.LocalDayPlanImpl;
import org.junit.jupiter.api.Test;

import java.time.*;

public class APFactoryTest {
    @Test
    public void createLocalDayPlanTest(ZoneId zone, LocalDate date, Timeline timeline ) {

    }

    @Test
    public void createLocalDayPlanTest2( LocalDay day, Instant start, Instant end ) {

    }

    @Test
    public void createAppointmentDataTest(String description, Duration duration ) {

    }

    @Test
    public void createAppointmentDataTest2( String description, Duration duration, Priority priority ) {

    }

    @Test
    public void createAppointmentRequestTest(AppointmentData appData, LocalTime prefStart, TimePreference fallBack ) {

    }

    @Test
    public void between( Instant start, Instant end ) {

    }
}
