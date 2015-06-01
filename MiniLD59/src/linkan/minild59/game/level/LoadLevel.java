package linkan.minild59.game.level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import linkan.minild59.game.Game;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.entities.mob.Player;
import linkan.minild59.game.level.tiles.Tile;
import linkan.minild59.game.util.Vector2i;

@Deprecated
public class LoadLevel extends Level {

	public LoadLevel(Game game, int width, int height, InputHandler input, File file) {
		super(game, width, height, input);
		String line;
		int y = 0;
		try (
		    InputStream fis = new FileInputStream(file);
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		) {
		    while ((line = br.readLine()) != null) {
		    	for (int x = 0; x < width; x++){
		    	    char c = line.charAt(x);        
					switch (c) {
					case '#':
						tiles[x + y * width] = Tile.STONE.getId();
						break;
					case ' ':
						if(player == null) player = new Player(this,x<<4,y<<4,input);
						tiles[x + y * width] = Tile.GRASS.getId();
						break;
					}
		    	}
		        y++;
		    }
		    this.addEntity(player);
			if(!this.IsLegalMap()){
				System.err.println("ILLEGAL MAP GENERATED!!!");
				System.err.flush();
				Thread.sleep(100);
			}else{
				System.err.println("LEGAL MAP GENERATED.");
				System.err.flush();
				Thread.sleep(100);
				//Vector2i start = new Vector2i((int)player.getX(), (int)player.getY());
				//Vector2i goal = new Vector2i(7, 40);
				//List<Node> path = this.findPath(start, goal);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private boolean IsLegalMap() throws InterruptedException{
		Vector2i start = new Vector2i((int)player.getX()>>4, (int)player.getY()>>4);
		for (int y2 = 0; y2 < height; y2++) {
			for (int x2 = 0; x2 < width; x2++) {
				if(getTile(x2, y2).isSolid()) continue;
				Vector2i goal = new Vector2i(x2, y2);
				if(start.equals(goal)) continue;
				List<Node> path = this.findPath(start, goal);
				if(path == null){
					tiles[start.getX() + start.getY() * width] = Tile.WATER.getId();
					tiles[goal.getX() + goal.getY() * width] = Tile.WATER.getId();
					System.err.printf("Goal X (%d), Goal Y (%d)\n", goal.getX(), goal.getY());
					System.err.flush();
					Thread.sleep(100);
					for(int y = 0; y < height; y++){
						for(int x = 0; x < width;x++){
							switch(tiles[x + y * width]){
							case 2 :{
								System.out.printf("#");
								System.out.flush();
								Thread.sleep(100);
								break;
							}
							case 1 :
							{					
								System.out.printf(" ");
								System.out.flush();
								Thread.sleep(100);
								break;
							}
							case 3 :
							{
								System.err.printf("#");
								System.err.flush();
								Thread.sleep(100);
								break;
							}
							}
						}
						System.out.printf("\n");
						System.out.flush();
						Thread.sleep(100);
					}
					return false;
				}
			}
		}
		return true;
	}

}
