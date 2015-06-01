package linkan.minild59.game.level.tiles;

import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;

public class BasicTile extends Tile {
	
	protected int tileId;

	public BasicTile(int id,double cost, int x, int y, int levelColour) {
		super(id, cost, false, levelColour);
		this.tileId = y*8 + x;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		if(0 > x || x >= level.width || 0 > y || y >= level.height) return;
		screen.render(x, y, tileId, 0x00, 0x00, 1, true, false);
	}

	@Override
	public void update() {}
}
