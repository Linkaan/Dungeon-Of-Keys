package linkan.minild59.game.level.tiles;

public class BasicSolidTile extends BasicTile {

	public BasicSolidTile(int id, int x, int y, int levelColour) {
		super(id,1000, x, y, levelColour);
		this.solid = true;
	}

}
