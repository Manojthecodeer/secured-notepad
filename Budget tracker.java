import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.border.Border; // Explicitly import Border
import javax.swing.border.CompoundBorder;

public class Budget {
    private JFrame frame;
    private JTextField userField, amountField, categoryField, salaryField;
    private JPasswordField passwordField;
    private JTextArea expenseArea;
    private String currentUser;

    private static final String URL = "jdbc:mysql://localhost:3306/BudgetDB";
    private static final String USER = "root";
    private static final String PASSWORD = "San@1122";
    private static final Color PRIMARY_COLOR = new Color(94, 129, 172); // Soft Blue
    private static final Color SIDEBAR_COLOR = new Color(40, 40, 40); // Dark Gray
    private static final Color TEXT_COLOR = new Color(50, 50, 50); // Dark Text
    private static final Color BUTTON_COLOR_1 = new Color(0, 150, 136); // Teal
    private static final Color BUTTON_COLOR_2 = new Color(255, 112, 67); // Coral
    private static final Color BUTTON_COLOR_3 = new Color(124, 77, 255); // Violet
    private static final Color BUTTON_COLOR_4 = new Color(255, 202, 40); // Amber
    private static final Color BUTTON_COLOR_5 = new Color(120, 144, 156); // Blue Gray

    public Budget() {
        createLoginSignupUI();
    }

    private void createLoginSignupUI() {
        frame = new JFrame("Budget Tracker - Login/Signup");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 181, 246), 0, getHeight(), new Color(66, 165, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        userField = new JTextField(15);
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        userField.setFont(new Font("Arial", Font.PLAIN, 14));

        passwordField = new JPasswordField(15);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
        JButton signupButton = createStyledButton("Signup", BUTTON_COLOR_1);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createStyledLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        panel.add(signupButton, gbc);

        loginButton.addActionListener(_ -> loginUser());
        signupButton.addActionListener(_ -> signupUser());

        frame.add(panel);
        frame.setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void signupUser() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Signup successful!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }

    private void loginUser() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    currentUser = username;
                    frame.dispose();
                    createMainUI();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }

    private void createMainUI() {
        frame = new JFrame("Budget Tracker - " + currentUser);
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(103, 58, 183), 0, getHeight(), new Color(63, 81, 181));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 255, 255, 200));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        header.add(welcomeLabel, BorderLayout.WEST);
        header.setPreferredSize(new Dimension(0, 60));

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel appTitle = new JLabel("Expense Tracker");
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(appTitle);
        sidebar.add(Box.createVerticalStrut(30));

        JButton addExpenseButton = createSidebarButton("Add Expense", BUTTON_COLOR_1);
        JButton viewExpensesButton = createSidebarButton("View Expenses", BUTTON_COLOR_2);
        JButton pieChartButton = createSidebarButton("View Pie Chart", BUTTON_COLOR_3);
        JButton financialTipsButton = createSidebarButton("Financial Tips", BUTTON_COLOR_4);
        JButton logoutButton = createSidebarButton("Logout", BUTTON_COLOR_5);

        sidebar.add(addExpenseButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(viewExpensesButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(pieChartButton);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(financialTipsButton);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutButton);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input card
        JPanel inputCard = new JPanel(new GridBagLayout());
        inputCard.setBackground(Color.WHITE);
        inputCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        inputCard.setPreferredSize(new Dimension(0, 220));
        inputCard.setBorder(new CompoundBorder(inputCard.getBorder(), new DropShadowBorder())); // Combine borders

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel inputTitle = new JLabel("Add New Expense");
        inputTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        inputTitle.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputCard.add(inputTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputCard.add(createStyledLabel("Category:", TEXT_COLOR), gbc);
        categoryField = new JTextField(15);
        categoryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        inputCard.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputCard.add(createStyledLabel("Amount:", TEXT_COLOR), gbc);
        amountField = new JTextField(15);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        inputCard.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputCard.add(createStyledLabel("Monthly Salary:", TEXT_COLOR), gbc);
        salaryField = new JTextField(15);
        salaryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        salaryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        inputCard.add(salaryField, gbc);

        // Expense display card
        expenseArea = new JTextArea(15, 30);
        expenseArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expenseArea.setBackground(Color.WHITE);
        expenseArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        expenseArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(expenseArea);
        Border titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true), "Expenses", 0, 0, new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR);
        scrollPane.setBorder(new CompoundBorder(titledBorder, new DropShadowBorder())); // Combine borders

        contentPanel.add(inputCard, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);

        addExpenseButton.addActionListener(_ -> {
            addExpense();
            categoryField.setText("");
            amountField.setText("");
            salaryField.setText("");
        });
        viewExpensesButton.addActionListener(_ -> viewExpenses());
        pieChartButton.addActionListener(_ -> showPieChart());
        financialTipsButton.addActionListener(_ -> showFinancialTips());
        logoutButton.addActionListener(_ -> {
            frame.dispose();
            currentUser = null;
            createLoginSignupUI();
        });

        frame.setVisible(true);
    }

    private JButton createSidebarButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private JLabel createStyledLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private void addExpense() {
        String category = categoryField.getText().trim();
        String amountStr = amountField.getText().trim();

        if (category.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Category and amount cannot be empty.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "INSERT INTO expenses (username, category, amount) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, currentUser);
                stmt.setString(2, category);
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Expense added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error adding expense: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount format.");
        }
    }

    private void viewExpenses() {
        expenseArea.setText("");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT category, amount FROM expenses WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            StringBuilder expenses = new StringBuilder();
            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                expenses.append(category).append(": ₹").append(amount).append("\n");
            }
            expenseArea.setText(expenses.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error retrieving expenses: " + ex.getMessage());
        }
    }

    private void showPieChart() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT category, SUM(amount) AS total FROM expenses WHERE username = ? GROUP BY category";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, currentUser);
                ResultSet rs = stmt.executeQuery();

                DefaultPieDataset dataset = new DefaultPieDataset();
                while (rs.next()) {
                    dataset.setValue(rs.getString("category"), rs.getDouble("total"));
                }

                if (dataset.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(frame, "No expenses to display.");
                    return;
                }

                JFreeChart chart = ChartFactory.createPieChart("Expense Distribution", dataset, true, true, false);
                PiePlot plot = (PiePlot) chart.getPlot();
                ChartFrame chartFrame = new ChartFrame("Expense Chart", chart);
                chartFrame.setSize(500, 500);
                chartFrame.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
        }
    }

    private void showFinancialTips() {
        String salaryText = salaryField.getText().trim();

        if (salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter your monthly salary!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double salary = Double.parseDouble(salaryText);

            double needs = salary * 0.50;
            double wants = salary * 0.30;
            double savings = salary * 0.20;

            String message = String.format(
                "💰 Financial Budgeting Tips:\n\n" +
                "🏠 Essentials (50%%): ₹%.2f\n" +
                "🎉 Wants (30%%): ₹%.2f\n" +
                "💵 Savings & Investments (20%%): ₹%.2f\n\n" +
                "📌 Tip: Save at least 20%% for future stability & emergencies!",
                needs, wants, savings
            );

            JOptionPane.showMessageDialog(frame, message, "Financial Tips", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Enter a valid salary amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Budget::new);
    }

    // Custom border for drop shadow effect
    public static class DropShadowBorder implements Border {
        private final int shadowSize = 5;
        private final Color shadowColor = new Color(0, 0, 0, 50);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw shadow
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(x + shadowSize, y + shadowSize, width - shadowSize, height - shadowSize, 15, 15);

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}