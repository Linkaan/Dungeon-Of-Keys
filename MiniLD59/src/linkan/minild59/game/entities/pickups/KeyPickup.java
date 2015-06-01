package linkan.minild59.game.entities.pickups;

import linkan.minild59.game.level.Level;

public class KeyPickup extends Pickup {
	
	public KeyPickup(Level level, double x, double y) {
		super(level, x, y, 5+3*8);
	}
}
