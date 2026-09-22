package railway;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * RailwayTheme.java
 * Centralized Design System & UI Factory.
 * Professional Modern Railway Control-Room Aesthetics:
 * - Slate 900 / Midnight Header & Sidebar
 * - Crisp Slate 50 Content Canvas & Pure White Card Containers
 * - Vibrant Royal Blue & Signal Amber Primary Accents
 * - Modern Monospace Terminal Console with Window Controls
 */
public class RailwayTheme {

	// ---- Modern Palette ----
	public static final Color NAVY_DARK       = new Color(15, 23, 42);    // Slate 900 - sidebar
	public static final Color NAVY_MID        = new Color(30, 41, 59);    // Slate 800 - sidebar hover
	public static final Color NAVY_HEADER     = new Color(11, 19, 43);    // Deep Nightfall - top bar
	public static final Color NAVY_LIGHT      = new Color(51, 65, 85);    // Slate 700 - dividers
	
	public static final Color PRIMARY         = new Color(37, 99, 235);   // Royal Blue (#2563EB)
	public static final Color PRIMARY_DARK    = new Color(29, 78, 216);   // Hover Blue (#1D4ED8)
	public static final Color SECONDARY       = new Color(79, 70, 229);   // Indigo
	
	public static final Color ACCENT_AMBER    = new Color(245, 158, 11);  // Amber (#F59E0B)
	public static final Color ACCENT_AMBER_DARK = new Color(217, 119, 6);
	
	public static final Color CONTENT_BG      = new Color(248, 250, 252); // Slate 50 - clean canvas
	public static final Color CARD_BG         = Color.WHITE;
	public static final Color BORDER_COLOR    = new Color(226, 232, 240); // Slate 200 - soft border
	public static final Color BORDER_FOCUS    = new Color(59, 130, 246);  // Blue 500 - focus border
	
	public static final Color TEXT_LIGHT      = new Color(248, 250, 252);
	public static final Color TEXT_MUTED      = new Color(100, 116, 139); // Slate 500
	public static final Color TEXT_DARK       = new Color(15, 23, 42);    // Slate 900
	
	public static final Color SUCCESS         = new Color(16, 185, 129);  // Emerald (#10B981)
	public static final Color SUCCESS_DARK    = new Color(5, 150, 105);
	public static final Color SUCCESS_GREEN   = SUCCESS;
	
	public static final Color DANGER          = new Color(239, 68, 68);   // Rose (#EF4444)
	public static final Color DANGER_DARK     = new Color(220, 38, 38);
	public static final Color ERROR_RED       = DANGER;
	
	public static final Color WARNING         = ACCENT_AMBER;
	
	public static final Color CONSOLE_BG      = Color.WHITE;               // Crisp White Card
	public static final Color CONSOLE_TEXT    = new Color(30, 41, 59);     // Slate 800 - Dark charcoal text
	public static final Color CONSOLE_HEADER  = new Color(241, 245, 249);  // Slate 100 - Soft light card header
	public static final Color CONSOLE_BORDER  = new Color(226, 232, 240);  // Slate 200 - Clean border

	// ---- Typography Tokens ----
	public static final Font FONT_APP_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
	public static final Font FONT_SECTION     = new Font("Segoe UI", Font.BOLD, 11);
	public static final Font FONT_NAV         = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font FONT_NAV_ACTIVE  = new Font("Segoe UI", Font.BOLD, 13);
	public static final Font FONT_PANEL_TITLE = new Font("Segoe UI", Font.BOLD, 19);
	public static final Font FONT_LABEL       = new Font("Segoe UI", Font.BOLD, 12);
	public static final Font FONT_FIELD       = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font FONT_BUTTON      = new Font("Segoe UI", Font.BOLD, 13);
	public static final Font FONT_MONO        = new Font("Consolas", Font.PLAIN, 13);

