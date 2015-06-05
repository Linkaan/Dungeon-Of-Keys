package linkan.minild59.game.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linkan.minild59.game.Game;
import linkan.minild59.game.Game.STATE;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.graphics.SpriteSheet;

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
			particles.add(new MenuParticle(Utility.random(0, game.getWidth()), Utility.random(0, game.getHeight()), 8, 0xffaaaaaa, new linkan.minild59.game.level.Rectangle[]{
					new linkan.minild59.game.level.Rectangle(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*3, ((game.getWidth())>>1), (game.getHeight())>>3),
					new linkan.minild59.game.level.Rectangle(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*2, ((game.getWidth())>>1), (game.getHeight())>>3),
					new linkan.minild59.game.level.Rectangle(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*1, ((game.getWidth())>>1), (game.getHeight())>>3),
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
			if(Game.gameState == STATE.Menu && mouseOver(((game.getWidth())>>1)-((game.getWidth())>>2), (game.getHeight())-((game.getHeight()+128)>>3)*3, ((game.getWidth())>>1), (game.getHeight())>>3)){
				game.fadeIn(25);
				game.setCursor(game.invisibleCursor);
				Game.gameState = STATE.Game;
			}else if(mouseOver(((game.getWidth())>>1)-((game.getWidth())>>2), (game.getHeight())-((game.getHeight()+128)>>3)*2, ((game.getWidth())>>1), (game.getHeight())>>3)){
				if(Game.gameState == STATE.Menu){
					Game.gameState = STATE.Options;
				}else if(Game.gameState == STATE.Options){
					SpriteSheet spriteSheet = new SpriteSheet("/sprite_sheet.png"); 
					Game.SPRITE_SHEET.image = spriteSheet.image;
					Game.SPRITE_SHEET.pixels = spriteSheet.pixels;
				}else if(Game.gameState == STATE.End){
					game.retry();
					game.fadeIn(25);
					game.setCursor(game.invisibleCursor);
					Game.gameState = STATE.Game;
				}
			}else if(mouseOver(((game.getWidth())>>1)-((game.getWidth())>>2), (game.getHeight())-((game.getHeight()+128)>>3)*1, ((game.getWidth())>>1), (game.getHeight())>>3)){
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
			g.drawString(Game.TITLE, (game.getWidth() - (int) r.getWidth()) >> 1, fm.getAscent());
		}else if(Game.gameState == STATE.Options){
			r = fm.getStringBounds("Options", g);
			g.drawString("Options", (game.getWidth() - (int) r.getWidth()) >> 1, fm.getAscent());			
		}else if(Game.gameState == STATE.End){
			r = fm.getStringBounds("Game Over!", g);
			g.drawString("Game Over!", (game.getWidth() - (int) r.getWidth()) >> 1, fm.getAscent());			
		}
		
		// body
		g.setFont(body);
		fm = g.getFontMetrics();
		if(Game.gameState == STATE.Menu){
			r = fm.getStringBounds("Start", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*3, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Start", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*3 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Options/Help", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*2, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Options/Help", (game.getWidth() - (int) r.getWidth()) >> 1,(game.getHeight())-((game.getHeight()+128)>>3)*2 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Exit", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*1, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Exit", (game.getWidth() - (int) r.getWidth()) >> 1,(game.getHeight())-((game.getHeight()+128)>>3)*1 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
		}else if(Game.gameState == STATE.Options){
			g.setFont(text);
			fm = g.getFontMetrics();
			r = fm.getStringBounds("Use arrow keys or WASD keys to move. Use your mouse", g);
			g.drawString("Use arrow keys or WASD keys to move. Use your mouse", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*4 - (int)r.getHeight()*4 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("to aim and left click to shoot, and right click to", g);
			g.drawString("to aim and left click to shoot, and right click to", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*4 - (int)r.getHeight()*3 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("attack. Find three         to unlock the boss level.", g);
			g.drawString("attack. Find three         to unlock the boss level.", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*4 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			Rectangle2D r2 = fm.getStringBounds("attack. Find three ", g);
			g.drawImage(Game.SPRITE_SHEET.image.getSubimage(5<<4, 3<<4, 16, 16), ((game.getWidth() - (int) r.getWidth()) >> 1)+(int)r2.getWidth(), (game.getHeight())-((game.getHeight()+128)>>3)*4 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent()-40,64,64, null);
			//g.drawRect(((game.getWidth() - (int) r.getWidth()) >> 1)+(int)r2.getWidth(), (game.getHeight())-((game.getHeight()+128)>>3)*4 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent()-40,64,64);
			
			r = fm.getStringBounds("Press button in the upper-right corner to select", g);
			g.drawString("Press button in the upper-right corner to select", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*3 - (int)r.getHeight()*2 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("a random spritesheet (runs asynchronously). Or press", g);
			g.drawString("a random spritesheet (runs asynchronously). Or press", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*3 - (int)r.getHeight() + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("the button below to select the default spritesheet.", g);
			g.drawString("the button below to select the default spritesheet.", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*3 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			g.setFont(body);
			fm = g.getFontMetrics();
			
			r = fm.getStringBounds("Default", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2),  (game.getHeight())-((game.getHeight()+128)>>3)*2, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Default", (game.getWidth() - (int) r.getWidth()) >> 1,(game.getHeight())-((game.getHeight()+128)>>3)*2 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Back", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2),  (game.getHeight())-((game.getHeight()+128)>>3)*1, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Back", (game.getWidth() - (int) r.getWidth()) >> 1,(game.getHeight())-((game.getHeight()+128)>>3)*1 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
		}else if(Game.gameState == STATE.End){
			r = fm.getStringBounds("You lost!", g);
			g.drawString("You lost!", (game.getWidth() - (int) r.getWidth()) >> 1, (game.getHeight())-((game.getHeight()+128)>>3)*3 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Retry", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2) , (game.getHeight())-((game.getHeight()+128)>>3)*2, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Retry", (game.getWidth() - (int) r.getWidth()) >> 1,(game.getHeight())-((game.getHeight()+128)>>3)*2 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
			
			r = fm.getStringBounds("Main Menu", g);
			g.drawRect(((game.getWidth())>>1)-((game.getWidth())>>2),  (game.getHeight())-((game.getHeight()+128)>>3)*1, ((game.getWidth())>>1), (game.getHeight())>>3);
			g.drawString("Main Menu", (game.getWidth() - (int) r.getWidth()) >> 1,(game.getHeight())-((game.getHeight()+128)>>3)*1 + (((game.getHeight())>>3)-(int)r.getHeight()) / 2 + fm.getAscent());
		}
	}

}
