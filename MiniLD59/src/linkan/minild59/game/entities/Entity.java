package linkan.minild59.game.entities;

import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;

public abstract class Entity {

	protected double x, y;
	protected Level level;
	
	public Entity(Level level){
		init(level);
	}
	
	public final void init(Level level){
		this.level = level;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public abstract void update();
	public abstract void render(Screen screen);
}
