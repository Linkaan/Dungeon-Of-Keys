package linkan.minild59.game.entities.projectile;

import linkan.minild59.game.Game;
import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.entities.mob.Mob;
import linkan.minild59.game.entities.particle.Emitter;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.graphics.SpriteSheet;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.util.Utility;

public class DefaultProjectile extends Projectile {
	
	public static final int FIRE_RATE = 15;
	
	protected int hbXmul = 9, hbYmul = 9;
	protected int hbXmod = 3, hbYmod = 5;
	
	protected SpriteSheet sprite;
	
	//private int tickCount = 0;

	public DefaultProjectile(Level level, Mob owner,double x, double y, double dir) {
		super(level, owner, x, y, dir);
		range = Utility.random(96, 144);
		speed = 2;
		damage = 4;
		
		sprite = Game.SPRITE_SHEET.rotate(7 + 3*8, angle);
		
		nx = speed * Math.cos(angle); 
		ny = speed * Math.sin(angle);
	}

	@Override
	public void update() {
		if(this.hasCollided(nx, ny)) {
			new Emitter(level, x+5, y+5, 50, 44 , 2, 0xffaaaaaa);
			BasicSoundEffect.playSound("/hit.wav");
			level.removeEntity(this);
		}
		//if(tickCount % 10 == 0) sprite = sprite.rotate(7+3*8, Math.PI / 20.0);
		hit();
		move();
		//tickCount++;
	}

	@Override
	public void render(Screen screen) {
		//Debug.drawRect(screen, (int)x, (int)y, 16, 16,0xffff00ff, true);
		screen.render((int)x, (int)y, 7 + 3*8, 0x00, 0x00,1, sprite);
	}
	
	@Override
	public boolean hasCollided(double xa, double ya) {
	      boolean solid = false;
	      for (int c = 0; c < 4; c++) {

	         double xt = ((x + xa) - c % 2 * hbXmul + hbXmod) / 16;
	         double yt = ((y + ya) - c / 2 * hbYmul + hbYmod) / 16;

	         int ix = (int) Math.ceil(xt);
	         int iy = (int) Math.ceil(yt);

	         if (c % 2 == 0) ix = (int) Math.floor(xt);
	         if (c / 2 == 0) iy = (int) Math.floor(yt);

	         if (level.getTile(ix, iy).isSolid())
	            solid = true;
	      }
	      return solid;
	}
}
