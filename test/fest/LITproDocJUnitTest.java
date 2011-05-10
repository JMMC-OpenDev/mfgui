/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LITproDocJUnitTest.java,v 1.3 2011-04-07 15:36:42 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2011/04/06 14:11:38  mella
 * Fix demo loading and save screenshot with LITpro prefix.. C'est vraiment top!!!
 *
 * Revision 1.1  2011/04/05 08:34:31  mella
 * First implementatio of basic tests
 *
 *
 * Copied from AsproDocJUnitTest/bourges
 */
package fest;

import static org.fest.swing.core.matcher.JButtonMatcher.*;
import static org.fest.swing.timing.Pause.*;

import fest.common.JmcsApplicationSetup;

import fest.common.JmcsFestSwingJUnitTestCase;
import fr.jmmc.mcs.timer.TimerFactory;
import fr.jmmc.mcs.timer.TimerFactory.UNIT;

import java.awt.Frame;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.fest.assertions.Fail;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.timing.Condition;
import org.fest.swing.timing.Timeout;

import org.junit.Test;



/**
 * This simple tests takes screenshots to complete the Aspro2 documentation
 * 
 * @author bourgesl
 */
public final class LITproDocJUnitTest extends JmcsFestSwingJUnitTestCase {

    /** queries to perform */
    private static final int QUERY_ITERATIONS = 20;

    /** 60s timeout */
    private static final Timeout LONG_TIMEOUT = Timeout.timeout(60 * 1000l);    
    /** time to wait between queries (ms) */
    private static final long QUERY_PAUSE = 1 * 200l;

    /**
     * Define the application
     */
    static {
        // disable dev LAF menu :
        System.setProperty("jmcs.laf.menu", "false");

        JmcsApplicationSetup.define(fr.jmmc.mf.gui.ModelFitting.class);

        // define robot delays :
        defineRobotDelayBetweenEvents(SHORT_DELAY);

        // define delay before taking screenshot :
        defineScreenshotDelay(SHORT_DELAY);

        // disable tooltips :
        enableTooltips(false);
    }

    /**
     * Test if the application can Load first demo settings
     */
    @Test
    @GUITest
    public void shouldLoadDemo() {
        window.menuItemWithPath("File", "Open demo settings", "Tutorial example 1: angular diameter of a single star").click();
    }

    /**
     * Test if the application can make N queries correctly
     *
     * Sans compression de fichier cote client
     * LABO
     * Timer [LITpro (ms) - ms] [20] (threshold = 5000.0 ms) {
     *  Low  : Timer [LITpro (ms) - ms] [20] {num = 20 : min = 1479.47662, avg = 1749.61253, max = 3491.18581, acc = 34992.2506, std = 280.24085 [16] std low  = 395.698 [13] std high = 0.0 [3] }
     *  High : Timer [LITpro (ms) - ms] [0]
     * }
     * ADSL2:
     * Timer [LITpro (ms) - ms] [20] (threshold = 5000.0 ms) {
  Low  : Timer [LITpro (ms) - ms] [20] {num = 20 : min = 2352.07077, avg = 2610.93381, max = 4081.17988, acc = 52218.67628, std = 202.38237 [16] std low  = 305.54489 [12] std high = 0.0 [4] }
  High : Timer [LITpro (ms) - ms] [0]
}
     * ADSL2 et reponse serveur gzippee:
     * Timer [LITpro (ms) - ms] [20] (threshold = 5000.0 ms) {
  Low  : Timer [LITpro (ms) - ms] [20] {num = 20 : min = 2293.26749, avg = 2648.40666, max = 4076.51545, acc = 52968.1333, std = 329.80932 [16] std low  = 301.387 [12] std high = 0.0 [4] }
  High : Timer [LITpro (ms) - ms] [0]
}
}
     *
     * Avec compression de fichier
     * LABO
     * Timer [LITpro (ms) - ms] [20] (threshold = 5000.0 ms) {
     *  Low  : Timer [LITpro (ms) - ms] [20] {num = 20 : min = 1478.70491, avg = 1735.373, max = 3291.45839, acc = 34707.46006, std = 225.31403 [16] std low  = 340.36363 [12] std high = 0.0 [4] }
     *  High : Timer [LITpro (ms) - ms] [0]
     *}
     * ADSL2
     * Timer [LITpro (ms) - ms] [20] (threshold = 5000.0 ms) {
  Low  : Timer [LITpro (ms) - ms] [20] {num = 20 : min = 1730.28448, avg = 1972.07167, max = 3535.25745, acc = 39441.43348, std = 222.11101 [16] std low  = 310.72906 [13] std high = 0.0 [3] }
  High : Timer [LITpro (ms) - ms] [0]
}
     * ADSL2  et reponse serveur gzippee:
     * Timer [LITpro (ms) - ms] [20] (threshold = 5000.0 ms) {
  Low  : Timer [LITpro (ms) - ms] [20] {num = 20 : min = 1684.15672, avg = 2033.1817, max = 3558.05825, acc = 40663.63419, std = 470.95373 [16] std low  = 378.57893 [10] std high = 0.0 [6] }
  High : Timer [LITpro (ms) - ms] [0]
}
     */
    @Test
    @GUITest
    public void shouldNQuery() {
        window.requireVisible();

        final String buttonText = "Run fit";

        final JButtonFixture buttonFixture = window.button(withText(buttonText));

        long start;
        int nStart = 0;
        try {

            for (int i = 0; i < QUERY_ITERATIONS; i++) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("start query : " + i);
                }
                start = System.nanoTime();

                window.show();
                // click to start SearchCal query:
                buttonFixture.click();

                // Start done:
                nStart++;
                pauseMedium();
                if (closeMessage()) {
                    Fail.fail("An error occured while running query [" + nStart + "] !");
                }
                final JButton button = buttonFixture.component();

                pause(new Condition("LITproServerRunning") {
                    /**
                     * Checks if the condition has been satisfied.
                     * @return <code>true</code> if the condition has been satisfied, otherwise <code>false</code>.
                     */
                    public boolean test() {
                        return GuiActionRunner.execute(new GuiQuery<Boolean>() {
                            protected Boolean executeInEDT() {                               
                                return button.isEnabled();
                            }
                        });
                    }
                }, LONG_TIMEOUT);

                TimerFactory.getTimer("LITpro (ms)", UNIT.ms, 5000l).addMilliSeconds(start, System.nanoTime());

                if (logger.isLoggable(Level.INFO)) {
                    logger.info("pause (ms) : " + QUERY_PAUSE);
                }

                pause(QUERY_PAUSE);
            }
        } finally {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Queries started: " + nStart);
            }

            if (!TimerFactory.isEmpty()) {
                logger.warning("TimerFactory : statistics : " + TimerFactory.dumpTimers());
            }
        }
    }

  /**
   * Test the application exit sequence : ALWAYS THE LAST TEST
   */
  @Test
  @GUITest
  public void shouldExit() {
    logger.severe("shouldExit test");

    window.close();

    confirmDialogDontSave();
  }

  /*
  --- Utility methods  ---------------------------------------------------------
   */

}
