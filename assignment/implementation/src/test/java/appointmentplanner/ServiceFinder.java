package appointmentplanner;

import appointmentplanner.api.AbstractAPFactory;
import java.util.Optional;
import java.util.ServiceLoader;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 *
 * @author Pieter van den Hombergh {@code p.vandenhombergh@fontys.nl}
 */
public class ServiceFinder {

    /**
     * Try to find the factory provided by this module. Fails with
     * AssumptionViolated exception to disable the test that uses or relies on
     * the factory when it is not (yet) available.
     *
     * @return an AbstractAPFactory implementing object
     * @throws AssumptionViolatedException when no service is not found.
     */
    static AbstractAPFactory getFactory() {
        Optional<AbstractAPFactory> firstFound = ServiceLoader.load( AbstractAPFactory.class ).findFirst();
        assumeThat( firstFound ).as( "Factory service not found" ).isNotEmpty();
        return firstFound.get();
    }
}
