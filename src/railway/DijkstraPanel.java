package railway;

import javax.swing.*;

// DijkstraPanel.java
// Feature 1: Dijkstra's Algorithm -- finds the route with the MINIMUM total distance (km).
public class DijkstraPanel extends BasePanel implements Refreshable {

	private JComboBox<String> startBox, endBox;

	public DijkstraPanel(RailwayService service) {
		super(service, "Minimum Distance Path (Dijkstra's Algorithm)", "Find the route with the lowest total distance between two stations");

		startBox = RailwayTheme.createComboBox(new String[0]);
		endBox = RailwayTheme.createComboBox(new String[0]);
		addField("Starting Station", startBox);
		addField("Destination Station", endBox);

		JButton findBtn = RailwayTheme.createPrimaryButton("Find Route (Dijkstra)");
		addActionButton(findBtn);

		findBtn.addActionListener(e -> {
			if (startBox.getSelectedItem() == null || endBox.getSelectedItem() == null) {
				printResult("Error: No stations available yet.");
				return;
			}
			printResult(service.findMinimumDistancePath((String) startBox.getSelectedItem(), (String) endBox.getSelectedItem()));
		});
	}

	@Override
	public void refreshData() {
		startBox.removeAllItems();
		endBox.removeAllItems();
		for (String s : service.getStationNames()) { startBox.addItem(s); endBox.addItem(s); }
	}
}
