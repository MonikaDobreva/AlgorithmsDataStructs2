package appointmentplanner;

import appointmentplanner.api.TimeSlot;
import appointmentplannerimpl.DoublyLinkedList;
import appointmentplannerimpl.LinkedListIterator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkedListIteratorTest {
    @Mock
    TimeSlot first, second, third, forth;

    private DoublyLinkedList<TimeSlot> list;
    private LinkedListIterator iterator;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        list = new DoublyLinkedList<TimeSlot>();
        this.list.toFront(this.forth);
        this.list.toFront(this.third);
        this.list.toFront(this.second);
        this.list.toFront(this.first);

        this.iterator = new LinkedListIterator(this.list.getHead(), this.list.getTail());
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
        var node = (DoublyLinkedList.AllocationNode) this.iterator.next();
        var otherNode = (DoublyLinkedList.AllocationNode) this.iterator.next();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(node.getT()).isEqualTo(this.first);
            softly.assertThat(otherNode.getT()).isEqualTo(this.second);
        });
    }
}
