package linkan.minild59.game.level.tiles;

import linkan.minild59.game.Game;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;

public class MultiTile extends BasicTile {
	
	private int[][] multiTileCoords;
	protected int[] tileIndices;

	public MultiTile(int id, double cost, int[][] multiCoord, int levelColour) {
		this(id, cost, multiCoord, false, levelColour);
	}

	public MultiTile(int id, double cost, int[][] multiCoords, boolean isSolid, int levelColour) {
		super(id, cost, multiCoords[0][0], multiCoords[0][1], levelColour);
		this.multiTileCoords = multiCoords;
		this.solid = isSolid;
		this.tileIndices = new int[Game.LEVEL_WIDTH * Game.LEVEL_HEIGHT];
		for(int y = 0; y < Game.LEVEL_HEIGHT; y++){
			for(int x = 0; x < Game.LEVEL_WIDTH; x++){
				this.tileIndices[x+y*Game.LEVEL_WIDTH] = (int) Math.abs((Math.cos(x + y * Game.WIDTH) * Math.PI)) % multiTileCoords.length;
			}
		}
	}
	
	@Override
	public void render(Screen screen, Level level, int x, int y){
		int tileIndex = this.tileIndices[(x>>4)+(y>>4)*Game.LEVEL_WIDTH];
		screen.render(x, y, (multiTileCoords[tileIndex][0] + (multiTileCoords[tileIndex][1] * 8)), 0x00, 0x00, 1, true, false);
	}
}
