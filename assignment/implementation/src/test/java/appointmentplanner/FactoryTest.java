package appointmentplanner;

import appointmentplanner.ServiceFinder;
import appointmentplanner.api.AbstractAPFactory;
import appointmentplanner.api.LocalDay;
import appointmentplanner.api.LocalDayPlan;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Example service invocation and factory test.
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public class FactoryTest {

    static AbstractAPFactory fac;

    @BeforeAll
    static void assumeFactory() {
        fac = ServiceFinder.getFactory();
    }

    @Test
    void factoryCreatesDayPlan() {
        LocalDay day = LocalDay.now();
        LocalDayPlan ldp = fac.createLocalDayPlan( day, LocalTime.parse( "08:00" ), LocalTime.parse( "17:30" ) );
        assertThat( ldp ).as( fac.getClass().getName() + " should not return null object" ).isNotNull();
    }

}
