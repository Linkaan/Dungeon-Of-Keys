package linkan.minild59.game.entities.mob;

import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.entities.projectile.DefaultProjectile;
import linkan.minild59.game.entities.projectile.Projectile;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.level.Rectangle;
import linkan.minild59.game.level.tiles.Tile;

public abstract class Mob extends Entity{
	
	protected String name;
	protected int movingDir = 1, numSteps = 1, scale = 1;
	protected double speed, health;
	protected boolean isMoving;
	
	protected Rectangle bounds;
	
	public Mob(Level level, String name, int x, int y, double speed, double health) {
		super(level);
		this.name = name;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.health = health;
	}
	
	protected void move(double xa, double ya) {
		if (xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			numSteps--;
			return;
		}
		if (!hasCollided((int)xa, (int)ya)) {

			if (ya < 0) movingDir = 0;
			if (ya > 0) movingDir = 1;
			if (xa < 0) movingDir = 2;
			if (xa > 0) movingDir = 3;

			while (xa != 0) {
				if (Math.abs(xa) > 1) {
					if(!hasCollided(abs(xa), ya)){
						this.x += abs(xa);
					}
					xa -= abs(xa);
				} else {
					if(!hasCollided(abs(xa), ya)){
						this.x += xa;
					}
					xa = 0;
				}
			}
			
			while (ya != 0) {
				if (Math.abs(ya) > 1) {
					if(!hasCollided(xa, abs(ya))){
						this.y += abs(ya);
					}
					ya -= abs(ya);
				} else {
					if(!hasCollided(xa, abs(ya))){
						this.y += ya;
					}
					ya = 0;
				}
			}
		}
		numSteps++;
	}
	
	protected void shoot(double x, double y, double dir){
		int degrees = (int) Math.toDegrees(dir);
		if(degrees >= -45 && degrees < 45) movingDir = 3;
		else if(degrees >= 45 && degrees < 135) movingDir = 1;
		else if(degrees >= -135 && degrees < -45) movingDir = 0;
		else movingDir = 2;
		Projectile p = new DefaultProjectile(level,this, x, y, dir);
		level.addEntity(p);
	}
	
	public Rectangle getBounds(){
		return new Rectangle((int)x, (int)y, 16*scale, 16*scale);
	}
	
	
	public abstract boolean hasCollided(double xa, double ya);
	public abstract boolean equals(Mob m);
	public abstract void takeDamage(double damage);
	
	protected int abs(double value){
		if(value < 0) return -1;
		return 1;
	}
	
	protected boolean isSolidTile(int xa, int ya, int x, int y){
		if(level == null) return false;
		Tile lastTile = level.getTile((int)(this.x) + x >> 4, (int)(this.y) + y >> 4);
		Tile newTile = level.getTile((int)(this.x) + x + xa >> 4, (int)(this.y) + y + ya >> 4);
		if(!lastTile.equals(newTile) && newTile.isSolid()){
			return true;
		}
		return false;
	}
	
	public String getName(){
		return name;
	}
}
