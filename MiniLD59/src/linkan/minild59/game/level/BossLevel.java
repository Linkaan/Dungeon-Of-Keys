package linkan.minild59.game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import linkan.minild59.game.Game;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.entities.mob.Boss;
import linkan.minild59.game.entities.mob.Player;
import linkan.minild59.game.level.tiles.Tile;

public class BossLevel extends Level {
	
	private BufferedImage image;
	
	public BossLevel(Game game, String path, InputHandler input) {
		super(game, 0, 0, input);
		this.loadLevelFromFile(path, input);
		this.addEntity(player);
		this.loading = false;
	}

	private void loadLevelFromFile(String path, InputHandler input) {
		try{
			this.image = ImageIO.read(Level.class.getResourceAsStream(path));
			this.width = image.getWidth();
			this.height = image.getHeight();
			tiles = new byte[width * height];
			this.loadTiles(input);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadTiles(InputHandler input){
		int[] tileColours = this.image.getRGB(0, 0, width, height, null, 0, width);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				tileCheck: for(Tile t : Tile.tiles){
					if(t != null && t.getLevelColour() == tileColours[x + y * width]){
						this.tiles[x + y * width] = t.getId();
						break tileCheck;
					}
				}
				if(tileColours[x + y * width] == 0xffff00ff){
					this.tiles[x + y * width] = Tile.GRASS.getId();
					player = new Player(this, x<<4, y<<4, input);
				}else if(tileColours[x + y * width] == 0xffffffff){
					this.tiles[x + y * width] = Tile.GRASS.getId();
					this.addEntity(new Boss(this, x<<4, y<<4));
				}
			}			
		}
	}
}
