import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class CrashGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// Static variables for formatting and random number generation
	private static final DecimalFormat df = new DecimalFormat("0.00");

	// Instance variables for game state
	private float total;
	private float betAmount;
	private JTextField cashTextField, betTextField, multiplierTextField;
	private JButton betButton, cashOutButton;
	private Timer timer; // Timer for updating multiplierTextField
	private float maxMultiplier;
	private float payoutMultiplier;
	private ImageIcon explosionIcon = new ImageIcon("/Users/nehan/Downloads/explosion1.png");
	private ImageIcon rocketIcon = new ImageIcon("/Users/nehan/Downloads/rocket.gif");
	private ImageIcon rocketIconStill = new ImageIcon("/Users/nehan/Downloads/rocket.png");
	private ImageIcon backgroundImage = new ImageIcon("/Users/nehan/Downloads/space.jpg");
	private JLabel gifLabel;

	// Constructor to initialize the GUI
	public CrashGUI() {
		super("Crash Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400); // Increased height to accommodate GIF
		setLayout(new BorderLayout()); // Use BorderLayout for overall layout

		// Panel for betting components
		JPanel bettingPanel = new JPanel();
		bettingPanel.setLayout(new BoxLayout(bettingPanel, BoxLayout.Y_AXIS));
		bettingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Add betting components
		cashTextField = new JTextField(10);
		bettingPanel.add(new JLabel("Cash:"));
		bettingPanel.add(cashTextField);

		betTextField = new JTextField(10);
		bettingPanel.add(new JLabel("Bet:"));
		bettingPanel.add(betTextField);

		betButton = new JButton("Bet");
		betButton.addActionListener(this);
		bettingPanel.add(betButton);

		cashOutButton = new JButton("Cash Out");
		cashOutButton.addActionListener(this);
		cashOutButton.setEnabled(false);
		bettingPanel.add(cashOutButton);

		multiplierTextField = new JTextField(20);
		multiplierTextField.setEditable(false);
		bettingPanel.add(new JLabel("Multiplier:"));
		bettingPanel.add(multiplierTextField);

		// Add betting panel to the content pane
		add(bettingPanel, BorderLayout.SOUTH);

		// Set up the background label with the background image
		JLabel backgroundLabel = new JLabel(backgroundImage);
		backgroundLabel.setLayout(new BorderLayout());
		backgroundLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the background label
		add(backgroundLabel, BorderLayout.CENTER); // Add background label to the content pane

		// Load GIF image
		gifLabel = new JLabel(rocketIconStill);
		gifLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the GIF label
		backgroundLabel.add(gifLabel, BorderLayout.CENTER); // Add GIF label to the background label

		// Set JFrame visible
		setVisible(true);
	}

	// ActionListener implementation for button clicks and timer events
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == betButton) {
			// Handle bet button click
			try {
				// Get initial cash and bet amount from text fields
				float initialCash = Float.parseFloat(cashTextField.getText());
				betAmount = Float.parseFloat(betTextField.getText());
				total = initialCash - betAmount;

				// Check if the user has sufficient funds for the bet
				if (total < 0) {
					JOptionPane.showMessageDialog(this, "Insufficient funds for this bet.");
					return;
				}

				cashTextField.setText(df.format(total)); // Update cash text field
				betButton.setEnabled(false); // Disable bet button
				cashOutButton.setEnabled(true); // Enable cash out button
				gifLabel.setIcon(rocketIcon); // Update GIF label with rocket icon
				startBetting(); // Start the betting process
			} catch (NumberFormatException ex) {
				// Handle invalid input
				JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
			}
		} else if (e.getSource() == cashOutButton) {
			// Handle cash out button click
			cashOut(); // Perform cash out action
		} else if (e.getSource() == timer) {
			// Handle timer event for updating multiplier
			if (!cashOut && tempFloat <= payoutMultiplier) {
				tempFloat += 0.01; // Increment multiplier
				multiplierTextField.setText(df.format(tempFloat)); // Update multiplier text field
			} else {
				// Stop the timer when cash out or crash condition is met
				timer.stop();
				if (tempFloat > payoutMultiplier) {
					gifLabel.setIcon(explosionIcon); // Update GIF label with explosion image
					JOptionPane.showMessageDialog(this, "Game crashed at multiplier: " + df.format(tempFloat));
				} else if (cashOut) {
					gifLabel.setIcon(rocketIconStill);
					JOptionPane.showMessageDialog(this, "Cashed out at multiplier: " + df.format(tempFloat)
							+ "\nMax Multiplier: " + df.format(payoutMultiplier));
					// Update total with payout
					float payout = betAmount * tempFloat;
					total += payout;
					cashTextField.setText(df.format(total)); // Update cash text field
				}
				betButton.setEnabled(true); // Enable bet button
				cashOutButton.setEnabled(false); // Disable cash out button
			}
		}
	}

	// Method to start the betting process
	private void startBetting() {
		cashOut = false; // Reset cash out flag
		tempFloat = 0.0f; // Reset multiplier

		// Generate random multipliers for the game
		maxMultiplier = generateMultiplier();
		payoutMultiplier = (float) (Math.round((Math.random() * (maxMultiplier - 1) + 1) * 100) / 100.0);

		// Create and start the timer with a delay of 100 milliseconds
		timer = new Timer(100, this);
		timer.start();
	}

	// Method to perform cash out action
	private void cashOut() {
		cashOutButton.setEnabled(false); // Disable cash out button
		cashOut = true; // Set cash out flag
	}

	// Instance variables for game state
	private static float tempFloat = 0.0f;
	private static boolean cashOut = false;

	// Method to generate random multiplier within a range
	private float generateMultiplier() {
		double multiplier = (Math.log(1 - Math.random()) / (-0.7));
		return (float) multiplier;
	}

	// Main method to create an instance of CrashGUI
	public static void main(String[] args) {
		new CrashGUI();
	}
}