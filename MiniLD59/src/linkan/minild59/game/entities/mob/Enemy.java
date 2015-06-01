package linkan.minild59.game.entities.mob;

import java.util.List;

import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.entities.particle.Emitter;
import linkan.minild59.game.entities.pickups.HealthPickup;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.level.Node;
import linkan.minild59.game.level.tiles.Tile;
import linkan.minild59.game.util.Vector2i;

public class Enemy extends Mob {
	
	private static final int MAX_RADIUS = 10;
	
	private static final int ATTACK_RATE= 60;
	private static final int ATTACK_DMG = 8;
	
	private int scale = 1, tickCount = 0, attackRate;
	private double walkSpeed, swimSpeed, maxHealth;
	private List<Node> path;
	
	protected boolean isSwimming = false;
	protected int hbXmul = 3, hbYmul = 7;
	protected int hbXmod = 2, hbYmod = 7;

	public Enemy(Level level, String name, int x, int y, double walkSpeed, double swimSpeed, double health) {
		super(level, name, x, y, walkSpeed, health);
		this.walkSpeed = walkSpeed;
		this.swimSpeed = swimSpeed;
		this.maxHealth = health;
	}

	@Override
	public void update() {
		if(attackRate > 0) attackRate--;
		double xa = 0, ya = 0;
		int px = (int)level.getPlayer().getX()+8;
		int py = (int)level.getPlayer().getY()+8;
		Vector2i start = new Vector2i((int)(x+8) >> 4, (int)(y+8) >> 4);
		Vector2i goal = new Vector2i(px >> 4, py >> 4);
		double distance = start.getDistance(goal);
		if(distance <= MAX_RADIUS){
			if(tickCount % 3 == 0) path = level.findPath(start, goal);
			if (path != null) {
				if (!path.isEmpty()) {
					Vector2i vec = path.get(path.size() - 1).tile;
					if((int)this.x < vec.getX() << 4) xa += speed;
					if((int)this.x > vec.getX() << 4) xa -= speed;
					if((int)this.y < vec.getY() << 4) ya += speed;
					if((int)this.y > vec.getY() << 4) ya -= speed;
				}else{
					if(attackRate <= 0){
						level.getPlayer().takeDamage(ATTACK_DMG);
						attackRate = ATTACK_RATE;
					}
				}
			}
		}
		if(xa != 0 || ya != 0){
			move(xa, ya);
			isMoving = true;
		}else if(start.equals(goal)){
			isMoving = false;
		}
		if(level.getTile((int)(this.x + 4) >> 4, (int)(this.y + 8) >> 4).getId() == Tile.WATER.getId()){
			speed = swimSpeed;
			isSwimming = true;
		} else if(isSwimming && level.getTile((int)(this.x + 4) >> 4, (int)(this.y + 8) >> 4).getId() != Tile.WATER.getId()){
			speed = walkSpeed;
			isSwimming = false;
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
		if(isSwimming){
			yOffset += 4;
			cut = Screen.BIT_CUT_Y;
		}
		screen.render(xOffset, yOffset, tile, mirrorDir,cut, scale, true, false);
		//screen.renderBox((int)(this.x - level.xOffset-8), (int)(this.y - level.yOffset-8), 32, 3, 0xff93131e, false);
		//screen.renderBox((int)(this.x - level.xOffset-8), (int)(this.y - level.yOffset)-8, (int)(32*(health/maxHealth)), 3, 0xff38fc1c, false);
		screen.render((int)(0 +this.x)-10, (int)this.y-10, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(12+this.x)-10, (int)this.y-10, 6+3*8, 0x00, 0x00, 1, true, true);
		screen.render((int)(24+this.x)-10, (int)this.y-10, 6+3*8, 0x00, 0x00, 1, true, true);
		double phealth = (health/maxHealth)*3;
		for(int i = 0; i < 3;i++){
			if(phealth <= i) return;
			int c = phealth <= (i+0.5) ? Screen.BIT_CUT_X : 0x00;
			screen.render((int)((i*12)+this.x)-10, (int)this.y-10, 6+3*8, 0x00, c, 1, true, false);
		}
		//Font.render(name, screen, xOffset - (name.length() / 2 * 16 - (name.length() % 2 == 0 ? 16 : 8)), yOffset-20, 1, 0xff93131e);
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
		if(health - damage > 0) {health -= damage;} else {
			health = 0;
			level.removeEntity(this);
			if(Math.random() > .5) level.addEntity(new HealthPickup(level, x, y));
		}
		BasicSoundEffect.playSound("/hurt.wav");
		new Emitter(level, x+8, y+8, 50, 44 , 2, 0xFFB41C2B);
	}

	@Override
	public boolean equals(Mob m) {
		return (m instanceof Enemy);
	}

	/**
	@Override
	public boolean hasCollided(int xa, int ya) {
		int xMin = 1;
		int xMax = 7;
		int yMin = 1;
		int yMax = 7;
		for(int x = xMin; x < xMax; x++){
			if(isSolidTile(xa, ya, x, yMin)){
				return true;
			}
		}
		for(int x = xMin; x < xMax; x++){
			if(isSolidTile(xa, ya, x, yMax)){
				return true;
			}
		}
		for(int y = yMin; y < yMax; y++){
			if(isSolidTile(xa, ya, xMin, y)){
				return true;
			}
		}
		for(int y = yMin; y < yMax; y++){
			if(isSolidTile(xa, ya, xMax, y)){
				return true;
			}
		}
		return false;
	}
	*/
}
