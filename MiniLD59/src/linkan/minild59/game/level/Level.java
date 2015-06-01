package linkan.minild59.game.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import linkan.minild59.game.Game;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.entities.mob.Player;
import linkan.minild59.game.entities.particle.Particle;
import linkan.minild59.game.entities.projectile.Projectile;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.tiles.Tile;
import linkan.minild59.game.util.RayCastingResult;
import linkan.minild59.game.util.Vector2i;

public abstract class Level {
	
	public final byte[] CHECK_ORDER = new byte[]{1, 3, 5, 7, 0, 2, 6, 8};
	
	public volatile boolean loading = true;
	public int width;
	public int height;
	public int xOffset, yOffset;
	public Comparator<Node> nodeSorter = new Comparator<Node>() {
		
		@Override
		public int compare(Node n0, Node n1) {
			if(n1.fCost < n0.fCost) return +1;
			if(n1.fCost > n0.fCost) return -1;
			return 0;
		}
	};
	
	protected List<Entity> entities;
	protected byte[] tiles;
	protected Game game;
	protected Player player;
	//private Enemy enemy;
	
	public Level(Game game, int width, int height, InputHandler input) {
		tiles = new byte[width * height];
		entities = new ArrayList<Entity>();
		this.game = game;
		this.width = width;
		this.height = height;
		//this.generateLevel();
		
		//player = new Player(this,32,32,input);
		//enemy = new Enemy(this, "Enemy", 32, 32, 1, 0.5);
		//this.addEntity(player);
		//this.addEntity(enemy);
	}
	
	public void update(){
		if(this.loading) return;
		for (Entity e : new ArrayList<>(entities)) {
		    e.update();
		}
		this.updateTiles();
	}
	
	public void updateWhileFading(){
		if(this.loading) return;
		for (Entity e : new ArrayList<>(entities)) {
			if(e instanceof Particle || e instanceof Player || e instanceof Projectile) e.update();
		}
		this.updateTiles();
	}
	
	public void updateTiles(){
		for(Tile t : Tile.tiles){
			if(t == null) break;
			t.update();
		}
	}
	
