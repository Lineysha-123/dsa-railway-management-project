package railway;

import javax.swing.*;

// RecommendationPanel.java
public class RecommendationPanel extends BasePanel implements Refreshable {

	private JComboBox<String> sourceBox, destinationBox, categoryBox;
	private JTextField dateField;

	public RecommendationPanel(RailwayService service) {
		super(service, "Train Recommendation System", "Compares every eligible train by distance, fare, stops, and departure time");

		sourceBox = RailwayTheme.createComboBox(new String[0]);
		destinationBox = RailwayTheme.createComboBox(new String[0]);
		categoryBox = RailwayTheme.createComboBox(new String[]{"AC", "SLPR", "GEN"});
		dateField = RailwayTheme.createTextField();
		dateField.setToolTipText("DD/MM/YYYY");

		addField("Starting Station", sourceBox);
		addField("Destination Station", destinationBox);
		addField("Seat Category (for fare comparison)", categoryBox);
		addField("Date of Journey (DD/MM/YYYY)", dateField);

		JButton recommendBtn = RailwayTheme.createPrimaryButton("Get Recommendations");
		addActionButton(recommendBtn);

		recommendBtn.addActionListener(e -> {
			if (sourceBox.getSelectedItem() == null || destinationBox.getSelectedItem() == null) {
				printResult("Error: No stations available yet.");
				return;
			}
			printResult(service.recommendTrains((String) sourceBox.getSelectedItem(), (String) destinationBox.getSelectedItem(),
					(String) categoryBox.getSelectedItem(), textOf(dateField)));
		});
	}

	@Override
	public void refreshData() {
		sourceBox.removeAllItems();
		destinationBox.removeAllItems();
		for (String s : service.getStationNames()) { sourceBox.addItem(s); destinationBox.addItem(s); }
	}
}
