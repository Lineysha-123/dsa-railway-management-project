package railway;

import javax.swing.*;

// NetworkMapPanel.java
public class NetworkMapPanel extends BasePanel implements Refreshable {

	public NetworkMapPanel(RailwayService service) {
		super(service, "Railway Network Map", "Every station, its connections, and distances (km)");

		JButton refreshBtn = RailwayTheme.createPrimaryButton("Refresh Map");
		addActionButton(refreshBtn);
		refreshBtn.addActionListener(e -> refreshData());
	}

	@Override
	public void refreshData() {
		printResult(service.getNetworkMap());
	}
}
