package gitlet;
import org.junit.Before;
import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;

/** Warnings. */
@SuppressWarnings("unchecked")

/** The suite of all JUnit tests for the gitlet package.
 *  @author Olivia Kim
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    Gitlet g = new Gitlet();

    @Before
    public void base() {
        File f = new File(".gitlet/");
        if (!f.exists()) {
            g.init();
        }
    }

    @Test
    public void testInit() {
        File a = new File(".gitlet/");
        assertTrue(a.exists());
    }
}


