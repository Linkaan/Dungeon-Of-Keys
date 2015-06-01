package linkan.minild59.game.level.tiles;

import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;

public class AnimatedTile extends BasicTile {
	
	private int[][] animationTileCoords;
	private int currentAnimationIndex, animationSwitchDelay;
	private long lastIterationTime;

	public AnimatedTile(int id, int cost, int[][] animationCoords, int animationSwitchDelay, int levelColour) {
		super(id, cost, animationCoords[0][0], animationCoords[0][1], levelColour);
		this.animationTileCoords = animationCoords;
		this.currentAnimationIndex = 0;
		this.animationSwitchDelay = animationSwitchDelay;
		this.lastIterationTime = System.currentTimeMillis();
	}
	
	@Override
	public void update(){
		if((System.currentTimeMillis() - lastIterationTime) >= (animationSwitchDelay)){
			lastIterationTime = System.currentTimeMillis();
			currentAnimationIndex = (currentAnimationIndex + 1) % animationTileCoords.length;
			this.tileId = (animationTileCoords[currentAnimationIndex][0] + (animationTileCoords[currentAnimationIndex][1] * 8));
		}
	}
	
	@Override
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, 0x00, 0x00, 1, true, false);
	}
}
