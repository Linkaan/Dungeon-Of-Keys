package linkan.minild59.game.util;

import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.level.Rectangle;

public class Debug {
	
	private Debug(){}
	
	public static void drawRect(Screen screen, Rectangle rect, boolean fixed){
		screen.drawRect(rect.x1, rect.y1, rect.w, rect.h, 0xffff0000, fixed);
	}
	
	public static void drawRect(Screen screen, Rectangle rect, int colour, boolean fixed) {
		screen.drawRect(rect.x1, rect.y1, rect.w, rect.h, colour, fixed);
	}
	
	public static void drawRect(Screen screen, int x, int y, int width, int height, boolean fixed){
		screen.drawRect(x, y, width, height, 0xffff0000, fixed);
	}
	
	public static void drawRect(Screen screen, int x, int y, int width, int height, int colour, boolean fixed){
		screen.drawRect(x, y, width, height, colour, fixed);
	}
	
	public static void drawLine(Screen screen, int x0, int y0, int x1, int y1, int colour, boolean fixed){
		screen.drawVectors(Level.BresenhamLine(x0, y0, x1, y1), colour, fixed);
	}

}
