module appointmentplanner {
    requires appointmenplanner.api;
    uses appointmentplanner.api.AbstractAPFactory; // self use in tests
    provides appointmentplanner.api.AbstractAPFactory with appointmentplanner.APFactory;
}
