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

    /** 60s timeout */
    private static final Timeout LONG_TIMEOUT = Timeout.timeout(60 * 1000l);
    /** queries to perform (500) */
    private static final int QUERY_ITERATIONS = 30;
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
     * Timer [SearchCal (ms) - ms] [500] (threshold = 5000.0 ms) {
     *   Low  : Timer [SearchCal (ms) - ms] [494] {num = 494 : min = 1057.43562, avg = 3775.94762, max = 4905.56433, acc = 1865318.12799, std = 227.17308 [490] std low  = 246.11642 [139] std high = 222.81639 [351] }
     *   High : Timer [SearchCal (ms) - ms] [6] {num = 6 : min = 5130.75824, avg = 7191.16121, max = 10874.424, acc = 43146.96727, std = 0.0 [2] std low  = 0.0 [2] std high = 0.0 [0] }
     * }
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
