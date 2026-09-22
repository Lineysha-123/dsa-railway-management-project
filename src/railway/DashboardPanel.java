package railway;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// DashboardPanel.java
public class DashboardPanel extends JPanel implements Refreshable {

	private final RailwayService service;
	private JLabel trainsValue, stationsValue, bookedValue, waitingValue;

	public DashboardPanel(RailwayService service) {
		this.service = service;
		setLayout(new BorderLayout());
		setBackground(RailwayTheme.CONTENT_BG);
		setBorder(new EmptyBorder(20, 24, 20, 24));

		JLabel title = RailwayTheme.createPanelTitle("Dashboard Overview");
		JLabel subtitle = new JLabel("Live snapshot of the railway network");
		subtitle.setFont(RailwayTheme.FONT_LABEL);
		subtitle.setForeground(RailwayTheme.TEXT_MUTED);
		subtitle.setBorder(new EmptyBorder(4, 0, 20, 0));

		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(RailwayTheme.CONTENT_BG);
		JPanel titleBox = new JPanel();
		titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
		titleBox.setBackground(RailwayTheme.CONTENT_BG);
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		titleBox.add(title);
		titleBox.add(subtitle);
		header.add(titleBox, BorderLayout.NORTH);

		JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
		cards.setBackground(RailwayTheme.CONTENT_BG);

		trainsValue = new JLabel("0");
		stationsValue = new JLabel("0");
		bookedValue = new JLabel("0");
		waitingValue = new JLabel("0");

		cards.add(statCard("🚆", "Active Trains", trainsValue, new Color(37, 99, 235)));
		cards.add(statCard("🚉", "Network Stations", stationsValue, new Color(16, 185, 129)));
		cards.add(statCard("🎫", "Tickets Booked", bookedValue, new Color(139, 92, 246)));
		cards.add(statCard("⏳", "Passengers Waiting", waitingValue, new Color(245, 158, 11)));

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(RailwayTheme.CONTENT_BG);
		top.add(header, BorderLayout.NORTH);
		top.add(cards, BorderLayout.CENTER);
		top.setBorder(new EmptyBorder(0, 0, 20, 0));

		JTextArea note = RailwayTheme.createOutputArea();
		note.setText(
			"========================================================================\n" +
			"        SMART RAILWAY MANAGEMENT & CONTROL-ROOM SYSTEM (v2.0)          \n" +
			"========================================================================\n\n" +
			"SYSTEM READY:\n" +
			" • Graph Network Engine: Operational (Dijkstra Min-Distance & BFS Fewest-Hops)\n" +
			" • Dynamic Fare Engine : Active (Class rates, km pricing & weekend surge)\n" +
			" • Seat Allocation     : Managed via Category Quotas & LIFO Recycling Stacks\n" +
			" • Waitlist Scheduler  : Prioritizing Senior Citizens (P1) > Ladies (P2) > General (P3)\n\n" +
			"QUICK NAVIGATION GUIDE:\n" +
			" 1. Passenger Services  -> Search seat availability, book journeys, cancel tickets\n" +
			" 2. Admin Control Panel -> Commission new trains, extend station routes, set timetables\n" +
			" 3. Fewest Stops (BFS)  -> Identify paths with minimum intermediate transit transfers\n" +
			" 4. Shortest Distance   -> Compute absolute shortest track route using Dijkstra's Algorithm\n" +
			" 5. Train Recommender   -> Multi-criteria optimal train comparison\n\n" +
			"Telemetry data refreshes automatically upon returning to this panel."
		);
		JPanel terminal = RailwayTheme.createTerminalPanel(note);

		add(top, BorderLayout.NORTH);
		add(terminal, BorderLayout.CENTER);

		refreshData();
	}

	private JPanel statCard(String icon, String label, JLabel valueLabel, Color accentColor) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(RailwayTheme.CARD_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(RailwayTheme.BORDER_COLOR, 1),
				new EmptyBorder(16, 18, 16, 18)
		));

		JPanel topRow = new JPanel(new BorderLayout());
		topRow.setBackground(RailwayTheme.CARD_BG);
		JLabel iconLabel = new JLabel(icon);
		iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

		JLabel l = new JLabel(label.toUpperCase());
		l.setFont(new Font("Segoe UI", Font.BOLD, 11));
		l.setForeground(RailwayTheme.TEXT_MUTED);
		topRow.add(iconLabel, BorderLayout.WEST);
		topRow.add(l, BorderLayout.EAST);

		valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
		valueLabel.setForeground(accentColor);
		valueLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

		card.add(topRow, BorderLayout.NORTH);
		card.add(valueLabel, BorderLayout.CENTER);
		return card;
	}

	@Override
	public void refreshData() {
		trainsValue.setText(String.valueOf(service.getTrainNumbers().length));
		stationsValue.setText(String.valueOf(service.getStationNames().length));
		String summary = service.getDashboardSummary();
		for (String line : summary.split("\n")) {
			if (line.startsWith("Tickets booked")) bookedValue.setText(line.split(":")[1].trim());
			if (line.startsWith("Passengers waiting")) waitingValue.setText(line.split(":")[1].trim());
		}
	}
}
