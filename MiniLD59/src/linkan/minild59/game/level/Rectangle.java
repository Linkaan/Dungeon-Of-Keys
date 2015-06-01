package linkan.minild59.game.level;

import linkan.minild59.game.util.Vector2i;

public class Rectangle {
	
	public int x1, x2, y1, y2, w, h;
	public Vector2i center;
	
	public Rectangle(int x, int y, int w, int h) {
		this.x1 = x;
		this.x2 = x + w;
		this.y1 = y;
		this.y2 = y + h;
		this.w = w;
		this.h = h;
		this.center = new Vector2i((int)Math.floor((x1+x2)/2),(int)Math.floor((y1+y2)/2));
	}
	
	public int size(){
		return w*h;
	}
	
    public boolean contains(int X, int Y) {
        int w = this.w;
        int h = this.h;
        if ((w | h) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        int x = this.x1;
        int y = this.y1;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;
        //    overflow || intersect
        return ((w < x || w > X) &&
                (h < y || h > Y));
    }
	
	public boolean intersects(Rectangle rect){
		return (x1 <= rect.x2 && x2 >= rect.x1 && y1 <= rect.y2 && rect.y2 >= rect.y1);
	}
	
	@Override
	public boolean equals(Object object){
		if(!(object instanceof Rectangle)) return false;
		Rectangle rect = (Rectangle) object;
		if(x1 == rect.x1 && x2 == rect.x2 && y1 == rect.y1 && y2 == rect.y2) return true;
		return false;
	}
}