import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Snake extends JPanel implements ActionListener, KeyListener {
    int width, height;
    static int tile_size = 25;
    Tile head;
    Tile food;
    static Random ran = new Random();
    Timer gameLoop;
    int vel_x, vel_y;
    ArrayList<Tile> body;
    boolean gameOver = false;
    boolean paused = false;

    Snake(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(this.width, this.height));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow(); // Ensure the panel has focus for key events

        gameLoop = new Timer(100, this);
        resetGame();
        gameLoop.start();
    }

    private class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void placeFood() {
        food.x = ran.nextInt(width / tile_size);
        food.y = ran.nextInt(height / tile_size);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw snake head
        g.setColor(Color.YELLOW);
        g.fill3DRect(head.x * tile_size, head.y * tile_size, tile_size, tile_size, true);

        // Draw food
        g.setColor(Color.WHITE);
        g.fill3DRect(food.x * tile_size, food.y * tile_size, tile_size, tile_size, true);

        // Draw snake body
        for (Tile snakePart : body) {
            g.fill3DRect(snakePart.x * tile_size, snakePart.y * tile_size, tile_size, tile_size, true);
        }

        // Draw score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over: " + body.size() + " | Press R to Restart", tile_size - 16, tile_size);
        } else {
            g.drawString("Score: " + body.size(), tile_size - 16, tile_size);
        }

        // Draw pause message
        if (paused && !gameOver) {
            g.setColor(Color.YELLOW);
            g.drawString("Paused", width / 2 - 30, height / 2);
        }
    }

    public void move() {
        if (paused) return;

        // Snake eats food
        if (collision(head, food)) {
            body.add(new Tile(food.x, food.y));
            placeFood();
            if (gameLoop.getDelay() > 50) {
                gameLoop.setDelay(gameLoop.getDelay() - 1); // Increase speed
            }
        }

        // Move snake body
        for (int i = body.size() - 1; i >= 0; i--) {
            Tile snakePart = body.get(i);
            if (i == 0) {
                snakePart.x = head.x;
                snakePart.y = head.y;
            } else {
                Tile preSnakePart = body.get(i - 1);
                snakePart.x = preSnakePart.x;
                snakePart.y = preSnakePart.y;
            }
        }

        // Move snake head
        head.x += vel_x;
        head.y += vel_y;

        // Game over conditions
        for (Tile snakePart : body) {
            if (collision(head, snakePart)) {
                gameOver = true;
            }
        }
        if (head.x < 0 || head.x >= width / tile_size || head.y < 0 || head.y >= height / tile_size) {
            gameOver = true;
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void resetGame() {
        head = new Tile(5, 5);
        food = new Tile(10, 10);
        body = new ArrayList<>();
        vel_x = 1; // Initial velocity to the right
        vel_y = 0;
        placeFood();
        gameOver = false;
        paused = false;
        gameLoop.setDelay(200); // Reset speed
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP && vel_y == 0) {
            vel_x = 0;
            vel_y = -1;
        } else if (key == KeyEvent.VK_DOWN && vel_y == 0) {
            vel_x = 0;
            vel_y = 1;
        } else if (key == KeyEvent.VK_LEFT && vel_x == 0) {
            vel_x = -1;
            vel_y = 0;
        } else if (key == KeyEvent.VK_RIGHT && vel_x == 0) {
            vel_x = 1;
            vel_y = 0;
        } else if (key == KeyEvent.VK_P) {
            paused = !paused;
        } else if (key == KeyEvent.VK_R && gameOver) {
            resetGame();
            gameLoop.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No implementation needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No implementation needed
    }

    public static void main(String[] args) {
        int width = 500;
        int height = width;

        JFrame frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Snake sg = new Snake(width, height);
        frame.add(sg);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
