package linkan.minild59.game.level;

import linkan.minild59.game.util.Vector2i;

public class Room {
	
	public int x1, x2, y1, y2, w, h;
	public Vector2i center;
	
	public Room(int x, int y, int w, int h) {
		this.x1 = x;
		this.x2 = x + w;
		this.y1 = y;
		this.y2 = y + h;
		this.w = w;
		this.h = h;
		this.center = new Vector2i((int)Math.floor((x1+x2)/2),(int)Math.floor((y1+y2)/2));
	}
	
	public boolean intersects(Room room){
		return (x1 <= room.x2 && x2 >= room.x1 && y1 <= room.y2 && room.y2 >= room.y1);
	}
}