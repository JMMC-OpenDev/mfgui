/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: LITproDocJUnitTest.java,v 1.2 2011-04-06 14:11:38 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2011/04/05 08:34:31  mella
 * First implementatio of basic tests
 *
 *
 * Copied from AsproDocJUnitTest/bourges
 */
package fest;

import fest.common.JmcsApplicationSetup;

import fest.common.JmcsFestSwingJUnitTestCase;

import java.awt.Frame;
import javax.swing.JComponent;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.fixture.FrameFixture;

import org.junit.Test;

/**
 * This simple tests takes screenshots to complete the Aspro2 documentation
 * 
 * @author bourgesl
 */
public final class LITproDocJUnitTest extends JmcsFestSwingJUnitTestCase {

  /** name of the tab pane corresponding to the interferometer map */
  private static final String TAB_INTERFEROMETER_MAP = "Map";
  /** name of the tab pane corresponding to the observability panel */
  private static final String TAB_OBSERVABILITY = "Observability";
  /** name of the tab pane corresponding to the uv coverage panel */
  private static final String TAB_UV_COVERAGE = "UV coverage";

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
    window.menuItemWithPath("File", "Open demo settings","Tutorial example 1: angular diameter of a single star").click();
  }
  
  /**
   * Test Preferences
   */
  @Test
  @GUITest
  public void shouldOpenPreferences() {
    window.menuItemWithPath("Edit", "Preferences...").click();

    final Frame prefFrame = robot().finder().find(FrameMatcher.withTitle("Preferences"));

    if (prefFrame != null) {
      final FrameFixture frame = new FrameFixture(robot(), prefFrame);

      frame.requireVisible();
      frame.moveToFront();

      saveScreenshot(frame, "LITpro-prefs.png");

      // close frame :
      frame.close();
    }
  }

 

  /**
   * Capture a screenshot of the main form using the given file name
   * @param fileName the file name (including the png extension)
   */
  private void captureMainForm(final String fileName) {
    saveCroppedScreenshotOf(fileName, 0, 0, -1, getMainFormHeight(window));
  }

  /**
   * Determine the height of the main form
   * @param window window fixture
   * @return height of the main form
   */
  private static int getMainFormHeight(final FrameFixture window) {
    int height = 32 + 10;

    JComponent com;
    com = window.panel("observationForm").component();
    height += com.getHeight();

    com = window.menuItemWithPath("File").component();
    height += com.getHeight();

    return height;
  }
}
