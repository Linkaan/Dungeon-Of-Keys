package linkan.minild59.game.entities.pickups;

import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;

public abstract class Pickup extends Entity {
	
	protected int sprite;

	public Pickup(Level level, double x, double y, int sprite) {
		super(level);
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	@Override
	public void render(Screen screen) {
		screen.render((int)x, (int)y, sprite, 0x00, 0x00, 1, true, false);
	}
	
	@Override
	public void update() {
		if(level.getPlayer().getBounds().contains((int)x+8, (int)y+8)){
			BasicSoundEffect.playSound(BasicSoundEffect.SFX_PICKUP, BasicSoundEffect.NORMAL_PRIORITY);
			level.getPlayer().pickup(this);
			level.removeEntity(this);
		}
	}
}
