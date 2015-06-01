package linkan.minild59.game.entities.pickups;

import linkan.minild59.game.level.Level;

public class HealthPickup extends Pickup {
	
	public static final double HEALTH_POINTS = 20;
	
	public HealthPickup(Level level, double x, double y) {
		super(level, x, y, 6+3*8);
	}
}
