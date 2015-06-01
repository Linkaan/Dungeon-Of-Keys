package linkan.minild59.game;

import java.applet.Applet;
import java.awt.BorderLayout;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameLauncher extends Applet {
	
	private static Game game = new Game();
	
	@Override
	public void init(){
		this.setLayout(new BorderLayout());
		this.add(game, BorderLayout.CENTER);
		this.setMinimumSize(Game.DIMENSIONS);
		this.setMaximumSize(Game.DIMENSIONS);
		this.setPreferredSize(Game.DIMENSIONS);
	}
	
	@Override
	public void start(){
		game.start();
	}
	
	@Override 
	public void stop(){
		game.stop();
	}
	
	public static void main(String[] args) {
		game.setMinimumSize(Game.DIMENSIONS);
		game.setMaximumSize(Game.DIMENSIONS);
		game.setPreferredSize(Game.DIMENSIONS);
		
		game.frame = new JFrame(Game.TITLE);
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLayout(new BorderLayout());
		game.frame.add(game, BorderLayout.CENTER);
		game.frame.pack();
		game.frame.setResizable(false);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		
		game.start();
	}

}