	// ---- Sidebar Navigation Button ----
	public static JButton createNavButton(String text) {
		JButton b = new JButton(text);
		b.setFont(FONT_NAV);
		b.setForeground(new Color(203, 213, 225));
		b.setBackground(NAVY_DARK);
		b.setOpaque(true);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setHorizontalAlignment(SwingConstants.LEFT);
		b.setBorder(new EmptyBorder(10, 20, 10, 14));
		b.setAlignmentX(Component.LEFT_ALIGNMENT);
		b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if (!b.getBackground().equals(PRIMARY)) {
					b.setBackground(NAVY_MID);
					b.setForeground(Color.WHITE);
				}
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				if (!b.getBackground().equals(PRIMARY)) {
					b.setBackground(NAVY_DARK);
					b.setForeground(new Color(203, 213, 225));
				}
			}
		});
		return b;
	}

	public static JLabel createSectionLabel(String text) {
		JLabel l = new JLabel(text.toUpperCase());
		l.setFont(FONT_SECTION);
		l.setForeground(new Color(148, 163, 184));
		l.setBorder(new EmptyBorder(16, 20, 6, 10));
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		return l;
	}

	// ---- Primary Button (Royal Blue / Indigo) ----
	public static JButton createPrimaryButton(String text) {
		JButton b = new JButton(text);
		b.setFont(FONT_BUTTON);
		b.setForeground(Color.WHITE);
		b.setBackground(PRIMARY);
		b.setOpaque(true);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setBorder(new EmptyBorder(10, 22, 10, 22));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				b.setBackground(PRIMARY_DARK);
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				b.setBackground(PRIMARY);
			}
		});
		return b;
	}

	// ---- Success Action Button (Emerald Green) ----
	public static JButton createSuccessButton(String text) {
		JButton btn = new JButton(text);
		btn.setFont(FONT_BUTTON);
		btn.setForeground(Color.WHITE);
		btn.setBackground(SUCCESS);
		btn.setOpaque(true);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setBorder(new EmptyBorder(10, 22, 10, 22));
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btn.setBackground(SUCCESS_DARK);
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				btn.setBackground(SUCCESS);
			}
		});
		return btn;
	}

	// ---- Secondary Outline Button ----
	public static JButton createSecondaryButton(String text) {
		JButton b = new JButton(text);
		b.setFont(FONT_BUTTON);
		b.setForeground(TEXT_DARK);
		b.setBackground(new Color(241, 245, 249));
		b.setOpaque(true);
		b.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(9, 20, 9, 20)
		));
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				b.setBackground(new Color(226, 232, 240));
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				b.setBackground(new Color(241, 245, 249));
			}
		});
		return b;
	}

	// ---- Form Controls ----
	public static JTextField createTextField() {
		JTextField f = new JTextField();
		f.setFont(FONT_FIELD);
		f.setForeground(TEXT_DARK);
		f.setBackground(Color.WHITE);
		f.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(7, 10, 7, 10)
		));
		return f;
	}

	public static JComboBox<String> createComboBox(String[] items) {
		JComboBox<String> box = new JComboBox<>(items);
		box.setFont(FONT_FIELD);
		box.setForeground(TEXT_DARK);
		box.setBackground(Color.WHITE);
		box.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		return box;
	}

	public static JLabel createFormLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(FONT_LABEL);
		l.setForeground(new Color(71, 85, 105)); // Slate 600
		return l;
	}

	public static JLabel createPanelTitle(String text) {
		JLabel l = new JLabel(text);
		l.setFont(FONT_PANEL_TITLE);
		l.setForeground(NAVY_DARK);
		return l;
	}

	// ---- Card Container ----
	public static JPanel createCard() {
		JPanel p = new JPanel();
		p.setBackground(CARD_BG);
		p.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(20, 24, 20, 24)
		));
		return p;
	}

	public static JPanel createContentWrapper() {
		JPanel p = new JPanel();
		p.setBackground(CONTENT_BG);
		p.setBorder(new EmptyBorder(20, 24, 20, 24));
		return p;
	}

	// ---- Modern Clean Card Output & Activity Feed ----
	public static JTextArea createOutputArea() {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setFont(new Font("Consolas", Font.PLAIN, 12));
		area.setBackground(Color.WHITE);
		area.setForeground(CONSOLE_TEXT);
		area.setCaretColor(PRIMARY);
		area.setSelectionColor(new Color(219, 234, 254));
		area.setSelectedTextColor(new Color(30, 58, 138));
		area.setBorder(new EmptyBorder(12, 16, 12, 16));
		area.setLineWrap(false);
		return area;
	}

	/**
	 * Wraps a JTextArea inside a clean, modern card-based System Output & Activity Log.
	 * Replaces the dark terminal with a professional light enterprise UI card.
	 * Includes a live status indicator, 1-click Copy, Clear, and Minimize/Expand toggle.
	 */
	public static JPanel createTerminalPanel(JTextArea area) {
		JPanel panelWrapper = new JPanel(new BorderLayout());
		panelWrapper.setBackground(Color.WHITE);
		panelWrapper.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(CONSOLE_BORDER, 1),
				new EmptyBorder(0, 0, 0, 0)
		));

		// Top Bar
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(CONSOLE_HEADER);
		topBar.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, CONSOLE_BORDER),
				new EmptyBorder(6, 14, 6, 10)
		));

		// Left: Title & Status Badge
		JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		leftHeader.setOpaque(false);

		JLabel titleIcon = new JLabel("📋  System Activity & Output Log");
		titleIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
		titleIcon.setForeground(new Color(30, 41, 59));

		JLabel statusBadge = new JLabel(" READY ");
		statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
		statusBadge.setForeground(new Color(5, 150, 105));
		statusBadge.setOpaque(true);
		statusBadge.setBackground(new Color(209, 250, 229));
		statusBadge.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(110, 231, 183), 1),
				new EmptyBorder(2, 6, 2, 6)
		));

		leftHeader.add(titleIcon);
		leftHeader.add(statusBadge);

		// Right: Action Buttons (Copy, Clear, Minimize)
		JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		rightActions.setOpaque(false);

		JButton copyBtn = new JButton("📋 Copy");
		copyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		copyBtn.setForeground(new Color(71, 85, 105));
		copyBtn.setBackground(Color.WHITE);
		copyBtn.setFocusPainted(false);
		copyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		copyBtn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
				new EmptyBorder(3, 8, 3, 8)
		));
		copyBtn.addActionListener(e -> {
			String text = area.getText();
			if (text != null && !text.isEmpty()) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(text), null);
				statusBadge.setText(" COPIED ");
				Timer t = new Timer(1400, evt -> statusBadge.setText(" READY "));
				t.setRepeats(false);
				t.start();
			}
		});

		JButton clearBtn = new JButton("🗑️ Clear");
		clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		clearBtn.setForeground(new Color(71, 85, 105));
		clearBtn.setBackground(Color.WHITE);
		clearBtn.setFocusPainted(false);
		clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		clearBtn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
				new EmptyBorder(3, 8, 3, 8)
		));
		clearBtn.addActionListener(e -> area.setText(""));

		JButton collapseBtn = new JButton("▾ Minimize");
		collapseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		collapseBtn.setForeground(new Color(71, 85, 105));
		collapseBtn.setBackground(Color.WHITE);
		collapseBtn.setFocusPainted(false);
		collapseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		collapseBtn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
				new EmptyBorder(3, 8, 3, 8)
		));

		JScrollPane scroll = new JScrollPane(area);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getViewport().setBackground(Color.WHITE);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		final boolean[] isCollapsed = {false};
		final Dimension[] savedPreferredSize = new Dimension[]{null};

		collapseBtn.addActionListener(e -> {
			if (!isCollapsed[0]) {
				savedPreferredSize[0] = panelWrapper.getPreferredSize();
				scroll.setVisible(false);
				panelWrapper.setPreferredSize(new Dimension(panelWrapper.getWidth(), 36));
				collapseBtn.setText("▲ Expand");
				isCollapsed[0] = true;
			} else {
				scroll.setVisible(true);
				panelWrapper.setPreferredSize(savedPreferredSize[0]);
				collapseBtn.setText("▾ Minimize");
				isCollapsed[0] = false;
			}
			panelWrapper.revalidate();
			panelWrapper.repaint();
		});

		// Auto-expand when fresh output arrives
		area.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void onTextChange() {
				if (isCollapsed[0] && area.getText() != null && !area.getText().trim().isEmpty()) {
					SwingUtilities.invokeLater(() -> {
						scroll.setVisible(true);
						panelWrapper.setPreferredSize(savedPreferredSize[0]);
						collapseBtn.setText("▾ Minimize");
						isCollapsed[0] = false;
						panelWrapper.revalidate();
						panelWrapper.repaint();
					});
				}
			}
			public void insertUpdate(javax.swing.event.DocumentEvent e) { onTextChange(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { onTextChange(); }
		});

		rightActions.add(copyBtn);
		rightActions.add(clearBtn);
		rightActions.add(collapseBtn);

		topBar.add(leftHeader, BorderLayout.WEST);
		topBar.add(rightActions, BorderLayout.EAST);

		panelWrapper.add(topBar, BorderLayout.NORTH);
		panelWrapper.add(scroll, BorderLayout.CENTER);
		return panelWrapper;
	}
}
