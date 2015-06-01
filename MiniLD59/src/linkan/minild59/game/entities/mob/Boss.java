package linkan.minild59.game.entities.mob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.entities.particle.Emitter;
import linkan.minild59.game.entities.projectile.DefaultProjectile;
import linkan.minild59.game.entities.projectile.Projectile;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.level.Node;
import linkan.minild59.game.level.Rectangle;
import linkan.minild59.game.level.tiles.Tile;
import linkan.minild59.game.util.Utility;
import linkan.minild59.game.util.Vector2i;

public class Boss extends Mob {
	
	private static final double WALK_SPEED = 1.0;
	private static final double MAX_HEALTH = 200.0;
	
	private static final int ATTACK_RATE= 60;
	private static final int ATTACK_DMG = 16;
	
	private int scale = 4, tickCount = 0, attackRate, fireRate;
	private double lastHP = 6;
	private List<Node> path;
	
	protected int hbXmul = 3, hbYmul = 7;
	protected int hbXmod = 2, hbYmod = 7;

	public Boss(Level level, int x, int y) {
		super(level, "Boss", x, y, WALK_SPEED, MAX_HEALTH);
		fireRate = DefaultProjectile.FIRE_RATE;
	}

	@Override
	public void update() {
		if(attackRate > 0) attackRate--;
		if(fireRate > 0) fireRate--;
		double xa = 0, ya = 0;
		int px = (int)level.getPlayer().getX()+8;
		int py = (int)level.getPlayer().getY()+8;
		Vector2i start = new Vector2i((int)(x+8) >> 4, (int)(y+8) >> 4);
		Vector2i goal = new Vector2i(px >> 4, py >> 4);
		if(tickCount % 3 == 0) path = this.findPath(start, goal);
		if (path != null) {
			if (!path.isEmpty()) {
				Vector2i vec = path.get(path.size() - 1).tile;
				if(level.getTile(vec.getX(), vec.getY()).getId() != Tile.WATER.getId()){
					if((int)this.x < vec.getX() << 4) xa += speed;
					if((int)this.x > vec.getX() << 4) xa -= speed;
					if((int)this.y < vec.getY() << 4) ya += speed;
					if((int)this.y > vec.getY() << 4) ya -= speed;
				}else{
					if(fireRate <= 0){
						double dx = px - x;
						double dy = py - y;
						double angle = Math.atan2(dy, dx);
						shoot(x, y, angle);
						fireRate = DefaultProjectile.FIRE_RATE;
					}
				}
			}else{
				if(attackRate <= 0){
					level.getPlayer().takeDamage(ATTACK_DMG);
					attackRate = ATTACK_RATE;
				}
			}
		}
		
		if(xa != 0 || ya != 0){
			move(xa, ya);
			isMoving = true;
		}else{
			isMoving = false;
		}
		tickCount++;
	}

	@Override
	public void render(Screen screen) {
		int tile = 9;
		int walkingSpeed = 4;
		int mirrorDir = 0x00, cut = 0x00;
		int xOffset = (int)(x), yOffset = (int)(y);
		if(movingDir == 0){
			tile = 15;
		}else if(movingDir == 2){
			tile = 12;
			mirrorDir = Screen.BIT_MIRROR_X;
		}else if(movingDir == 3){
			tile = 12;
		}
		if(isMoving)tile += 1 + ((numSteps >> walkingSpeed) & 1);
		screen.render(xOffset, yOffset, tile, mirrorDir,cut, scale, true, false);
		//Debug.drawRect(screen, getBounds(), 0xff38fc1c, true);
		//Font.render(name, screen, xOffset - (name.length() / 2 * 16 - 4), yOffset-40, 2, 0xff93131e, true);
		//screen.renderBox((int)(this.x - level.xOffset-8), (int)(this.y - level.yOffset-8), 32, 3, 0xff93131e, false);
		//screen.renderBox((int)(this.x - level.xOffset-8), (int)(this.y - level.yOffset)-8, (int)(32*(health/maxHealth)), 3, 0xff38fc1c, false);
		screen.render((int)(0 +this.x)-32, (int)this.y-24, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(12+this.x)-32, (int)this.y-24, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(24+this.x)-32, (int)this.y-24, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(36+this.x)-32, (int)this.y-24, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(48+this.x)-32, (int)this.y-24, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(60+this.x)-32, (int)this.y-24, 6+3*8, 0x00, 0x00, 1, true, true);
		double phealth = (health/MAX_HEALTH)*6;
		for(int i = 0; i < 6;i++){
			if(phealth <= i) return;
			int c = phealth <= (i+0.5) ? Screen.BIT_CUT_X : 0x00;
			screen.render((int)((i*12)+this.x)-32, (int)this.y-24, 6+3*8, 0x00, c, 1, true, false);
		}
	}
	
