package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.LinkedListBackwardsIterator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class BackwardsIteratorTest {
    @Mock
    TimeSlot first, second, third, forth;

    private DoublyLinkedList<TimeSlot> list;
    private LinkedListBackwardsIterator iterator;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        this.list = new DoublyLinkedList<TimeSlot>();
        this.list.addFront(this.forth);
        this.list.addFront(this.third);
        this.list.addFront(this.second);
        this.list.addFront(this.first);

        this.iterator = new LinkedListBackwardsIterator(this.list.getHead(), this.list.getTail());
    }

    @Test
    public void hasNextTest() {
        assertThat(this.iterator.hasNext()).isTrue();
    }

    @Test
    public void hasNextTest2() {
        this.iterator.next();
        this.iterator.next();
        this.iterator.next();
        this.iterator.next();
        assertThat(this.iterator.hasNext()).isFalse();
    }

    @Test
    public void nextTest() {
        var node = (DoublyLinkedList.Node) this.iterator.next();
        var otherNode = (DoublyLinkedList.Node) this.iterator.next();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(node.getItem()).isEqualTo(this.forth);
            softly.assertThat(otherNode.getItem()).isEqualTo(this.third);
        });
    }
}
