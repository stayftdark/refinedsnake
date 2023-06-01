package refinedsnake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX; // COORDINATE OF WHERE APPLE IS LOCATED
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	Random random2;

	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(new Color(155, 171, 216));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);// HOW FAST THE GAME IS RUNNING, ActionListener interface
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		if (running) {
			/*
			 * for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) { // creating grids
			 * g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT); g.drawLine(0, i *
			 * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE); }
			 */
			g.setColor(Color.white);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) { // HEAD
					g.setColor(new Color(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else { // BODY
					g.setColor(new Color(204, 204, 255));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(new Color(61, 165, 217));
			Font font = (new Font("Ink Free", Font.BOLD, 30));
			g.setFont(font);
			FontMetrics metrics = getFontMetrics(g.getFont());
			// PUTS ON THE CENTER OF THE SCREEN
			g.drawString("points: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("points: " + applesEaten)) / 2,
					g.getFont().getSize());
		} else {
			gameOver(g);
		}
	}

	public void newApple() { // CREATE NEW APPLE
		appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

	}

	public void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];

		}
		switch (direction) { // CASE FOR EACH POSSIBLE DIRECTION(UP, DOWN, LEFT, RIGHT)
		case 'U':
			y[0] = y[0] - UNIT_SIZE; // GOES UP
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE; // GOES DOWN
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE; // GOES LEFT
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE; // GOES RIGHT
			break;
		}
	}

	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}

	public void checkCollisions() {
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) { // IF THE HEAD COLLDIDED WITH ANY PART OF THE BODY
				running = false;
			}
		}
		// IF HEAD TOUCHES LEFT BORDER
		if (x[0] < 0) {
			running = false;
		}
		// IF HEAD TOUCHES RIGHT BORDER
		if (x[0] > SCREEN_WIDTH) {
			running = false;
		}
		// IF HEAD TOUCHES BOTTOM BORDER
		if (y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		// IF HEAD TOUCHES TOP BORDER
		if (y[0] < 0) {
			running = false;
		}
		if (!running) {
			timer.stop();
		}
	}

	public void gameOver(Graphics g) {
		random2 = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(new Color(223, 208, 243));
		// GAME OVER TEXT
		g.setColor(new Color(218, 112, 214));
		Font font = (new Font("Ink Free", Font.BOLD, 75));
		g.setFont(font);
		FontMetrics metrics = getFontMetrics(g.getFont());
		// PUTS ON THE CENTER OF THE SCREEN
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER")) / 2, SCREEN_HEIGHT / 2);
		// POINTS AT END OF GAME
		g.setColor(new Color(218, 112, 214));
		Font font2 = (new Font("Ink Free", Font.BOLD, 20));
		g.setFont(font2);
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		// PUTS ON THE CENTER OF THE SCREEN
		g.drawString("FINAL SCORE: " + applesEaten,
				(SCREEN_WIDTH - metrics2.stringWidth("FINAL SCORE: " + applesEaten)) / 2, SCREEN_HEIGHT / 3);
	}

	public void restartGame() {
		setVisible(false);
		new GameFrame();
	}

	public void dispose() {
		JFrame parent = (JFrame) this.getTopLevelAncestor();
		parent.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter { // INNER CLASS
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			case KeyEvent.VK_R:
				restartGame();
				System.out.println("restart");
				break;
			}
		}
	}

}