	public void renderTiles(Screen screen, int xOffset, int yOffset){
		if(this.loading) return;
		if(xOffset < 0) xOffset = 0;
		if(xOffset > ((width << 4)- screen.width)) xOffset = ((width << 4)- screen.width);
		if(yOffset < 0) yOffset = 0;
		if(yOffset > ((height << 4)- screen.height)) yOffset = ((height << 4)- screen.height);
		
		screen.setOffset(xOffset, yOffset);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
		for(int y = (yOffset >> 4);  y < (yOffset + screen.height >> 4) + 1; y++){
			for(int x = (xOffset >> 4); x < (xOffset + screen.width >> 4) + 1; x++){
				getTile(x, y).render(screen, this, x << 4, y << 4);
			}
		}
		
		//enemy.debugRender(screen);
		//player.debugRender(screen);
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public void renderEntities(Screen screen){
		if(this.loading) return;
		for (Entity e : new ArrayList<>(entities)) {
		    e.render(screen);
		}
	}
	
	public RayCastingResult RayCast(Vector2i pos, double angle, float rayLength){
		RayCastingResult result = new RayCastingResult();
		result.setCollided(false);
		if(rayLength <= 0){
			result.setCollided(this.hasCollided(pos.getX(), pos.getY()));
			result.setPosition(pos);
			return result;
		}
		double adjacent = pos.getX()+rayLength*Math.cos(angle);
		double opposite = pos.getY()+rayLength*Math.sin(angle);
		List<Vector2i> rayLine = BresenhamLine(pos.getX(), pos.getY(), (int)adjacent, (int)opposite);
		if(!rayLine.isEmpty()){
			for(int rayVectorIndex = 0;rayVectorIndex < rayLine.size();rayVectorIndex++){
				Vector2i rayVector = rayLine.get(rayVectorIndex);
				if(this.hasCollided(rayVector.getX(), rayVector.getY())){
					result.setPosition(rayVector);
					result.setCollided(true);
					break;
				}
			}
		}
		return result;
	}
	
	private int hbXmul = 9, hbYmul = 9;
	private int hbXmod = 3, hbYmod = 5;
	
	private boolean hasCollided(double x, double y) {
	      boolean solid = false;
	      for (int c = 0; c < 4; c++) {

	         double xt = (x - c % 2 * hbXmul + hbXmod) / 16;
	         double yt = (y - c / 2 * hbYmul + hbYmod) / 16;

	         int ix = (int) Math.ceil(xt);
	         int iy = (int) Math.ceil(yt);

	         if (c % 2 == 0) ix = (int) Math.floor(xt);
	         if (c / 2 == 0) iy = (int) Math.floor(yt);

	         if (this.getTile(ix, iy).isSolid())
	            solid = true;
	      }
	      return solid;
	}
	
	/**
	public static List<Vector2i> BresenhamLine(Vector2i A, Vector2i B){
		List<Vector2i> result = new ArrayList<Vector2i>();
		boolean steep = Math.abs(B.getY() - A.getY()) > Math.abs(B.getX() - A.getX());
		if(steep){
			Vector2i c = new Vector2i(A);
			A.setX(A.getY());
			A.setY(c.getX());
			c.setXY(B);
			B.setX(B.getY());
			B.setY(c.getX());
		}
		if(A.getX() > B.getX()){
			Vector2i c = new Vector2i(A);
			A.setX(B.getX());
			A.setY(B.getY());
			B.setX(c.getX());
			B.setY(c.getY());
		}
		
		int dx = B.getX() - A.getX();
		int dy = Math.abs(B.getY() - A.getY());
		int err = 0;
		int ys;
		int y = A.getY();
		if(A.getY() < B.getY()) ys = 1; else ys = -1;
		for(int x = A.getX(); x <= B.getX(); x++){
			if(steep) result.add(new Vector2i(y, x));
			else result.add(new Vector2i(x, y));
			err += dy;
			if(2 * err >= dx){
				y += ys;
				err -= dx;
			}
		}
		return result;
	}
	*/
	
	public static List<Vector2i> BresenhamLine(int x1, int y1, int x2, int y2){
		List<Vector2i> result = new ArrayList<Vector2i>();
		int dy = y2 - y1;
		int dx = x2 - x1;
		int xs, ys;
		
		if(dy < 0) {dy = -dy; ys = -1;} else { ys = 1;}
		if(dx < 0) {dx = -dx; xs = -1;} else { xs = 1;}
		dy <<= 1;
		dx <<= 1;
		
		result.add(new Vector2i(x1, y1));
		if(dx > dy){
			int fraction = dy - (dx >> 1);
			while(x1 != x2){
				if(fraction >= 0){
					y1 += ys;
					fraction -= dx;
				}
				x1 += xs;
				fraction += dy;
				result.add(new Vector2i(x1, y1));
			}
		}else{
			int fraction = dx - (dy >> 1);
			while(y1 != y2){
				if(fraction >= 0){
					x1 += xs;
					fraction -= dy;
				}
				y1 += ys;
				fraction += dx;
				result.add(new Vector2i(x1, y1));
			}
		}
		return result;
	}
	
	public List<Node> findPath(Vector2i start, Vector2i goal){
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		Node current = new Node(start, null, 0, start.getDistance(goal));
		openList.add(current);
		while(!openList.isEmpty()){
			Collections.sort(openList, nodeSorter);
			current = openList.get(0);
			if(current.tile.equals(goal)) {
				List<Node> path = new ArrayList<Node>();
				while(current.parent != null){
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear(); 
				return path;
			}
			openList.remove(current);
			closedList.add(current);
			boolean us = false, ls = false, rs = false, ds = false;
			for(int index = 0; index < 8; index++){
				int i = this.CHECK_ORDER[index];
				int x = current.tile.getX();
				int y = current.tile.getY();
				int xi = (i % 3) - 1;
				int yi = (i / 3) - 1;
				Tile at = getTile(x + xi, y + yi);
				if(at == null) continue;
				Vector2i a = new Vector2i(x+xi,y+yi);
				if(i == 0 && us && ls) continue;
				if(i == 2 && us && rs) continue;
				if(i == 6 && ds && ls) continue;
				if(i == 8 && ds && rs) continue;
				if(at.isSolid()){
					if(i == 1) us = true;
					if(i == 3) ls = true;
					if(i == 5) rs = true;
					if(i == 7) ds = true;
					continue;
				}
				double gCost = current.gCost + (current.tile.getDistance(a) > 1 ? 0.95 : 1) + at.getCost();
				double hCost = a.getDistance(goal);
				Node node = new Node(a, current, gCost, hCost);
				if(vecInList(closedList, a) && gCost >= node.gCost) continue;
				if(!vecInList(openList, a) || gCost < node.gCost) openList.add(node);
			}
		}
		closedList.clear();
		return null;
	}
	
	public void addEntity(Entity entity){
		this.entities.add(entity);
	}
	
	public void removeEntity(Entity entity){
		this.entities.remove(entity);
	}
	
	public Tile getTile(int x, int y){
		if(0 > x || x >= width || 0 > y || y >= height) return Tile.VOID;
		return Tile.tiles[tiles[x + y * width]];
	}

	public boolean vecInList(List<Node> list, Vector2i vector){
		for(Node n : list){
			if(n.tile.equals(vector)) return true;
		}
		return false;
	}

	public List<Entity> getEntities() {
		return new ArrayList<>(entities);
	}

	public void won() {
		game.won();
	}
}
