package appointmentplanner;

import appointmentplanner.api.*;
import appointmentplannerimpl.AppointmentDataImpl;
import appointmentplannerimpl.AppointmentImpl;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

public class AppointmentTest {
    @Test
    public void testConstructorCorrectValues() {
        AppointmentData mockedData = mock(AppointmentData.class);
        AppointmentRequest mockedRequest = mock(AppointmentRequest.class);
        TimeSlot mockedSlot = mock(TimeSlot.class);

        when(mockedSlot.getStart()).thenReturn(Instant.now());
        when(mockedSlot.getEnd()).thenReturn(Instant.now());

        Appointment appointment = new AppointmentImpl(
                mockedData, mockedRequest, mockedSlot
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(appointment.getAppointmentData()).isSameAs(mockedData);
            softly.assertThat(appointment.getRequest()).isSameAs(mockedRequest);
            softly.assertThat(appointment.getStart()).isEqualTo(mockedSlot.getStart());
            softly.assertThat(appointment.getEnd()).isEqualTo(mockedSlot.getEnd());
        });
    }

    @ParameterizedTest
    @CsvSource({
            "true, false, false",
            "false, true, false",
            "false, false, true"
    })
    public void constructorThrowsIAException(boolean dataNull, boolean requestNull, boolean slotNull) {
        AppointmentData mockedData;
        AppointmentRequest mockedRequest;
        TimeSlot mockedSlot;

        if (dataNull) mockedData = null;
        else mockedData = mock(AppointmentData.class);

        if (requestNull) mockedRequest = null;
        else mockedRequest = mock(AppointmentRequest.class);

        if (slotNull) mockedSlot = null;
        else mockedSlot = mock(TimeSlot.class);

        ThrowableAssert.ThrowingCallable constructorCall =
                () -> new AppointmentImpl(mockedData, mockedRequest, mockedSlot);

        assertThatCode(constructorCall)
                .hasMessage("Null values are not being accepted!")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAppointmentDataGetters() {
        AppointmentData mockedData = mock(AppointmentData.class);
        AppointmentRequest mockedRequest = mock(AppointmentRequest.class);
        TimeSlot mockedSlot = mock(TimeSlot.class);

        when(mockedData.getDuration()).thenReturn(Duration.ofMinutes(10));
        when(mockedData.getPriority()).thenReturn(Priority.LOW);
        when(mockedData.getDescription()).thenReturn("mock Description");

        Appointment appointment = new AppointmentImpl(
                mockedData, mockedRequest, mockedSlot
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(appointment.getPriority()).isEqualTo(mockedData.getPriority());
            softly.assertThat(appointment.getDescription()).isEqualTo(mockedData.getDescription());
            softly.assertThat(appointment.getDuration()).isEqualTo(mockedData.getDuration());
        });
    }
}


