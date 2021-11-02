package appointmentplanner;

import appointmentplanner.api.LocalDay;
import appointmentplanner.api.Timeline;
import appointmentplannerimpl.TimelineImpl;
import appointmentplannerimpl.LocalDayPlanImpl;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class LocalDayPlanImplTest {
    @Test
    public void constructorAndGettersTest() {
        var timeline = mock(Timeline.class);
        var localDate = LocalDate.now();
        var zoneId = mock(ZoneId.class);
        var localDay = new LocalDay(zoneId, localDate);
        var localDayPlan = new LocalDayPlanImpl(zoneId, localDate, timeline);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(localDayPlan.getTimeline()).isEqualTo(timeline);
            softly.assertThat(localDayPlan.getDay()).isEqualTo(localDay);
        });
    }

    @Test
    public void constructorWithTimeTest() {
        var day = LocalDay.now();
        var start = day.ofLocalTime(LocalTime.parse("01:00"));
        var end = day.ofLocalTime(LocalTime.parse("02:00"));
        var timeline = new TimelineImpl(start, end);
        var ldp = new LocalDayPlanImpl(day, start, end);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(ldp.getDay()).isEqualTo(day);
            softly.assertThat(ldp.earliest()).isEqualTo(start);
            softly.assertThat(ldp.tooLate()).isEqualTo(end);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "00:01, 00:00"
    })
    public void ExceptionTest(String start, String end) {
        var day = LocalDay.now();
        Instant s = day.ofLocalTime(LocalTime.parse(start));
        Instant e = day.ofLocalTime(LocalTime.parse(end));

        ThrowableAssert.ThrowingCallable exceptionCode = () -> {
            new LocalDayPlanImpl(
                    day,
                    s,
                    e
            );
        };
        assertThatCode(exceptionCode)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("The end must be after the start!");
    }
}
