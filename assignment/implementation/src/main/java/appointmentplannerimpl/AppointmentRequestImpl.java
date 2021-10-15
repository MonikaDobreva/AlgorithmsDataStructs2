package appointmentplannerimpl;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;

import java.time.LocalTime;

public class AppointmentRequestImpl implements AppointmentRequest {
    private LocalTime prefStart;
    private TimePreference fallback;
    private AppointmentData appointmentData;


    public AppointmentRequestImpl(AppointmentData appointmentData, LocalTime prefStart, TimePreference fallBack) {
        this.appointmentData = appointmentData;
        this.prefStart = prefStart;
        this.fallback = fallBack;
    }

    @Override
    public String getDescription() {
        return this.appointmentData.getDescription();
    }

    @Override
    public Priority getPriority() {
        return this.appointmentData.getPriority();
    }

    @Override
    public LocalTime getStartTime() {
        return this.prefStart;
    }

    @Override
    public AppointmentData getAppointmentData() {
        return this.appointmentData;
    }
}
