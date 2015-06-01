package linkan.minild59.game.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linkan.minild59.game.Game;
import linkan.minild59.game.Game.STATE;
import linkan.minild59.game.graphics.SpriteSheet;
import linkan.minild59.game.InputHandler;

public class Menu {
	
	private Game game;
	private InputHandler input;
	private Font header, body, text;
	
	private int lastButton = -1;
	
	//private List<MenuParticle> particles = new ArrayList<MenuParticle>();

	public Menu(Game game, InputHandler input, Font baseFont) {
		this.game = game;
		this.input = input;
		if(baseFont != null){			
			try {
				this.header = baseFont.deriveFont(48f);
				this.body = baseFont.deriveFont(32f);
				this.text = baseFont.deriveFont(16f);
			} catch (Exception e){
				this.header = new Font("Verdana", Font.PLAIN, 48);
				this.body = new Font("Verdana", Font.PLAIN, 32);
				this.text = new Font("Verdana", Font.PLAIN, 16);
				e.printStackTrace();
			}
		}else{
			this.header = new Font("Verdana", Font.PLAIN, 48);
			this.body = new Font("Verdana", Font.PLAIN, 32);
			this.text = new Font("Verdana", Font.PLAIN, 16);
		}
		/**
		for(int i = 0; i < 300; i++){
			particles.add(new MenuParticle(Utility.random(0, Game.WIDTH*Game.SCALE), Utility.random(0, Game.HEIGHT*Game.SCALE), 8, 0xffaaaaaa, new linkan.minild59.game.level.Rectangle[]{
					new linkan.minild59.game.level.Rectangle(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3),
					new linkan.minild59.game.level.Rectangle(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3),
					new linkan.minild59.game.level.Rectangle(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3),
			}));
		}
		*/
	}
	
	public boolean mouseOver(int x, int y, int w, int h){
		if(input.getY() > y && input.getY() < y + h){
			if(input.getX() > x && input.getX() < x + w){
				return true;
			}			
		}
		return false;
	}
	
	public void update(){
		if(lastButton != input.getButton() && (lastButton = input.getButton()) == 1){
			if(Game.gameState == STATE.Menu && mouseOver(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2), (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3)){
				game.fadeIn(25);
				Game.gameState = STATE.Game;
			}else if(mouseOver(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2), (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3)){
				if(Game.gameState == STATE.Menu){
					Game.gameState = STATE.Options;
				}else if(Game.gameState == STATE.Options){
					SpriteSheet spriteSheet = new SpriteSheet("/sprite_sheet.png"); 
					Game.SPRITE_SHEET.image = spriteSheet.image;
					Game.SPRITE_SHEET.pixels = spriteSheet.pixels;
				}else if(Game.gameState == STATE.End){
					game.retry();
					game.fadeIn(25);
					Game.gameState = STATE.Game;
				}
			}else if(mouseOver(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2), (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3)){
				if(Game.gameState == STATE.Menu){
					System.exit(0);
				}else{
					if(Game.gameState == STATE.End){
						game.level.getPlayer().boss = false;
						game.retry();
					}
					Game.gameState = STATE.Menu;
				}
			}
		}
		/**
		for(MenuParticle p : particles){
			p.update();
		}
		*/
	}
	
	public void render(Graphics g){
		/**
		for(MenuParticle p : particles){
			p.render(g);
		}
		*/
		g.setColor(Color.WHITE);
		
		// header
		g.setFont(header);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r;
		if(Game.gameState == STATE.Menu){			
			r = fm.getStringBounds(Game.TITLE, g);
			g.drawString(Game.TITLE, (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, fm.getAscent());
		}else if(Game.gameState == STATE.Options){
			r = fm.getStringBounds("Options", g);
			g.drawString("Options", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, fm.getAscent());			
		}else if(Game.gameState == STATE.End){
			r = fm.getStringBounds("Game Over!", g);
			g.drawString("Game Over!", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, fm.getAscent());			
		}
		
		// body
		g.setFont(body);
		fm = g.getFontMetrics();
		if(Game.gameState == STATE.Menu){
			r = fm.getStringBounds("Start", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Start", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Options/Help", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Options/Help", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1,(Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Exit", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Exit", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1,(Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
		}else if(Game.gameState == STATE.Options){
			g.setFont(text);
			fm = g.getFontMetrics();
			r = fm.getStringBounds("Use arrow keys or WASD keys to move. Use your mouse", g);
			g.drawString("Use arrow keys or WASD keys to move. Use your mouse", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*4 - (int)r.getHeight()*4 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("to aim and left click to shoot, and right click to", g);
			g.drawString("to aim and left click to shoot, and right click to", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*4 - (int)r.getHeight()*3 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("attack. Find three         to unlock the boss level.", g);
			g.drawString("attack. Find three         to unlock the boss level.", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*4 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			Rectangle2D r2 = fm.getStringBounds("attack. Find three ", g);
			g.drawImage(Game.SPRITE_SHEET.image.getSubimage(5<<4, 3<<4, 16, 16), ((Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1)+(int)r2.getWidth(), (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*4 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent()-40,64,64, null);
			//g.drawRect(((Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1)+(int)r2.getWidth(), (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*4 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent()-40,64,64);
			
			r = fm.getStringBounds("Press button in the upper-right corner to select", g);
			g.drawString("Press button in the upper-right corner to select", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3 - (int)r.getHeight()*2 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("a random spritesheet (runs asynchronously). Or press", g);
			g.drawString("a random spritesheet (runs asynchronously). Or press", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3 - (int)r.getHeight() + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("the button below to select the default spritesheet.", g);
			g.drawString("the button below to select the default spritesheet.", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			g.setFont(body);
			fm = g.getFontMetrics();
			
			r = fm.getStringBounds("Default", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2),  (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Default", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1,(Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Back", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2),  (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Back", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1,(Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
		}else if(Game.gameState == STATE.End){
			r = fm.getStringBounds("You lost!", g);
			g.drawString("You lost!", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*3 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Retry", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2) , (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Retry", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1,(Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*2 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Main Menu", g);
			g.drawRect(((Game.WIDTH*Game.SCALE)>>1)-((Game.WIDTH*Game.SCALE)>>2),  (Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1, ((Game.WIDTH*Game.SCALE)>>1), (Game.HEIGHT*Game.SCALE)>>3);
			g.drawString("Main Menu", (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1,(Game.HEIGHT*Game.SCALE)-((Game.HEIGHT*Game.SCALE+128)>>3)*1 + (((Game.HEIGHT*Game.SCALE)>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
		}
	}

}
