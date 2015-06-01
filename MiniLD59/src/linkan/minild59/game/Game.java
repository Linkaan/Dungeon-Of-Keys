package linkan.minild59.game;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;

import javax.swing.JFrame;

import linkan.minild59.game.audio.BasicSoundEffect;
import linkan.minild59.game.audio.SoundHelixMusic;
import linkan.minild59.game.graphics.Screen;
import linkan.minild59.game.graphics.SpriteSheet;
import linkan.minild59.game.graphics.image.BoxBlurFilter;
import linkan.minild59.game.level.BSPLevel;
import linkan.minild59.game.level.BossLevel;
import linkan.minild59.game.level.Level;
import linkan.minild59.game.menu.Menu;
import linkan.minild59.game.util.Utility;

public class Game extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH  = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE  = 3;
	public static final String TITLE="Dungeon Of Keys";
	
	public static final int LEVEL_WIDTH  = 64;
	public static final int LEVEL_HEIGHT = 64;
	
	public static volatile SpriteSheet SPRITE_SHEET = new SpriteSheet("/sprite_sheet.png");
	
	public enum STATE {
		Menu,
		Options,
		Game,
		Boss,
		End,
	};
	public static STATE gameState = STATE.Menu;
	
	public Level level;
	public boolean running = false;
	public int tickCount = 0;
	
	private volatile boolean fading = false;
	private volatile float alpha = 0.0f;
	private double xOffset, yOffset, xVel, yVel;
	private int lastButton = -1;
	private String dots = ".";
	
	private JFrame frame;
	private Font header, font;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	private BoxBlurFilter bbfilter;
	private SoundHelixMusic music;
	private Screen screen;
	private InputHandler input;
	private Menu menu;
	
	public Game(){		
		this.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		this.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		this.frame = new JFrame(TITLE);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLayout(new BorderLayout());
		this.frame.add(this, BorderLayout.CENTER);
		this.frame.pack();
		this.frame.setResizable(false);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
		
		start();
	}
	
	public void init(){
		InputStream is = Game.class.getResourceAsStream("/VCR_OSD_MONO_1.001.ttf");
		Font baseFont = null;
		try {
			baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
			header = baseFont.deriveFont(42f);
			font = baseFont.deriveFont(16f);
		} catch (Exception e){
			header = new Font("Verdana", Font.PLAIN, 48);
			font = new Font("Verdana", Font.PLAIN, 16);
			e.printStackTrace();
		}
		
		bbfilter = new BoxBlurFilter();
		music = new SoundHelixMusic();
		music.GenerateAndPlay(System.nanoTime());
		
		screen = new Screen(WIDTH, HEIGHT, this.pixels, SPRITE_SHEET);
		input = new InputHandler(this);
		//level = new RandomLevel(128,128, input, 24, 5, 10);
		level = new BSPLevel(this, LEVEL_WIDTH, LEVEL_HEIGHT, input, 20);
		menu = new Menu(this,input, baseFont);
		
		bbfilter.setRadius(1);
		bbfilter.setIterations(3);
		
		xOffset = Utility.random(0, level.width<<4);
		yOffset = Utility.random(0, level.height<<4);
		xVel = 0.5;
		yVel = 0.5;
		
		//level = new BossLevel("/boss_level.png", input);
		//level = new LoadLevel(64, 64, input, new File("C:/Users/p99linst/Dropbox/map.txt"));
	}
	
	public void retry(){
		alpha = 0.0f;
		music.GenerateAndPlay(System.nanoTime());
		if(level.getPlayer().boss){
			gameState = STATE.Boss;
			level = new BossLevel(this, "/boss_level.png", input);
			fadeIn(50);
		}else{			
			level = new BSPLevel(this, LEVEL_WIDTH, LEVEL_HEIGHT, input, 20);
			fadeIn(25);
		}
	}
	
	public synchronized void start(){
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop(){
		running = false;
	}

	@Override
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		
		//int frames = 0, ticks = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = false;
			while(delta >= 1){
				//ticks++;
				update();
				delta -= 1;
				shouldRender = true;
			}
			if(shouldRender){
				//frames++;
				render();				
			}
			
			if(System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer = System.currentTimeMillis();
				//System.out.printf("Updates: %d , FPS: %d\n", ticks, frames);
				//this.frame.setTitle(TITLE + String.format(" | %d ups, %d fps", ticks, frames));
				//ticks = 0;
				//frames = 0;
			}
		}
	}
	
	public void won() {
		BasicSoundEffect.playSound("/win.wav");
		level.getPlayer().boss = false;
		fading = true;
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			while(alpha > 0.0f){ // fade out
				alpha -= 0.05f;
				if(alpha <= 0.0f){
					alpha = 0.0f;
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fading = false;
			retry();
			gameState = STATE.Menu;
			while(alpha < 1.0f){ // fade in
				alpha += 0.05f;
				if(alpha >= 1.0f){
					alpha = 1.0f;
				}
				try {
					Thread.sleep(25);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void fadeOut(int ms){
		fading = true;
		alpha = 1.0f;
		new Thread(() -> {			
			while(alpha > 0.0f){ // fade out
				alpha -= 0.05f;
				if(alpha <= 0.0f){
					alpha = 0.0f;
				}
				try {
					Thread.sleep(ms);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fading = false;
		}).start();
	}
	
	public void fadeIn(int ms){
		fading = true;
		alpha = 0.0f;
		new Thread(() -> {
			while(alpha < 1.0f){ // fade in
				alpha += 0.05f;
				if(alpha >= 1.0f){
					alpha = 1.0f;
				}
				try {
					Thread.sleep(ms);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fading = false;
		}).start();
	}

	private void update(){
		tickCount++;
		if(!level.loading){				
			if(gameState == STATE.Game && !fading){
					level.update();
					if(level.getPlayer().keysPicked >= 3){
						BasicSoundEffect.playSound("/boss.wav");
						fading = true;
						new Thread(() -> {
							while(alpha > 0.0f){ // fade out
								alpha -= 0.05f;
								if(alpha <= 0.0f){
									alpha = 0.0f;
								}
								try {
									Thread.sleep(50);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							gameState = STATE.Boss;
							level = new BossLevel(this, "/boss_level.png", input);
							level.getPlayer().boss = true;
							while(alpha < 1.0f){ // fade in
								alpha += 0.05f;
								if(alpha >= 1.0f){
									alpha = 1.0f;
								}
								try {
									Thread.sleep(50);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							fading = false;
						}).start();
					}
			}else if(gameState == STATE.Boss && !fading){
				level.update();
			}else if(gameState == STATE.Menu || gameState == STATE.Options || gameState == STATE.End){
				if(tickCount % 120 == 0){
					xOffset = Utility.random(0, level.width<<4);
					yOffset = Utility.random(0, level.height<<4);
					xVel = Math.random() > .5 ? -0.5 : 0.5;
					yVel = Math.random() > .5 ? -0.5 : 0.5;
				}
				xOffset += xVel;
				yOffset += yVel;
				if(xOffset < 0 || xOffset > ((level.width << 4)- screen.width)) xVel *= -1;
				if(yOffset < 0 || yOffset > ((level.height << 4)- screen.height)) yVel *= -1;
				level.updateTiles();
				menu.update();
			}else if(fading){
				level.updateWhileFading();
			}
		}else{
			if(tickCount % 15 == 0){
				if(dots.length() >= 3){
					dots = ".";
				}else{						
					dots += ".";
				}
			}
		}
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(!level.loading){
			//set the opacity
			Graphics2D g2d = (Graphics2D) g;
			if(fading && !(alpha < 0.0) && !(alpha > 1.0)){
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			}
			if(gameState == STATE.Game || gameState == STATE.Boss){
				
				double xOffset = level.getPlayer().getX() - (WIDTH/2);
				double yOffset = level.getPlayer().getY() - (HEIGHT/2);
				level.renderTiles(screen, (int)xOffset, (int)yOffset);		
				level.renderEntities(screen);
				g.drawImage(image, 0, 0, this.getWidth(),this.getHeight(),null);
			}else if(gameState == STATE.Menu || gameState == STATE.Options || gameState == STATE.End){
				level.renderTiles(screen, (int)xOffset, (int)yOffset);
				g.drawImage(bbfilter.filter(image, null), 0, 0, this.getWidth(),this.getHeight(),null);
				menu.render(g);
			}
			/**
			Graphics2D g2 = (Graphics2D) g;   
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.toRadians(45), 0, 0);
			Font rotatedFont = font.deriveFont(affineTransform);
			g2.setFont(rotatedFont);
			FontMetrics fm = g2.getFontMetrics();
			Rectangle2D r = fm.getStringBounds("Random", g2);
			g2.fillRect(Game.WIDTH*Game.SCALE-100, 0, 100, 100);
			g2.setColor(Color.BLACK);
			g2.drawString("Random",Game.WIDTH*Game.SCALE-(int)r.getWidth(), (int)r.getHeight());
			*/
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D r = fm.getStringBounds("Random", g);
			g.setColor(Color.WHITE);
			g.fill3DRect((Game.WIDTH*Game.SCALE - (int) r.getWidth()-10), 0, (int) r.getWidth()+20, (int)(r.getHeight()+2.5), true);
			if(lastButton != input.getButton() && (lastButton = input.getButton()) == 1 && menu.mouseOver((Game.WIDTH*Game.SCALE - (int) r.getWidth()-10), 0, (int) r.getWidth()+20, (int)(r.getHeight()+2.5))){
				new Thread(() -> {					
					SpriteSheet spriteSheet = new SpriteSheet(); 
					SPRITE_SHEET.image = spriteSheet.image;
					SPRITE_SHEET.pixels = spriteSheet.pixels;
				}).start();
			}
			g.setColor(Color.BLACK);
			g.drawString("Random", (Game.WIDTH*Game.SCALE - (int) r.getWidth()), fm.getAscent());
		}else{
			g.setColor(Color.WHITE);
			g.setFont(header);
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D r = fm.getStringBounds("Generating Level"+dots, g);
			g.drawString("Generating Level"+dots, (Game.WIDTH*Game.SCALE - (int) r.getWidth()) >> 1, ((Game.HEIGHT*Game.SCALE - (int) r.getHeight()) >> 1) + fm.getAscent());		
		}
		
		//Debug.drawLine(screen, 0, 0, WIDTH, HEIGHT, 0xff00ff00, false);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game();
	}
}
