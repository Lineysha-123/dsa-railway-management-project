package railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(RailwayTheme.CONTENT_BG);
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = RailwayTheme.createCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RailwayTheme.BORDER_COLOR, 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(8, 8, 8, 8);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("🚆 Railway Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(RailwayTheme.NAVY_DARK);
        cardGbc.gridx = 0; cardGbc.gridy = 0; cardGbc.gridwidth = 2;
        card.add(title, cardGbc);

        JLabel subtitle = new JLabel("Sign in to access control panel and passenger services", SwingConstants.CENTER);
        subtitle.setFont(RailwayTheme.FONT_LABEL);
        subtitle.setForeground(RailwayTheme.TEXT_MUTED);
        cardGbc.gridy = 1;
        card.add(subtitle, cardGbc);

        cardGbc.gridy = 2;
        card.add(Box.createVerticalStrut(12), cardGbc);

        cardGbc.gridwidth = 1;
        cardGbc.gridx = 0; cardGbc.gridy = 3;
        JLabel userLabel = RailwayTheme.createFormLabel("Username:");
        card.add(userLabel, cardGbc);

        usernameField = RailwayTheme.createTextField();
        usernameField.setPreferredSize(new Dimension(200, 32));
        cardGbc.gridx = 1;
        card.add(usernameField, cardGbc);

        cardGbc.gridx = 0; cardGbc.gridy = 4;
        JLabel passLabel = RailwayTheme.createFormLabel("Password:");
        card.add(passLabel, cardGbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(RailwayTheme.FONT_FIELD);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RailwayTheme.BORDER_COLOR, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        passwordField.setPreferredSize(new Dimension(200, 32));
        cardGbc.gridx = 1;
        card.add(passwordField, cardGbc);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(RailwayTheme.ERROR_RED);
        messageLabel.setFont(RailwayTheme.FONT_LABEL);
        cardGbc.gridx = 0; cardGbc.gridy = 5; cardGbc.gridwidth = 2;
        card.add(messageLabel, cardGbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonPanel.setBackground(RailwayTheme.CARD_BG);
        JButton loginBtn = RailwayTheme.createPrimaryButton("Login");
        loginBtn.addActionListener(e -> attemptLogin());
        JButton demoBtn = RailwayTheme.createSecondaryButton("Demo as Passenger");
        demoBtn.addActionListener(e -> {
            usernameField.setText("passenger");
            passwordField.setText("pass123");
            attemptLogin();
        });
        buttonPanel.add(loginBtn);
        buttonPanel.add(demoBtn);
        cardGbc.gridy = 6; cardGbc.insets = new Insets(14, 8, 8, 8);
        card.add(buttonPanel, cardGbc);

        JLabel footer = new JLabel("Admin: admin / admin123  |  Passenger: passenger / pass123", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(RailwayTheme.TEXT_MUTED);
        cardGbc.gridy = 7; cardGbc.insets = new Insets(14, 8, 4, 8);
        card.add(footer, cardGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        add(card, gbc);

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke("ENTER"), "login");
        getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }
        AuthService auth = AuthService.getInstance();
        if (auth.login(username, password)) {
            messageLabel.setText("");
            mainFrame.showMainApp();
        } else {
            messageLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}