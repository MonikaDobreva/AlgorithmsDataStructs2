package appointmentplanner;

import org.assertj.core.api.SoftAssertions;
import static org.assertj.core.api.Assertions.assertThat;

public class Tools {

    @SafeVarargs
    public static <T> void testEqualsAndHashcode(T first, T same, T... notSame) {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(first.equals(first)).isTrue();
            softly.assertThat(first.equals(null)).isFalse();
            softly.assertThat(first.equals("something")).isFalse();
            softly.assertThat(first.equals(same)).isTrue();

            for (var i = 0; i < notSame.length; i++) {
                T nSame = notSame[i];
                softly.assertThat(first).isNotEqualTo(nSame);
            }
            assertThat(first.hashCode()).isEqualTo(same.hashCode());
        });
    }

}
