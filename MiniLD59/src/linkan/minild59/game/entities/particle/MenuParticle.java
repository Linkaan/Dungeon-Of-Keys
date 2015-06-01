package linkan.minild59.game.entities.particle;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import linkan.minild59.game.Game;
import linkan.minild59.game.util.Utility;

public class MenuParticle {
	
	private Random random;
	private int size;
	private double x, y, z, xa, ya, za;
	private Color colour;
	
	public MenuParticle(double x, double y, int size, int colour) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.colour = new Color(Utility.random(0x00000000, colour), true);
		
		this.random = new Random();
		this.xa = random.nextGaussian()*2;
		this.ya = random.nextGaussian()*2;
		this.z = random.nextFloat() + 2.0;
	}

	public void update() {
		this.za -= 0.1;
		if (z < 0){
			z = 0;
			za *= -0.55;
			//xa *= 0.4;
			//ya *= 0.4;
		}
		move(x + xa, (y + ya) + (z + za));
	}

	private void move(double x, double y) {
		hasCollided(x, y);
		this.x += xa;
		this.y += ya;
		this.z += za;
	}
	
	public void hasCollided(double x, double y) {
		if(x < 0 || x >= Game.WIDTH*Game.SCALE){
			this.xa *= -0.5;
			this.za *= -0.5;
		}
		if(y < 0 || y >= Game.HEIGHT*Game.SCALE){
			this.ya *= -0.5;
			this.za *= -0.5;
		}
		/**
		for(Rectangle bound : boundaries){
			if(!bound.contains((int)x, (int)y)) continue;
			if(x > bound.x1 && x < bound.x2){
				this.xa *= -0.5;
				this.za *= -0.5;
			}
			if(y > bound.y1 && y < bound.y2){
				this.ya *= -0.5;
				this.za *= -0.5;
			}
		}
		*/
	}

	public void render(Graphics g) {
		g.setColor(colour);
		g.fillRect((int)x, (int)(y - z), size, size);
	}
}
