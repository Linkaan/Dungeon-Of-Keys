package linkan.minild59.game.entities.mob;

import linkan.minild59.game.Game;
import linkan.minild59.game.Game.STATE;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.entities.particle.Emitter;
import linkan.minild59.game.entities.pickups.HealthPickup;
import linkan.minild59.game.entities.pickups.KeyPickup;
import linkan.minild59.game.entities.pickups.Pickup;
import linkan.minild59.game.entities.projectile.DefaultProjectile;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.level.tiles.Tile;

public class Player extends Mob {
	
	public static final double WALK_SPEED = 1.5;
	public static final double SWIM_SPEED = 0.5;
	public static final double MAX_HEALTH = 100.0;
	
	public static final int ATTACK_RATE= 15;
	public static final int ATTACK_DMG = 4;
	
	public boolean boss = false;
	public int keysPicked = 0;
	
	private InputHandler input;
	private int scale = 1, tickCount = 0, fireRate, attackRate;
	private int[] yOffsets = new int[5];
	
	protected boolean isSwimming = false;
	protected int hbXmul = 3, hbYmul = 7;
	protected int hbXmod = 2, hbYmod = 7;

	public Player(Level level, int x, int y, InputHandler input) {
		super(level, "Player", x, y, WALK_SPEED, MAX_HEALTH);
		this.input = input;
		fireRate = DefaultProjectile.FIRE_RATE;
		attackRate = ATTACK_RATE;
	}

	@Override
	public void update() {
		if(fireRate > 0) fireRate--;
		if(attackRate > 0) attackRate--;
		double xa = 0, ya = 0;
		if (input.up.isPressed())
			ya -= speed;
		if (input.down.isPressed())
			ya += speed;
		if (input.left.isPressed())
			xa -= speed;
		if (input.right.isPressed())
			xa += speed;
		this.updateAttacking();
		
		for(int i = 0; i < 5; i++){
			if(((health/MAX_HEALTH)*5) <= 1.5){
				yOffsets[i] = (int) (2 * Math.sin(Math.toDegrees((tickCount-i*5)/4)));
			}else{
				yOffsets[i] = 0;
			}
		}
		
		if(xa != 0 || ya != 0){
			move(xa, ya);
			isMoving = true;
		}else{
			isMoving = false;
		}
		if(level.getTile((int)(this.x + 8) >> 4, (int)(this.y + 8) >> 4).getId() == Tile.WATER.getId()){
			speed = SWIM_SPEED;
			isSwimming = true;
		}
		if(isSwimming && level.getTile((int)(this.x + 8) >> 4, (int)(this.y + 8) >> 4).getId() != Tile.WATER.getId()){
			speed = WALK_SPEED;
			isSwimming = false;
		}
		
		tickCount++;
	}
	
	private void updateAttacking(){
		if(input.getButton() == 3 && attackRate <= 0){
			attackRate = ATTACK_RATE;
			for(Entity ent : level.getEntities()){
				if(!(ent instanceof Mob) || ent instanceof Player) continue;
				Mob other = (Mob) ent;
				if(Math.sqrt(Math.abs((x - ent.getX()) * (x - ent.getX()) + (y - ent.getY()) * (y - ent.getY()))) <= 22.627416998){
					other.takeDamage(ATTACK_DMG);
				}
			}
		}else if(input.getButton() == 1 && fireRate <= 0){
			//double dx = input.getX() - (Game.WIDTH * Game.SCALE)/2;
			//double dy = input.getY() - (Game.HEIGHT * Game.SCALE)/2;
			double dx = input.getX() - (this.x - level.xOffset)*Game.SCALE;
			double dy = input.getY() - (this.y - level.yOffset)*Game.SCALE;
			shoot(x, y, Math.atan2(dy, dx));
			fireRate = DefaultProjectile.FIRE_RATE;
		}
	}
	
	public void pickup(Pickup pickup){
		if(pickup instanceof HealthPickup){
			if(health + HealthPickup.HEALTH_POINTS <= MAX_HEALTH) {health += HealthPickup.HEALTH_POINTS;} else {health = MAX_HEALTH;}
			new Emitter(level, x+8, y+8, 50, 44 , 2, 0xff38fc1c);
		}else if(pickup instanceof KeyPickup){
			keysPicked++;
			new Emitter(level, x+8, y+8, 128, 44 , 2, 0xffc19132);
		}
	}
 
