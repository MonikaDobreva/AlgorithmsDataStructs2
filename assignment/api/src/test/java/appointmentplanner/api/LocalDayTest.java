package appointmentplanner.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public class LocalDayTest {

    //@Disabled("Think TDD")
    @ParameterizedTest
    @CsvSource( {
        "UTC,2020-06-17,09:00",
        "Europe/Amsterdam,2020-12-11,11:00",
        "America/New_York,1998-09-11,14:00"
    } )
    public void localDate( String zoneName, LocalDate ld, LocalTime lt ) {

        ZoneId myZone = ZoneId.of( zoneName );
        LocalDay day = new LocalDay( myZone, ld );
        Instant in = day.ofLocalTime( lt );
        LocalTime y = day.timeOfInstant( in );
        assertThat( y ).isEqualTo( lt );

//        fail( "method method reached end. You know what to do." );
    }

}
