package linkan.minild59.game.entities.particle;

import linkan.minild59.game.level.Level;

public class Emitter {
	
	public Emitter(Level level, double x, double y, int amount, int life, int size, int colour) {
		for(int i = 0; i < amount; i++){
			level.addEntity(new Particle(level, x, y, life, size, colour));
		}
	}
}