	@Override
	public void render(Screen screen) {
		int tile = 0;
		int walkingSpeed = 4;
		int mirrorDir = 0x00, cut = 0x00;
		int xOffset = (int)(x), yOffset = (int)(y);
		if(movingDir == 0){
			tile = 6;
		}else if(movingDir == 2){
			tile = 3;
			mirrorDir = Screen.BIT_MIRROR_X;
		}else if(movingDir == 3){
			tile = 3;
		}
		if(isMoving) tile += 1 + ((numSteps >> walkingSpeed) & 1);
		if(isSwimming){
			yOffset += 4;
			if(15 <= tickCount % 60 && tickCount % 60 < 30){
				yOffset -= 1;
			}else if(!(tickCount % 60 < 15) && !(30 <= tickCount % 60 && tickCount % 60 < 45)){
				yOffset -= 1;
			}
			cut = Screen.BIT_CUT_Y;
		}
		screen.render(xOffset, yOffset, tile, mirrorDir,cut, scale, true, false);
		//Debug.drawRect(screen, getBounds(), true);
		//screen.render((int)(this.x - level.xOffset-8), (int)(this.y - level.yOffset-8), 32, 3, 0xff93131e, false);
		//screen.render((int)(this.x - level.xOffset-8), (int)(this.y - level.yOffset)-8, (int)(32*(health/MAX_HEALTH)), 3, 0xff38fc1c, false);
		if(Game.gameState == STATE.Game){
			screen.render(0 , Game.HEIGHT-16, 5+3*8, 0x00, 0x00, 1, false, true);
			screen.render(12, Game.HEIGHT-16, 5+3*8, 0x00, 0x00, 1, false, true);
			screen.render(24, Game.HEIGHT-16, 5+3*8, 0x00, 0x00, 1, false, true);
			screen.render(36, Game.HEIGHT-16, 5+3*8, 0x00, 0x00, 1, false, true);
			screen.render(48, Game.HEIGHT-16, 5+3*8, 0x00, 0x00, 1, false, true);
			for(int i = 0; i < keysPicked; i++){
				screen.render(i*12, Game.HEIGHT-16, 5+3*8, 0x00, 0x00, 1, false, false);
			}
		}
		double phealth = (health/MAX_HEALTH)*5;
		screen.render(0 , yOffsets[0], 6+3*8, 0x00, 0x00, 1, false, true);
		screen.render(12, yOffsets[1], 6+3*8, 0x00, 0x00, 1, false, true);
		screen.render(24, yOffsets[2], 6+3*8, 0x00, 0x00, 1, false, true);
		screen.render(36, yOffsets[3], 6+3*8, 0x00, 0x00, 1, false, true);
		screen.render(48, yOffsets[4], 6+3*8, 0x00, 0x00, 1, false, true);
		for(int i = 0; i < 5;i++){
			if(phealth <= i) return;
			int c = phealth <= (i+0.5) ? Screen.BIT_CUT_X : 0x00;
			screen.render(i*12, yOffsets[i], 6+3*8, 0x00, c, 1, false, false);
		}
		//Font.render(name, screen, xOffset - (name.length() / 2 * 16 - (name.length() % 2 == 0 ? 16 : 8)), yOffset-20, 1, 0xff38fc1c);
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
		if(health - damage > 0) {health -= damage;BasicSoundEffect.playSound(BasicSoundEffect.SFX_HURT, BasicSoundEffect.NORMAL_PRIORITY);} else {
			health = 0;
			BasicSoundEffect.playSound(BasicSoundEffect.SFX_LOSE, BasicSoundEffect.HIGH_PRIORITY);
			Game.gameState = STATE.End;
		}
		new Emitter(level, x+8, y+8, 50, 44 , 2, 0xff93131e);
	}

	@Override
	public boolean equals(Mob m) {
		return (m instanceof Player);
	}

	/**
	@Override
	public boolean hasCollided(int xa, int ya) {
		int xMin = 0;
		int xMax = 16;
		int yMin = 0;
		int yMax = 16;
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
