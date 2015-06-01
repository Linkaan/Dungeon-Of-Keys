package linkan.minild59.game.entities.particle;

import java.util.Random;

import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.util.Utility;

public class Particle extends Entity {
	
	private Random random;
	
	protected int life, tickCount, size, colour; // 0xffaaaaaa
	protected double z, xa, ya, za;
	
	public Particle(Level level, double x, double y, int life, int size, int colour) {
		super(level);
		this.x = x;
		this.y = y;
		this.life = life + Utility.random(-10, 10);
		this.size = size;
		this.colour = colour;
		
		this.random = new Random();
		this.xa = random.nextGaussian();
		this.ya = random.nextGaussian();
		this.z = random.nextFloat() + 2.0;
	}

	@Override
	public void update() {
		this.za -= 0.1;
		if (z < 0){
			z = 0;
			za *= -0.55;
			xa *= 0.4;
			ya *= 0.4;
		}
		move(x + xa, (y + ya) + (z + za));
		if(tickCount >= life) level.removeEntity(this);
		tickCount++;
	}

	private void move(double x, double y) {
		if(hasCollided(x, y)){
			this.xa *= -0.5;
			this.ya *= -0.5;
			this.za *= -0.5;
		}
		this.x += xa;
		this.y += ya;
		this.z += za;
	}
	
	public boolean hasCollided(double x, double y) {
	      boolean solid = false;
	      for (int c = 0; c < 4; c++) {

	         double xt = (x - c % 2 * 16) / 16;
	         double yt = (y - c / 2 * 16) / 16;
	         
	         int ix = (int) Math.ceil(xt);
	         int iy = (int) Math.ceil(yt);

	         if (c % 2 == 0) ix = (int) Math.floor(xt);
	         if (c / 2 == 0) iy = (int) Math.floor(yt);

	         if (level.getTile(ix, iy).isSolid())
	            solid = true;
	      }
	      return solid;
	}

	@Override
	public void render(Screen screen) {
		screen.renderBox((int)x, (int)(y - z), size, size, colour, true);
	}
}
