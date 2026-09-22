package railway;

import javax.swing.*;

// RailwayManagementSystem.java
// Entry point. All business logic lives in RailwayService; all UI wiring
// lives in MainFrame and the individual panel classes. This class only
// starts the application on the Swing Event Dispatch Thread.
public class RailwayManagementSystem {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
			// fall back to the default cross-platform look if the system L&F is unavailable
		}
		SwingUtilities.invokeLater(() -> {
			MainFrame frame = new MainFrame();
			frame.setVisible(true);
		});
	}
}
