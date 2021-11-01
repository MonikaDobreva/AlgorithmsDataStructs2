package appointmentplannerimpl;

import appointmentplanner.api.AppointmentData;
import appointmentplanner.api.AppointmentRequest;
import appointmentplanner.api.Priority;
import appointmentplanner.api.TimePreference;

import java.time.LocalTime;
import java.util.Objects;

public class AppointmentRequestImpl implements AppointmentRequest {
    private LocalTime prefStart;
    private TimePreference fallback;
    private AppointmentData appointmentData;


    public AppointmentRequestImpl(AppointmentData appointmentData, LocalTime prefStart, TimePreference fallBack) throws IllegalArgumentException {
        if (appointmentData == null){
            throw new IllegalArgumentException("There must be an appointment for the request!");
        }
        //usage of default TimePreference
        if (fallBack == null){
            fallBack = TimePreference.UNSPECIFIED;
        }
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

    @Override
    public TimePreference getTimePreference(){
        return this.fallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentRequestImpl that = (AppointmentRequestImpl) o;
        return appointmentData.equals(that.appointmentData) && prefStart.equals(that.prefStart) && fallback == that.fallback;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentData, prefStart, fallback);
    }
}