	public List<Node> findPath(Vector2i start, Vector2i goal){
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		Node current = new Node(start, null, 0, start.getDistance(goal));
		openList.add(current);
		while(!openList.isEmpty()){
			Collections.sort(openList, level.nodeSorter);
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
				int i = level.CHECK_ORDER[index];
				int x = current.tile.getX();
				int y = current.tile.getY();
				int xi = (i % 3) - 1;
				int yi = (i / 3) - 1;
				Tile at = level.getTile(x + xi, y + yi);
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
				double atCost = at.getId() == Tile.WATER.getId() ? Integer.MAX_VALUE : at.getCost();
				double gCost = current.gCost + current.tile.getDistance(a) + atCost;
				double hCost = a.getDistance(goal);
				Node node = new Node(a, current, gCost, hCost);
				if(level.vecInList(closedList, a) && gCost >= node.gCost) continue;
				if(!level.vecInList(openList, a) || gCost < node.gCost) openList.add(node);
			}
		}
		closedList.clear();
		return null;
	}
	
	public Rectangle getBounds(){
		return new Rectangle((int)x-17, (int)y-8, 45, 45);
	}

	@Override
	public boolean hasCollided(double xa, double ya) {
	      boolean solid = false;
	      for (int c = 0; c < 4; c++) {

	         double xt = ((x + xa) - c % 2 * hbXmul + hbXmod) / 16;
	         double yt = ((y + ya) - c / 2 * hbYmul + hbYmod) / 16;

	         int ix = (int) Math.ceil(xt);
	         int iy = (int) Math.ceil(yt);

	         if (c % 2 == 0) ix = (int) Math.floor(xt);
	         if (c / 2 == 0) iy = (int) Math.floor(yt);

	         if (level.getTile(ix, iy).isSolid())
	            solid = true;
	      }
	      return solid;
	}
	
	@Override
	public void takeDamage(double damage) {
		if(health - damage > 0) {
			double phealth = (health - damage)/MAX_HEALTH*6;
			int px = (int)level.getPlayer().getX()+8;
			int py = (int)level.getPlayer().getY()+8;
			if(lastHP > 5 && phealth <= 5){
				Vector2i pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
			}else if(lastHP > 4 && phealth <= 4){
				Vector2i pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Shooter(level, "Shooter", pos.getX(),pos.getY(), 1.0, 0.5, 16.0));
			}else if(lastHP > 3 && phealth <= 3){
				Vector2i pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
			}else if(lastHP > 2 && phealth <= 2){
				Vector2i pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Shooter(level, "Shooter", pos.getX(),pos.getY(), 1.0, 0.5, 16.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Shooter(level, "Shooter", pos.getX(),pos.getY(), 1.0, 0.5, 16.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
			}else if(lastHP > 1 && phealth <= 1){
				Vector2i pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Shooter(level, "Shooter", pos.getX(),pos.getY(), 1.0, 0.5, 16.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Shooter(level, "Shooter", pos.getX(),pos.getY(), 1.0, 0.5, 16.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Shooter(level, "Shooter", pos.getX(),pos.getY(), 1.0, 0.5, 16.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
				pos = randomPosition(px, py);
				new Emitter(level, pos.getX(), pos.getY(), 150, 44 , 2, 0xffaaaaaa);
				level.addEntity(new Enemy(level, "Enemy", pos.getX(),pos.getY(), 1.0, 0.5, 8.0));
			}
			BasicSoundEffect.playSound("/hurt.wav");
			lastHP = phealth;
			health -= damage;
		} else {
			health = 0;
			for(Entity e : level.getEntities()){
				if(e instanceof Enemy){
					new Emitter(level, x+8, y+8, 50, 44 , 2, 0xFFB41C2B);
					level.removeEntity(e);
				}else if(e instanceof Shooter){
					new Emitter(level, x+8, y+8, 50, 44 , 2, 0xFFC7C53C);
					level.removeEntity(e);
				}else if(e instanceof Projectile){
					new Emitter(level, x+5, y+5, 50, 44 , 2, 0xffaaaaaa);
					level.removeEntity(e);
				}
			}
			new Emitter(level, x+8, y+8, 300, 100 , 2, 0xff93131e);
			level.removeEntity(this);
			level.won();
			return;
		}
		new Emitter(level, x+8, y+8, 150, 44 , 2, 0xff93131e);
	}

	private Vector2i randomPosition(int px, int py) {
		int posX = Utility.random(px-64, px+64);
		int posY = Utility.random(py-64, py+64);
		if(level.getTile(posX>>4, posY>>4).isSolid()) return randomPosition(px, py);
		return new Vector2i(posX, posY);
	}

	@Override
	public boolean equals(Mob m) {
		return (m instanceof Boss);
	}

}
