package railway;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

// ScheduleUpdatePanel.java
// Provides both a station-by-station form editor and an interactive timetable table,
// pre-populating existing arrival and departure times so schedules can be easily set and updated.
public class ScheduleUpdatePanel extends JPanel implements Refreshable {

	private final RailwayService service;
	private JComboBox<String> trainBox;
	private JComboBox<String> stationBox;
	private JTextField arrivalField;
	private JTextField departureField;
	private DefaultTableModel tableModel;
	private JTable table;
	private JTextArea outputArea;

	public ScheduleUpdatePanel(RailwayService service) {
		this.service = service;
		setLayout(new BorderLayout(0, 12));
		setBackground(RailwayTheme.CONTENT_BG);
		setBorder(new EmptyBorder(16, 20, 16, 20));

		// Title & Instructions
		JPanel titleBox = new JPanel();
		titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
		titleBox.setBackground(RailwayTheme.CONTENT_BG);
		JLabel title = RailwayTheme.createPanelTitle("🕒 Train Timetable & Schedule Management");
		JLabel subtitle = new JLabel("Set arrival/departure times per station, or view the full route timetable.");
		subtitle.setFont(RailwayTheme.FONT_LABEL);
		subtitle.setForeground(RailwayTheme.TEXT_MUTED);
		subtitle.setBorder(new EmptyBorder(2, 0, 8, 0));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		titleBox.add(title);
		titleBox.add(subtitle);

		// Editor Card: Train & Station Form
		JPanel formCard = RailwayTheme.createCard();
		formCard.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 8, 6, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		trainBox = RailwayTheme.createComboBox(service.getTrainNumbers());
		trainBox.setPreferredSize(new Dimension(160, 32));
		stationBox = RailwayTheme.createComboBox(new String[0]);
		stationBox.setPreferredSize(new Dimension(160, 32));
		arrivalField = RailwayTheme.createTextField();
		arrivalField.setPreferredSize(new Dimension(110, 32));
		arrivalField.setToolTipText("HH:MM (24-hour) or N/A");
		departureField = RailwayTheme.createTextField();
		departureField.setPreferredSize(new Dimension(110, 32));
		departureField.setToolTipText("HH:MM (24-hour) or N/A");

		// Row 0: Train & Station Selectors
		gbc.gridx = 0; gbc.gridy = 0;
		formCard.add(new JLabel("Train:"), gbc);
		gbc.gridx = 1;
		formCard.add(trainBox, gbc);
		gbc.gridx = 2;
		formCard.add(new JLabel("Station:"), gbc);
		gbc.gridx = 3;
		formCard.add(stationBox, gbc);

		// Row 1: Arrival & Departure Times
		gbc.gridx = 0; gbc.gridy = 1;
		formCard.add(new JLabel("Arrival (HH:MM):"), gbc);
		gbc.gridx = 1;
		formCard.add(arrivalField, gbc);
		gbc.gridx = 2;
		formCard.add(new JLabel("Departure (HH:MM):"), gbc);
		gbc.gridx = 3;
		formCard.add(departureField, gbc);

		// Row 2: Action Buttons
		JButton updateStationBtn = RailwayTheme.createPrimaryButton("💾 Save Station Time");
		JButton viewScheduleBtn = RailwayTheme.createSecondaryButton("📋 View Full Timetable");
		JButton saveAllBtn = RailwayTheme.createSuccessButton("💾 Save All from Table");

		JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		actionRow.setBackground(RailwayTheme.CARD_BG);
		actionRow.add(updateStationBtn);
		actionRow.add(viewScheduleBtn);
		actionRow.add(saveAllBtn);

		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
		gbc.insets = new Insets(10, 8, 4, 8);
		formCard.add(actionRow, gbc);

		// Timetable Table
		tableModel = new DefaultTableModel(new Object[]{"Station", "Arrival (HH:MM)", "Departure (HH:MM)"}, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0;
			}
		};
		table = new JTable(tableModel);
		table.setRowHeight(26);
		table.setFont(RailwayTheme.FONT_FIELD);
		table.getTableHeader().setFont(RailwayTheme.FONT_BUTTON);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
				int row = table.getSelectedRow();
				String st = String.valueOf(tableModel.getValueAt(row, 0));
				stationBox.setSelectedItem(st);
				arrivalField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
				departureField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
			}
		});

		JScrollPane tableScroll = new JScrollPane(table);
		tableScroll.setPreferredSize(new Dimension(100, 140));
		tableScroll.setBorder(BorderFactory.createLineBorder(RailwayTheme.BORDER_COLOR, 1));

		JPanel middleCard = new JPanel(new BorderLayout(0, 8));
		middleCard.setBackground(RailwayTheme.CONTENT_BG);
		middleCard.add(formCard, BorderLayout.NORTH);
		middleCard.add(tableScroll, BorderLayout.CENTER);

		// Output Terminal
		outputArea = RailwayTheme.createOutputArea();
		JPanel terminal = RailwayTheme.createTerminalPanel(outputArea);
		terminal.setPreferredSize(new Dimension(100, 170));

		JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
		centerPanel.setBackground(RailwayTheme.CONTENT_BG);
		centerPanel.add(titleBox, BorderLayout.NORTH);
		centerPanel.add(middleCard, BorderLayout.CENTER);
		centerPanel.add(terminal, BorderLayout.SOUTH);

		add(centerPanel, BorderLayout.CENTER);

		// Event Listeners
		trainBox.addActionListener(e -> loadTrainData());
		stationBox.addActionListener(e -> onStationSelected());

		updateStationBtn.addActionListener(e -> {
			String trainNumber = (String) trainBox.getSelectedItem();
			String station = (String) stationBox.getSelectedItem();
			if (trainNumber == null || station == null) {
				outputArea.setText("Error: Please select both a train and a station.");
				return;
			}
			String result = service.setStationSchedule(trainNumber, station, arrivalField.getText().trim(), departureField.getText().trim());
			outputArea.setText(result);
			loadTableDataOnly();
		});

		viewScheduleBtn.addActionListener(e -> {
			String trainNumber = (String) trainBox.getSelectedItem();
			if (trainNumber == null) {
				outputArea.setText("Error: No train selected.");
				return;
			}
			outputArea.setText(service.getTrainScheduleDisplay(trainNumber));
		});

		saveAllBtn.addActionListener(e -> saveAllFromTable());

		// Initial Population
		loadTrainData();
	}

	private void loadTrainData() {
		String trainNumber = (String) trainBox.getSelectedItem();
		if (trainNumber == null) return;

		String[] stations = service.getRouteStationsArray(trainNumber);
		stationBox.removeAllItems();
		for (String s : stations) stationBox.addItem(s);

		loadTableDataOnly();
		onStationSelected();
		outputArea.setText(service.getTrainScheduleDisplay(trainNumber));
	}

	private void loadTableDataOnly() {
		String trainNumber = (String) trainBox.getSelectedItem();
		if (trainNumber == null) return;

		tableModel.setRowCount(0);
		String[] stations = service.getRouteStationsArray(trainNumber);
		for (int i = 0; i < stations.length; i++) {
			String st = stations[i];
			String arr = service.getStationArrival(trainNumber, st);
			String dep = service.getStationDeparture(trainNumber, st);
			tableModel.addRow(new Object[]{st, arr, dep});
		}
	}

	private void onStationSelected() {
		String trainNumber = (String) trainBox.getSelectedItem();
		String station = (String) stationBox.getSelectedItem();
		if (trainNumber == null || station == null) return;

		arrivalField.setText(service.getStationArrival(trainNumber, station));
		departureField.setText(service.getStationDeparture(trainNumber, station));
	}

	private void saveAllFromTable() {
		String trainNumber = (String) trainBox.getSelectedItem();
		if (trainNumber == null) { outputArea.setText("Error: No trains available."); return; }
		int rows = tableModel.getRowCount();
		if (rows == 0) { outputArea.setText("Error: No route loaded for this train."); return; }

		// Stop cell editing if user is currently typing in a cell
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}

		String[] arrivals = new String[rows];
		String[] departures = new String[rows];
		for (int i = 0; i < rows; i++) {
			arrivals[i] = String.valueOf(tableModel.getValueAt(i, 1));
			departures[i] = String.valueOf(tableModel.getValueAt(i, 2));
		}
		String result = service.applySchedule(trainNumber, arrivals, departures);
		outputArea.setText(result);
		loadTableDataOnly();
	}

	@Override
	public void refreshData() {
		String previouslySelected = (String) trainBox.getSelectedItem();
		trainBox.removeAllItems();
		for (String t : service.getTrainNumbers()) trainBox.addItem(t);
		if (previouslySelected != null) trainBox.setSelectedItem(previouslySelected);
		loadTrainData();
	}
}
