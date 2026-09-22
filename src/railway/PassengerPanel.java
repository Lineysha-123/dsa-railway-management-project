package railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PassengerPanel extends JPanel implements Refreshable {
    private final RailwayService service;
    private JTextArea outputArea;
    private JPanel cardsPanel;
    private CardLayout cardLayout;
    private final java.util.Map<String, JButton> tabButtons = new java.util.HashMap<>();

    private JComboBox<String> availSourceBox, availDestBox;
    private JComboBox<String> bookTrainBox, bookSourceBox, bookDestBox;
    private JComboBox<String> schedTrainBox;

    public PassengerPanel(RailwayService service) {
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(RailwayTheme.CONTENT_BG);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel header = createHeader();
        JPanel tabBar = createTabBar();

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(RailwayTheme.CONTENT_BG);
        cardsPanel.add(createAvailabilityPanel(), "AVAILABILITY");
        cardsPanel.add(createBookTicketPanel(), "BOOK");
        cardsPanel.add(createTimetablePanel(), "SCHEDULE");
        cardsPanel.add(createMyTicketsPanel(), "MY_TICKETS");
        cardsPanel.add(createWaitingListPanel(), "WAITING");

        outputArea = RailwayTheme.createOutputArea();
        JPanel terminal = RailwayTheme.createTerminalPanel(outputArea);
        terminal.setPreferredSize(new Dimension(100, 170));

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
        cardLayout.show(cardsPanel, "AVAILABILITY");
        outputArea.setText("Welcome! Use the tabs above to check trains, book tickets, view schedules, or manage bookings.");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(RailwayTheme.CONTENT_BG);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = RailwayTheme.createPanelTitle("🎫 Passenger Services Portal");
        JLabel subtitle = new JLabel("Book tickets with real-time seat allocation, view timetables, and manage bookings");
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
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tabs.setBackground(RailwayTheme.CONTENT_BG);
        String[][] tabData = {
                {"🔍 Check Availability", "AVAILABILITY"},
                {"🎟️ Book Ticket", "BOOK"},
                {"🕒 Train Timetable", "SCHEDULE"},
                {"📋 My Tickets", "MY_TICKETS"},
                {"⏳ Waiting List", "WAITING"}
        };
        tabButtons.clear();
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
                    new EmptyBorder(7, 14, 7, 14)
            ));
            tabBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tabButtons.put(data[1], tabBtn);

            tabBtn.addActionListener(e -> {
                for (JButton b : tabButtons.values()) {
                    b.setBackground(Color.WHITE);
                    b.setForeground(RailwayTheme.TEXT_DARK);
                }
                tabBtn.setBackground(RailwayTheme.PRIMARY);
                tabBtn.setForeground(Color.WHITE);
                if (data[1].equals("AVAILABILITY") || data[1].equals("BOOK")) refreshData();
                cardLayout.show(cardsPanel, data[1]);
                outputArea.setText("Passenger Mode: " + data[0]);
            });
            tabs.add(tabBtn);
        }
        return tabs;
    }

    public void showBookingTab() {
        JButton btn = tabButtons.get("BOOK");
        if (btn != null) btn.doClick();
        else cardLayout.show(cardsPanel, "BOOK");
    }

    public void showTimetableTab() {
        JButton btn = tabButtons.get("SCHEDULE");
        if (btn != null) btn.doClick();
        else cardLayout.show(cardsPanel, "SCHEDULE");
    }

    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        availSourceBox = RailwayTheme.createComboBox(service.getStationNames());
        availDestBox = RailwayTheme.createComboBox(service.getStationNames());
        JTextField dateField = RailwayTheme.createTextField();
        dateField.setToolTipText("DD/MM/YYYY");
        dateField.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(RailwayTheme.createFormLabel("From Station:"), gbc);
        gbc.gridx = 1;
        panel.add(availSourceBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(RailwayTheme.createFormLabel("To Station:"), gbc);
        gbc.gridx = 1;
        panel.add(availDestBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(RailwayTheme.createFormLabel("Travel Date:"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        JButton checkBtn = RailwayTheme.createPrimaryButton("🔍 Check Train Availability");
        checkBtn.addActionListener(e -> {
            if (availSourceBox.getSelectedItem() == null || availDestBox.getSelectedItem() == null) {
                outputArea.setText("Error: Please select source and destination");
                return;
            }
            if (availSourceBox.getSelectedItem().equals(availDestBox.getSelectedItem())) {
                outputArea.setText("Error: Source and Destination stations cannot be the same.");
                return;
            }
            String result = service.checkAvailability(
                    (String) availSourceBox.getSelectedItem(),
                    (String) availDestBox.getSelectedItem(),
                    dateField.getText().trim()
            );
            outputArea.setText(result);
        });
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        panel.add(checkBtn, gbc);
        return panel;
    }

    // 2-Column Responsive Layout with guaranteed visibility and scrolling
    private JScrollPane createBookTicketPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setBackground(RailwayTheme.CARD_BG);
        container.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel formGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        formGrid.setBackground(RailwayTheme.CARD_BG);

        // Column 1: Journey Details
        JPanel col1 = new JPanel(new GridBagLayout());
        col1.setBackground(RailwayTheme.CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        bookTrainBox = RailwayTheme.createComboBox(service.getTrainNumbers());
        bookSourceBox = RailwayTheme.createComboBox(service.getStationNames());
        bookDestBox = RailwayTheme.createComboBox(service.getStationNames());
        JComboBox<String> categoryBox = RailwayTheme.createComboBox(new String[]{"AC", "SLPR", "GEN"});
        JTextField dateField = RailwayTheme.createTextField();
        dateField.setToolTipText("DD/MM/YYYY");
        dateField.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; col1.add(RailwayTheme.createFormLabel("🚆 Select Train:"), gbc);
        gbc.gridx = 1; col1.add(bookTrainBox, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col1.add(RailwayTheme.createFormLabel("🚉 From Station:"), gbc);
        gbc.gridx = 1; col1.add(bookSourceBox, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col1.add(RailwayTheme.createFormLabel("🚉 To Station:"), gbc);
        gbc.gridx = 1; col1.add(bookDestBox, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col1.add(RailwayTheme.createFormLabel("💺 Seat Class:"), gbc);
        gbc.gridx = 1; col1.add(categoryBox, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col1.add(RailwayTheme.createFormLabel("📅 Journey Date:"), gbc);
        gbc.gridx = 1; col1.add(dateField, gbc);

        // Column 2: Passenger Details
        JPanel col2 = new JPanel(new GridBagLayout());
        col2.setBackground(RailwayTheme.CARD_BG);
        JTextField nameField = RailwayTheme.createTextField();
        JTextField ageField = RailwayTheme.createTextField();
        JComboBox<String> genderBox = RailwayTheme.createComboBox(new String[]{"M", "F", "O"});
        JTextField contactField = RailwayTheme.createTextField();

        r = 0;
        gbc.gridx = 0; gbc.gridy = r; col2.add(RailwayTheme.createFormLabel("👤 Passenger Name:"), gbc);
        gbc.gridx = 1; col2.add(nameField, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col2.add(RailwayTheme.createFormLabel("🎂 Age (Years):"), gbc);
        gbc.gridx = 1; col2.add(ageField, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col2.add(RailwayTheme.createFormLabel("⚧ Gender:"), gbc);
        gbc.gridx = 1; col2.add(genderBox, gbc); r++;
        gbc.gridx = 0; gbc.gridy = r; col2.add(RailwayTheme.createFormLabel("📱 Contact Phone:"), gbc);
        gbc.gridx = 1; col2.add(contactField, gbc); r++;

        JLabel hint = new JLabel("💡 Auto-Waitlist Priority: Senior Citizens (≥60) > Ladies > General");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(RailwayTheme.TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        col2.add(hint, gbc);

        formGrid.add(col1);
        formGrid.add(col2);

        // Prominent Action Button
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        actionRow.setBackground(RailwayTheme.CARD_BG);

        JButton bookBtn = RailwayTheme.createSuccessButton("🎟️  Confirm & Book Ticket");
        bookBtn.setPreferredSize(new Dimension(260, 38));
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        actionRow.add(bookBtn);

        container.add(formGrid, BorderLayout.CENTER);
        container.add(actionRow, BorderLayout.SOUTH);

        bookBtn.addActionListener(e -> {
            if (bookTrainBox.getSelectedItem() == null) {
                outputArea.setText("Error: No trains available");
                return;
            }
            if (bookSourceBox.getSelectedItem() != null && bookSourceBox.getSelectedItem().equals(bookDestBox.getSelectedItem())) {
                outputArea.setText("Error: Source and Destination stations cannot be the same.");
                return;
            }
            try {
                int age = Integer.parseInt(ageField.getText().trim());
                String result = service.bookTicket(
                        (String) bookTrainBox.getSelectedItem(),
                        nameField.getText().trim(),
                        contactField.getText().trim(),
                        String.valueOf(age),
                        (String) genderBox.getSelectedItem(),
                        dateField.getText().trim(),
                        (String) bookSourceBox.getSelectedItem(),
                        (String) bookDestBox.getSelectedItem(),
                        (String) categoryBox.getSelectedItem(),
                        "N/A"
                );
                outputArea.setText(result);
                if (result.startsWith("Success") || result.startsWith("Info")) {
                    nameField.setText("");
                    ageField.setText("");
                    contactField.setText("");
                }
            } catch (NumberFormatException ex) {
                outputArea.setText("Error: Invalid age. Please enter a number.");
            }
        });

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topRow.setBackground(RailwayTheme.CARD_BG);

        schedTrainBox = RailwayTheme.createComboBox(service.getTrainNumbers());
        schedTrainBox.setPreferredSize(new Dimension(200, 34));
        JButton viewBtn = RailwayTheme.createPrimaryButton("📋 View Timetable");
        viewBtn.addActionListener(e -> {
            String tn = (String) schedTrainBox.getSelectedItem();
            if (tn != null) outputArea.setText(service.getTrainScheduleDisplay(tn));
        });

        topRow.add(RailwayTheme.createFormLabel("Select Train:"));
        topRow.add(schedTrainBox);
        topRow.add(viewBtn);

        panel.add(topRow, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createMyTicketsPanel() {
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

        JLabel hint = new JLabel("Search by Ticket ID (e.g. P1001), Passenger Name, or Contact Phone");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(RailwayTheme.TEXT_MUTED);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(hint, gbc);

        JButton searchBtn = RailwayTheme.createPrimaryButton("🔍 Search Tickets");
        searchBtn.addActionListener(e -> outputArea.setText(service.searchTickets(queryField.getText().trim())));

        JButton exportBtn = RailwayTheme.createSuccessButton("📄 Export Receipt (.txt)");
        exportBtn.addActionListener(e -> outputArea.setText(service.exportTicketReceipt(queryField.getText().trim())));

        JButton cancelBtn = RailwayTheme.createSecondaryButton("❌ Cancel Ticket");
        cancelBtn.addActionListener(e -> outputArea.setText(service.cancelTicket(queryField.getText().trim())));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(RailwayTheme.CARD_BG);
        btnRow.add(searchBtn);
        btnRow.add(exportBtn);
        btnRow.add(cancelBtn);

        gbc.gridx = 1; gbc.gridy = 2; gbc.insets = new Insets(14, 8, 8, 8); gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnRow, gbc);
        return panel;
    }

    private JPanel createWaitingListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(RailwayTheme.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea waitingList = RailwayTheme.createOutputArea();
        waitingList.setBackground(RailwayTheme.CARD_BG);
        waitingList.setForeground(RailwayTheme.TEXT_DARK);
        waitingList.setFont(new Font("Consolas", Font.PLAIN, 13));

        JButton refreshBtn = RailwayTheme.createPrimaryButton("Refresh");
        refreshBtn.addActionListener(e -> waitingList.setText(service.getWaitingQueueDisplay()));

        panel.add(new JScrollPane(waitingList), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        waitingList.setText(service.getWaitingQueueDisplay());
        return panel;
    }

    @Override
    public void refreshData() {
        refreshStationBox(availSourceBox);
        refreshStationBox(availDestBox);
        refreshStationBox(bookSourceBox);
        refreshStationBox(bookDestBox);
        refreshTrainBox(bookTrainBox);
        refreshTrainBox(schedTrainBox);
    }

    private void refreshStationBox(JComboBox<String> box) {
        if (box == null) return;
        String previous = (String) box.getSelectedItem();
        box.removeAllItems();
        for (String s : service.getStationNames()) box.addItem(s);
        if (previous != null) box.setSelectedItem(previous);
    }

    private void refreshTrainBox(JComboBox<String> box) {
        if (box == null) return;
        String previous = (String) box.getSelectedItem();
        box.removeAllItems();
        for (String t : service.getTrainNumbers()) box.addItem(t);
        if (previous != null) box.setSelectedItem(previous);
    }
}