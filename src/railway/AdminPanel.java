package railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminPanel extends JPanel implements Refreshable {
    private final RailwayService service;
    private JTextArea outputArea;
    private JPanel cardsPanel;
    private CardLayout cardLayout;

    // Kept as an instance field (rather than a local constructor variable) so
    // refreshData() can repopulate it whenever this panel becomes visible --
    // previously it was built once in the constructor and never updated,
    // so trains added after startup silently never appeared here.
    private JComboBox<String> addStationTrainBox;

    // The Schedule tab reuses ScheduleUpdatePanel (a standalone, already-built,
    // table-based schedule editor) instead of duplicating the same load/edit/save
    // logic inline a second time, as the previous version did.
    private ScheduleUpdatePanel schedulePanel;

    public AdminPanel(RailwayService service) {
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(RailwayTheme.CONTENT_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header with logout
        JPanel header = createHeader();
        JPanel tabBar = createTabBar();

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(RailwayTheme.CONTENT_BG);
        cardsPanel.add(createAddTrainPanel(), "ADD_TRAIN");
        cardsPanel.add(createAddStationPanel(), "ADD_STATION");
        schedulePanel = new ScheduleUpdatePanel(service);
        cardsPanel.add(schedulePanel, "SCHEDULE");
        cardsPanel.add(createAllTrainsPanel(), "ALL_TRAINS");
        cardsPanel.add(createSearchReceiptsPanel(), "SEARCH_RECEIPTS");

        outputArea = RailwayTheme.createOutputArea();
        JPanel terminal = RailwayTheme.createTerminalPanel(outputArea);
        terminal.setPreferredSize(new Dimension(100, 240));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(RailwayTheme.CONTENT_BG);
        center.add(header, BorderLayout.NORTH);
        center.add(tabBar, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(RailwayTheme.CONTENT_BG);
        content.add(center, BorderLayout.NORTH);
        content.add(cardsPanel, BorderLayout.CENTER);
        content.add(terminal, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
        cardLayout.show(cardsPanel, "ADD_TRAIN");
        outputArea.setText("Welcome Admin! Select an administrative task from the tabs above.");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(RailwayTheme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = RailwayTheme.createPanelTitle("🛠️ Admin Control Panel");
        JLabel subtitle = new JLabel("Manage trains, stations, routes, and timetables");
        subtitle.setFont(RailwayTheme.FONT_LABEL);
        subtitle.setForeground(RailwayTheme.TEXT_MUTED);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(RailwayTheme.CONTENT_BG);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(RailwayTheme.FONT_BUTTON);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(RailwayTheme.DANGER);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        logoutBtn.addActionListener(e -> {
            AuthService.getInstance().logout();
            ((MainFrame) SwingUtilities.getWindowAncestor(this)).showLogin();
        });

        header.add(titlePanel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel createTabBar() {
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        tabs.setBackground(RailwayTheme.CONTENT_BG);
        String[][] tabData = {
                {"🚆 Add Train", "ADD_TRAIN"},
                {"🚉 Add Station", "ADD_STATION"},
                {"🕒 Set Schedule", "SCHEDULE"},
                {"📋 All Trains", "ALL_TRAINS"},
                {"🔍 Search & Receipts", "SEARCH_RECEIPTS"}
        };
        java.util.List<JButton> btnList = new java.util.ArrayList<>();
        for (int i = 0; i < tabData.length; i++) {
            String[] data = tabData[i];
            JButton tabBtn = new JButton(data[0]);
            tabBtn.setFont(RailwayTheme.FONT_BUTTON);
            if (i == 0) {
                tabBtn.setBackground(RailwayTheme.PRIMARY);
                tabBtn.setForeground(Color.WHITE);
            } else {
                tabBtn.setBackground(Color.WHITE);
                tabBtn.setForeground(RailwayTheme.TEXT_DARK);
            }
            tabBtn.setOpaque(true);
            tabBtn.setBorderPainted(false);
            tabBtn.setFocusPainted(false);
            tabBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RailwayTheme.BORDER_COLOR, 1),
                    new EmptyBorder(8, 16, 8, 16)
            ));
            tabBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnList.add(tabBtn);

            tabBtn.addActionListener(e -> {
                for (JButton b : btnList) {
                    b.setBackground(Color.WHITE);
                    b.setForeground(RailwayTheme.TEXT_DARK);
                }
                tabBtn.setBackground(RailwayTheme.PRIMARY);
                tabBtn.setForeground(Color.WHITE);
                if (data[1].equals("ADD_STATION")) refreshData();
                if (data[1].equals("SCHEDULE") && schedulePanel != null) schedulePanel.refreshData();
                cardLayout.show(cardsPanel, data[1]);
                outputArea.setText("Admin Mode: " + data[0]);
            });
            tabs.add(tabBtn);
        }
        return tabs;
    }

    private JPanel createAddTrainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = RailwayTheme.createTextField();
        JTextField numberField = RailwayTheme.createTextField();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Train Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Train Number:"), gbc);
        gbc.gridx = 1;
        panel.add(numberField, gbc);

        JButton addBtn = RailwayTheme.createPrimaryButton("Add Train");
        addBtn.addActionListener(e -> {
            String result = service.addNewTrain(nameField.getText().trim(), numberField.getText().trim());
            outputArea.setText(result);
            if (result.startsWith("Success")) {
                nameField.setText("");
                numberField.setText("");
            }
        });
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        panel.add(addBtn, gbc);
        return panel;
    }

    private JPanel createAddStationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addStationTrainBox = RailwayTheme.createComboBox(service.getTrainNumbers());
        JTextField stationField = RailwayTheme.createTextField();
        JTextField distanceField = RailwayTheme.createTextField();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Train:"), gbc);
        gbc.gridx = 1;
        panel.add(addStationTrainBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Station Name:"), gbc);
        gbc.gridx = 1;
        panel.add(stationField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        panel.add(distanceField, gbc);

        JButton addBtn = RailwayTheme.createPrimaryButton("Add Station");
        addBtn.addActionListener(e -> {
            if (addStationTrainBox.getSelectedItem() == null) {
                outputArea.setText("Error: No trains available");
                return;
            }
            String result = service.addStationToRoute(
                    (String) addStationTrainBox.getSelectedItem(),
                    stationField.getText().trim(),
                    distanceField.getText().trim()
            );
            outputArea.setText(result);
            if (result.contains("Success")) {
                stationField.setText("");
                distanceField.setText("");
            }
        });
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        panel.add(addBtn, gbc);
        return panel;
    }

    private JPanel createAllTrainsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea trainList = RailwayTheme.createOutputArea();
        trainList.setBackground(RailwayTheme.CARD_BG);
        trainList.setForeground(RailwayTheme.TEXT_DARK);
        trainList.setFont(new Font("Consolas", Font.PLAIN, 13));

        JButton refreshBtn = RailwayTheme.createPrimaryButton("Refresh");
        refreshBtn.addActionListener(e -> trainList.setText(service.listTrains()));

        panel.add(new JScrollPane(trainList), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        trainList.setText(service.listTrains());
        return panel;
    }

    private JPanel createSearchReceiptsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(22, 24, 22, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField queryField = RailwayTheme.createTextField();
        queryField.setToolTipText("Enter Ticket ID (e.g. P1001), Passenger Name, or Mobile Number");
        queryField.setPreferredSize(new Dimension(280, 34));

        JLabel searchLabel = RailwayTheme.createFormLabel("Search Query:");
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(searchLabel, gbc);
        gbc.gridx = 1;
        panel.add(queryField, gbc);

        JLabel hint = new JLabel("Search across all trains by Ticket ID, Passenger Name, or Phone");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(RailwayTheme.TEXT_MUTED);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(hint, gbc);

        JButton searchBtn = RailwayTheme.createPrimaryButton("🔍 Search Tickets");
        searchBtn.addActionListener(e -> outputArea.setText(service.searchTickets(queryField.getText().trim())));

        JButton exportBtn = RailwayTheme.createSuccessButton("📄 Export Receipt (.txt)");
        exportBtn.addActionListener(e -> outputArea.setText(service.exportTicketReceipt(queryField.getText().trim())));

        JButton detailsBtn = RailwayTheme.createSecondaryButton("ℹ️ View Details");
        detailsBtn.addActionListener(e -> outputArea.setText(service.getTicketDetails(queryField.getText().trim())));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(RailwayTheme.CARD_BG);
        btnRow.add(searchBtn);
        btnRow.add(exportBtn);
        btnRow.add(detailsBtn);

        gbc.gridx = 1; gbc.gridy = 2; gbc.insets = new Insets(14, 8, 8, 8); gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnRow, gbc);
        return panel;
    }

    // Repopulates the train dropdown(s) from the current service state. Previously
    // this was a no-op, so trains added after this panel was first built never
    // showed up here -- called both when the panel is (re)shown from the sidebar
    // and whenever the Add Station tab is selected. The Schedule tab refreshes
    // itself via schedulePanel.refreshData() (see createTabBar()).
    @Override
    public void refreshData() {
        String prevAddStation = addStationTrainBox != null ? (String) addStationTrainBox.getSelectedItem() : null;
        if (addStationTrainBox != null) {
            addStationTrainBox.removeAllItems();
            for (String t : service.getTrainNumbers()) addStationTrainBox.addItem(t);
            if (prevAddStation != null) addStationTrainBox.setSelectedItem(prevAddStation);
        }
        if (schedulePanel != null) schedulePanel.refreshData();
    }
}