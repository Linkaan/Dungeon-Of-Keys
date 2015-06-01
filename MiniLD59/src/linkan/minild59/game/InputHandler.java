package linkan.minild59.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
	
	private int mouseX = -1;
	private int mouseY = -1;
	private int mouseB = -1;
	
	public int getX(){
		return mouseX;
	}
	public int getY(){
		return mouseY;
	}
	public int getButton(){
		return mouseB;
	}

	public InputHandler(Game game){
		game.addKeyListener(this);
		game.addMouseListener(this);
		game.addMouseMotionListener(this);
	}
	
	public class Key {
		private boolean pressed = false;
		
		public boolean isPressed(){
			return pressed;
		}

		public void toggle(boolean isPressed) {
			pressed = isPressed;
		}
	}

	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();

	@Override
	public void keyPressed(KeyEvent e) {
		toggle(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		toggle(e.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent e) { }


	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.mouseB = e.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.mouseB = -1;
	}
	
	public void toggle(int keyCode, boolean isPressed) {
		if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
			up.toggle(isPressed);
		}
		if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
			down.toggle(isPressed);
		}
		if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			left.toggle(isPressed);
		}
		if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			right.toggle(isPressed);
		}
	}
}
