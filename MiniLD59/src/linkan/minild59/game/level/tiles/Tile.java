package linkan.minild59.game.level.tiles;

import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;

public abstract class Tile {
	
	public static final Tile[] tiles = new Tile[256];
	public static final Tile VOID = new BasicSolidTile(0, 8, 8, 0xff000000);
	public static final Tile GRASS = new MultiTile(1,1, new int[][]{{4, 6},{5, 6},{6, 6},{7, 6}}, 0xff00ff00);
	public static final Tile STONE = new MultiTile(2,Integer.MAX_VALUE, new int[][]{{0, 5},{1, 5},{2, 5},{3, 5},{4, 5},{5, 5},{6, 5}}, true, 0xff666666);
	public static final Tile WATER = new AnimatedTile(3,15, new int[][]{{0,6},{1, 6},{2, 6},{3, 6},{2, 6},{1, 6}},1000, 0xff0000ff);
	
	protected byte id;
	protected double cost;
	protected boolean solid;
	
	private int levelColour;
	
	public Tile(int id, double cost, boolean isSolid, int levelColour){
		this.id = (byte) id;
		if(tiles[id] != null) throw new RuntimeException("Duplicate tile id on " + id);
		this.cost = cost;
		this.solid = isSolid;
		this.levelColour = levelColour;
		tiles[id] = this;
	}
	
	public byte getId(){
		return this.id;
	}
	
	public double getCost() {
		return this.cost;
	}
	
	public boolean isSolid(){
		return this.solid;
	}
	
	public int getLevelColour(){
		return this.levelColour;
	}
	
	public abstract void update();
	
	public abstract void render(Screen screen, Level level, int x, int y);
}
