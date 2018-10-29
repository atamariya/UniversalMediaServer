package net.pms.util;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.sun.jna.Platform;

/**
 * Utility class containing static methods for the GUI
 */
public class GuiUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuiUtil.class);
	private static boolean lookAndFeelInitialized = false;
	
	/**
	 * Private Constructor to avoid instantiating this class.
	 */
	private GuiUtil() { }

	/**
	 * Initialize look and feel according to the OS.
	 */
	public static void initializeLookAndFeel() {
		if (lookAndFeelInitialized) {
			return;
		}

		LookAndFeel selectedLaf = null;
		if (Platform.isWindows()) {
			try {
				selectedLaf = (LookAndFeel) Class.forName("com.jgoodies.looks.windows.WindowsLookAndFeel").newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				selectedLaf = new PlasticLookAndFeel();
			}
		} else if (System.getProperty("nativelook") == null && !Platform.isMac()) {
			selectedLaf = new PlasticLookAndFeel();
		} else {
			try {
				String systemClassName = UIManager.getSystemLookAndFeelClassName();
				// Workaround for Gnome
				try {
					String gtkLAF = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
					Class.forName(gtkLAF);

					if (systemClassName.equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
						systemClassName = gtkLAF;
					}
				} catch (ClassNotFoundException ce) {
					LOGGER.error("Error loading GTK look and feel: ", ce);
				}

				LOGGER.trace("Choosing Java look and feel: " + systemClassName);
				UIManager.setLookAndFeel(systemClassName);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
				selectedLaf = new PlasticLookAndFeel();
				LOGGER.error("Error while setting native look and feel: ", e1);
			}
		}

		if (selectedLaf instanceof PlasticLookAndFeel) {
			PlasticLookAndFeel.setPlasticTheme(PlasticLookAndFeel.createMyDefaultTheme());
			PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_DEFAULT_VALUE);
			PlasticLookAndFeel.setHighContrastFocusColorsEnabled(false);
		} else if (selectedLaf != null && selectedLaf.getClass() == MetalLookAndFeel.class) {
			MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
		}

		// Work around caching in MetalRadioButtonUI
		JRadioButton radio = new JRadioButton();
		radio.getUI().uninstallUI(radio);
		JCheckBox checkBox = new JCheckBox();
		checkBox.getUI().uninstallUI(checkBox);

		if (selectedLaf != null) {
			try {
				UIManager.setLookAndFeel(selectedLaf);
			} catch (UnsupportedLookAndFeelException e) {
				LOGGER.warn("Can't change look and feel", e);
			}
		}

		lookAndFeelInitialized = true;
	}

}
