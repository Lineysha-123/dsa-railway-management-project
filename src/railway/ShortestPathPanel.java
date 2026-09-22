package railway;

import javax.swing.*;

// ShortestPathPanel.java
// Feature 10: BFS -- finds the route with the FEWEST station hops.
public class ShortestPathPanel extends BasePanel implements Refreshable {

	private JComboBox<String> startBox, endBox;

	public ShortestPathPanel(RailwayService service) {
		super(service, "Shortest Path (BFS)", "Find the route with the fewest station hops between two stations");

		startBox = RailwayTheme.createComboBox(new String[0]);
		endBox = RailwayTheme.createComboBox(new String[0]);
		addField("Starting Station", startBox);
		addField("Destination Station", endBox);

		JButton findBtn = RailwayTheme.createPrimaryButton("Find Route (BFS)");
		addActionButton(findBtn);

		findBtn.addActionListener(e -> {
			if (startBox.getSelectedItem() == null || endBox.getSelectedItem() == null) {
				printResult("Error: No stations available yet.");
				return;
			}
			printResult(service.findShortestHopPath((String) startBox.getSelectedItem(), (String) endBox.getSelectedItem()));
		});
	}

	@Override
	public void refreshData() {
		startBox.removeAllItems();
		endBox.removeAllItems();
		for (String s : service.getStationNames()) { startBox.addItem(s); endBox.addItem(s); }
	}
}
