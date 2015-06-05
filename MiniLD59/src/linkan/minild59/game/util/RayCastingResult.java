package linkan.minild59.game.util;

public class RayCastingResult {
	
	private boolean collided;
	private Vector2i position;
	
	public boolean hasCollided() {
		return collided;
	}
	public void setCollided(boolean collided) {
		this.collided = collided;
	}
	public Vector2i getPosition() {
		return position;
	}
	public void setPosition(Vector2i position) {
		this.position = position;
	}
	
}
