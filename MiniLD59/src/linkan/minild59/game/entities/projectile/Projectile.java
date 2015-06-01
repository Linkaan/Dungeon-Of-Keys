package linkan.minild59.game.entities.projectile;

import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.entities.mob.Mob;
import linkan.minild59.game.entities.mob.Player;
import linkan.minild59.game.level.Level;

public abstract class Projectile extends Entity {
	
	protected final double xOrigin, yOrigin;
	protected double angle, nx, ny, speed, range, distance, damage;
	
	protected Mob owner;

	public Projectile(Level level, Mob owner, double x, double y, double dir) {
		super(level);
		this.xOrigin = x;
		this.yOrigin = y;
		this.angle = dir;
		this.owner = owner;
		this.x = x;
		this.y = y;
		BasicSoundEffect.playSound(BasicSoundEffect.SFX_SHOOT, BasicSoundEffect.LOW_PRIORITY);
	}
	
	protected abstract boolean hasCollided(double xa, double ya);
	
	protected void move() {
		x += nx;
		y += ny;
		
		if((distance = Math.sqrt(Math.abs((xOrigin - x) * (xOrigin - x) + (yOrigin - y) * (yOrigin - y)))) >= range) level.removeEntity(this);
	}
	
	protected void hit(){
		/**
		if(!(owner instanceof Player) && level.getPlayer().getBounds().contains((int)x+8, (int)y+8)){
			level.getPlayer().takeDamage(damage);
			level.removeEntity(this);
		}
		*/ 
		for(Entity ent : level.getEntities()){
			if(!(ent instanceof Mob)) continue;
			Mob other = (Mob) ent;
			if(!owner.equals(other) && other.getBounds().contains((int)x+8, (int)y+8)){
				if(!(owner instanceof Player) && !(ent instanceof Player)) continue;
				other.takeDamage(damage);
				level.removeEntity(this);
				return;
			}
		}
	}
}
