import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dokgo on 19.12.16.
 */
public class SplayTreeTest {
    private SplayTree st;

    @Before
    public void setUp() throws Exception {
        st = new SplayTree();
        st.insert(5);
        st.insert(9);
        st.insert(13);
        st.insert(11);
        st.insert(1);
        st.insert(0);

        System.out.printf("onSetUp: %s", st);
        System.out.println("\nTests: ");
    }

    @Test
    public void insert() throws Exception {
        assertEquals(6, st.size());
        st.insert(81);
        assertEquals(7, st.size());
        System.out.printf("remove 7: %s\n", st);
    }

    @Test
    public void remove() throws Exception {
        assertEquals(6, st.size());
        st.remove(0);
        assertEquals(5, st.size());
    }

    @Test
    public void contains() throws Exception {
        assertTrue(st.contains(11));
        assertFalse(st.contains(78));
    }

    @Test
    public void print() throws Exception {
        assertEquals(" 0 1 5 9 11 13", st.toString());
    }

    @Test
    public void splay() throws Exception {
        st.contains(5);
        assertEquals("5", st.getRoot().toString());
        st.contains(20);
        assertEquals("13", st.getRoot().toString());


    }

}