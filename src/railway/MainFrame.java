package railway;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {
	private final RailwayService service = new RailwayService();
	private final CardLayout mainCardLayout = new CardLayout();
	private final JPanel mainContentPanel = new JPanel(mainCardLayout);
	private LoginPanel loginPanel;
	private JPanel appPanel;
	private JPanel contentPanel;

	// Card name -> panel instance, so nav clicks can call refreshData() on the
	// panel being shown. Previously nav buttons only did cardLayout.show(...),
	// so every Refreshable panel's dropdowns (stations/trains) were populated
	// once at construction time and never updated again -- the root cause of
	// stations/trains added later never appearing in the dropdowns.
	private final java.util.Map<String, JPanel> panelsByCard = new java.util.HashMap<>();

	public MainFrame() {
		setTitle("Railway Management System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1100, 700));
		setSize(1200, 750);
		setLocationRelativeTo(null);

		loginPanel = new LoginPanel(this);
		mainContentPanel.add(loginPanel, "LOGIN");

		appPanel = buildAppPanel();
		mainContentPanel.add(appPanel, "APP");

		getContentPane().add(mainContentPanel);
		showLogin();
	}

	private JPanel buildAppPanel() {
		contentPanel = buildContent();
		JPanel app = new JPanel(new BorderLayout());
		app.add(buildHeader(), BorderLayout.NORTH);
		app.add(buildSidebar(), BorderLayout.WEST);
		app.add(contentPanel, BorderLayout.CENTER);
		return app;
	}

	private JPanel buildHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(RailwayTheme.NAVY_HEADER);
		header.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 41, 59)),
				new EmptyBorder(12, 22, 12, 22)
		));
		JLabel title = new JLabel("🚆  RAILWAY MANAGEMENT SYSTEM");
		title.setFont(RailwayTheme.FONT_APP_TITLE);
		title.setForeground(Color.WHITE);

		boolean isAdmin = AuthService.getInstance().isAdmin();
		JLabel status = new JLabel(isAdmin ? "  ● ADMIN CONTROL ROOM  " : "  ● PASSENGER PORTAL  ");
		status.setFont(new Font("Segoe UI", Font.BOLD, 12));
		status.setForeground(Color.WHITE);
		status.setOpaque(true);
		status.setBackground(isAdmin ? new Color(37, 99, 235) : new Color(16, 185, 129));
		status.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(isAdmin ? new Color(96, 165, 250) : new Color(110, 231, 183), 1),
				new EmptyBorder(4, 10, 4, 10)
		));

		header.add(title, BorderLayout.WEST);
		header.add(status, BorderLayout.EAST);
		return header;
	}

	private final java.util.List<JButton> navButtons = new java.util.ArrayList<>();

	private JScrollPane buildSidebar() {
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setBackground(RailwayTheme.NAVY_DARK);
		sidebar.setBorder(new EmptyBorder(10, 0, 20, 0));
		navButtons.clear();

		addSection(sidebar, "Overview");
		JButton dashBtn = createSidebarNavButton("📊  Dashboard", "DASHBOARD", true);
		sidebar.add(dashBtn);

		if (AuthService.getInstance().isAdmin()) {
			addSection(sidebar, "Administration");
			JButton adminBtn = createSidebarNavButton("🛠️  Admin Panel", "ADMIN", false);
			sidebar.add(adminBtn);
			JButton bookBtn = createSidebarNavButton("🎟️  Book Ticket", "BOOK_TICKET", false);
			sidebar.add(bookBtn);
		} else {
			addSection(sidebar, "Passenger Services");
			JButton passBtn = createSidebarNavButton("🎫  Passenger Portal", "PASSENGER", false);
			sidebar.add(passBtn);
			JButton bookBtn = createSidebarNavButton("🎟️  Book Ticket", "BOOK_TICKET", false);
			sidebar.add(bookBtn);
			JButton timeBtn = createSidebarNavButton("🕒  Train Timetable", "TIMETABLE", false);
			sidebar.add(timeBtn);
		}

		addSection(sidebar, "Route & Network");
		JButton bfsBtn = createSidebarNavButton("🔀  Fewest Stops (BFS)", "BFS", false);
		sidebar.add(bfsBtn);
		JButton dijkstraBtn = createSidebarNavButton("📏  Shortest Route (Dijkstra)", "DIJKSTRA", false);
		sidebar.add(dijkstraBtn);
		JButton recBtn = createSidebarNavButton("🎯  Recommend Train", "RECOMMEND", false);
		sidebar.add(recBtn);
		JButton mapBtn = createSidebarNavButton("🗺️  Network Topology Map", "MAP", false);
		sidebar.add(mapBtn);

		sidebar.add(Box.createVerticalGlue());
		JButton logoutBtn = new JButton("🚪  Sign Out");
		logoutBtn.setFont(RailwayTheme.FONT_BUTTON);
		logoutBtn.setForeground(Color.WHITE);
		logoutBtn.setBackground(RailwayTheme.DANGER);
		logoutBtn.setOpaque(true);
		logoutBtn.setBorderPainted(false);
		logoutBtn.setFocusPainted(false);
		logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
		logoutBtn.setBorder(new EmptyBorder(9, 20, 9, 10));
		logoutBtn.addActionListener(e -> {
			AuthService.getInstance().logout();
			showLogin();
		});
		sidebar.add(logoutBtn);

		JScrollPane scroll = new JScrollPane(sidebar);
		scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getViewport().setBackground(RailwayTheme.NAVY_DARK);
		scroll.setPreferredSize(new Dimension(240, 100));
		return scroll;
	}

	private JButton createSidebarNavButton(String text, String cardName, boolean active) {
		JButton btn = RailwayTheme.createNavButton(text);
		if (active) {
			btn.setBackground(RailwayTheme.PRIMARY);
			btn.setForeground(Color.WHITE);
			btn.setFont(RailwayTheme.FONT_NAV_ACTIVE);
		}
		navButtons.add(btn);
		btn.addActionListener(e -> {
			for (JButton b : navButtons) {
				b.setBackground(RailwayTheme.NAVY_DARK);
				b.setForeground(new Color(203, 213, 225));
				b.setFont(RailwayTheme.FONT_NAV);
			}
			btn.setBackground(RailwayTheme.PRIMARY);
			btn.setForeground(Color.WHITE);
			btn.setFont(RailwayTheme.FONT_NAV_ACTIVE);
			showCard(cardName);
		});
		return btn;
	}

	private void addSection(JPanel sidebar, String label) {
		sidebar.add(RailwayTheme.createSectionLabel(label));
	}

	private JPanel buildContent() {
		JPanel panel = new JPanel(new CardLayout());
		panel.setBackground(RailwayTheme.CONTENT_BG);
		panelsByCard.clear();
		addCard(panel, new DashboardPanel(service), "DASHBOARD");
		addCard(panel, new AdminPanel(service), "ADMIN");
		addCard(panel, new PassengerPanel(service), "PASSENGER");
		addCard(panel, new ShortestPathPanel(service), "BFS");
		addCard(panel, new DijkstraPanel(service), "DIJKSTRA");
		addCard(panel, new RecommendationPanel(service), "RECOMMEND");
		addCard(panel, new NetworkMapPanel(service), "MAP");
		((CardLayout) panel.getLayout()).show(panel, "DASHBOARD");
		return panel;
	}

	private void addCard(JPanel panel, JPanel card, String name) {
		panel.add(card, name);
		panelsByCard.put(name, card);
	}

	// Shows the requested card and, if it implements Refreshable, refreshes its
	// dropdowns/data first so it always reflects the latest trains/stations.
	private void showCard(String name) {
		if ("BOOK_TICKET".equals(name)) {
			JPanel passCard = panelsByCard.get("PASSENGER");
			if (passCard instanceof PassengerPanel) {
				((PassengerPanel) passCard).refreshData();
				((PassengerPanel) passCard).showBookingTab();
			}
			((CardLayout) contentPanel.getLayout()).show(contentPanel, "PASSENGER");
			return;
		}
		if ("TIMETABLE".equals(name)) {
			JPanel passCard = panelsByCard.get("PASSENGER");
			if (passCard instanceof PassengerPanel) {
				((PassengerPanel) passCard).refreshData();
				((PassengerPanel) passCard).showTimetableTab();
			}
			((CardLayout) contentPanel.getLayout()).show(contentPanel, "PASSENGER");
			return;
		}
		JPanel card = panelsByCard.get(name);
		if (card instanceof Refreshable) ((Refreshable) card).refreshData();
		((CardLayout) contentPanel.getLayout()).show(contentPanel, name);
	}

	public void showLogin() {
		mainCardLayout.show(mainContentPanel, "LOGIN");
		setTitle("Railway Management System - Login");
	}

	public void showMainApp() {
		appPanel = buildAppPanel();
		mainContentPanel.removeAll();
		mainContentPanel.add(loginPanel, "LOGIN");
		mainContentPanel.add(appPanel, "APP");
		mainCardLayout.show(mainContentPanel, "APP");
		setTitle("Railway Management System - " + (AuthService.getInstance().isAdmin() ? "Admin" : "Passenger"));
		revalidate();
		repaint();
	}
}