package railway;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// BasePanel.java
// Shared skeleton for every feature panel: a title/subtitle header, a white
// "card" holding the input form + action button, and a dark console-style
// output area underneath showing the result. Subclasses just build the form
// fields and wire up one action.
public abstract class BasePanel extends JPanel {

	protected final RailwayService service;
	protected final JTextArea outputArea;
	protected final JPanel formCard;
	protected final GridBagConstraints gbc;
	private int rowIndex = 0;

	public BasePanel(RailwayService service, String title, String subtitle) {
		this.service = service;
		setLayout(new BorderLayout());
		setBackground(RailwayTheme.CONTENT_BG);
		setBorder(new EmptyBorder(20, 24, 20, 24));

		JPanel titleBox = new JPanel();
		titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
		titleBox.setBackground(RailwayTheme.CONTENT_BG);
		JLabel titleLabel = RailwayTheme.createPanelTitle(title);
		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel subtitleLabel = new JLabel(subtitle);
		subtitleLabel.setFont(RailwayTheme.FONT_LABEL);
		subtitleLabel.setForeground(RailwayTheme.TEXT_MUTED);
		subtitleLabel.setBorder(new EmptyBorder(4, 0, 16, 0));
		subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		titleBox.add(titleLabel);
		titleBox.add(subtitleLabel);

		formCard = RailwayTheme.createCard();
		formCard.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JPanel formWrapper = new JPanel(new BorderLayout());
		formWrapper.setBackground(RailwayTheme.CONTENT_BG);
		formWrapper.add(formCard, BorderLayout.CENTER);
		formWrapper.setBorder(new EmptyBorder(0, 0, 16, 0));

		outputArea = RailwayTheme.createOutputArea();
		JPanel terminal = RailwayTheme.createTerminalPanel(outputArea);

		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(RailwayTheme.CONTENT_BG);
		top.add(titleBox, BorderLayout.NORTH);
		top.add(formWrapper, BorderLayout.CENTER);

		add(top, BorderLayout.NORTH);
		add(terminal, BorderLayout.CENTER);

		printWelcome();
	}

	protected void printWelcome() {
		outputArea.setText("Ready. Fill in the fields above and press the action button.");
	}

	// Adds a "label | field" row to the form card, two per line via GridBagLayout.
	protected JComponent addField(String labelText, JComponent field) {
		gbc.gridx = 0;
		gbc.gridy = rowIndex;
		gbc.weightx = 0;
		JLabel label = RailwayTheme.createFormLabel(labelText);
		formCard.add(label, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		formCard.add(field, gbc);
		rowIndex++;
		return field;
	}

	protected void addActionButton(JButton button) {
		gbc.gridx = 0;
		gbc.gridy = rowIndex;
		gbc.gridwidth = 2;
		gbc.weightx = 0;
		gbc.insets = new Insets(16, 6, 6, 6);
		JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		wrap.setBackground(RailwayTheme.CARD_BG);
		wrap.add(button);
		formCard.add(wrap, gbc);
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.gridwidth = 1;
		rowIndex++;
	}

	protected void printResult(String text) {
		outputArea.setText(text);
		outputArea.setCaretPosition(0);
	}

	protected String textOf(JTextField f) {
		return f.getText() == null ? "" : f.getText().trim();
	}
}
