package linkan.minild59.game.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import linkan.minild59.game.Game;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.entities.mob.Player;
import linkan.minild59.game.level.tiles.Tile;
import linkan.minild59.game.util.Vector2i;

@Deprecated
public class RandomLevel extends Level {
	
	private List<Room> rooms;
	private int maxRooms, minRoomSize, maxRoomSize;
	
	private Random r;
	
	public RandomLevel(Game game, int mapWidth, int mapHeight, InputHandler input, int maxRooms, int minRoomSize, int maxRoomSize){
		super(game, mapWidth, mapHeight, input);
		this.maxRooms = maxRooms;
		this.minRoomSize = minRoomSize;
		this.maxRoomSize = maxRoomSize;
		this.r = new Random();
		this.makeMap(mapWidth, mapHeight);
		this.placePlayer(input);
		this.placeRooms();
	}
	
	private void makeMap(int mapWidth, int mapHeight){
		for(int y = 0; y < mapHeight; y++){
			for(int x = 0; x < mapWidth;x++){
				tiles[x + y * mapWidth] = Tile.STONE.getId();
			}
		}
	}
	
	private void placePlayer(InputHandler input){
			int w = maxRoomSize, h = maxRoomSize;
			int x = r.nextInt(width - w - 1) + 1;
			int y = r.nextInt(height - h - 1) + 1;
			
			Room newRoom = new Room(x, y, w, h);
			player = new Player(this, newRoom.center.getX() << 4, newRoom.center.getY() << 4, input);
			this.addEntity(player);
			this.createRoom(newRoom);
			rooms = new ArrayList<Room>();
			rooms.add(newRoom);
	}
	
	private void placeRooms(){
		//for(int i = 0; i < maxRooms; i++){
		while(rooms.size() < maxRooms){
			int w = minRoomSize + r.nextInt(maxRoomSize - minRoomSize + 1);
			int h = minRoomSize + r.nextInt(maxRoomSize - minRoomSize + 1);
			int x = r.nextInt(width - w - 1) + 1;
			int y = r.nextInt(height - h - 1) + 1;
			
			Room newRoom = new Room(x, y, w, h);
			
			boolean failed = false;
			for(Room otherRoom : rooms){
				if(newRoom.intersects(otherRoom)) {
					failed = true;
					break;
				}
			}
			if(!failed){
				this.createRoom(newRoom);
				Vector2i newCenter = newRoom.center;
				
				if(rooms.size() > 0){
					Vector2i prevCenter = rooms.get(rooms.size() - 1).center;
					if(r.nextInt() % 2 == 0){
						this.hCorridor(prevCenter.getX(), newCenter.getX(), prevCenter.getY());
						this.vCorridor(prevCenter.getY(), newCenter.getY(), newCenter.getX());
					}else{
						this.vCorridor(prevCenter.getY(), newCenter.getY(), prevCenter.getX());
						this.hCorridor(prevCenter.getX(), newCenter.getX(), newCenter.getY());
					}
				}
				rooms.add(newRoom);
			}
		}
	}
	
	private void createRoom(Room room){
		for(int x = room.x1; x < room.x2; x++){
			for(int y = room.y1; y < room.y2; y++){
				tiles[x + y * width] = Tile.GRASS.getId();
			}
		}
	}
	
	private void hCorridor(int x1, int x2, int y){
		for(int x = (int)Math.min(x1, x2);x < (int)Math.max(x1, x2) + 1; x++){
			tiles[x + y * width] = Tile.GRASS.getId();
		}
	}
	
	private void vCorridor(int y1, int y2, int x){
		for(int y = (int)Math.min(y1, y2);y < (int)Math.max(y1, y2) + 1; y++){
			tiles[x + y * width] = Tile.GRASS.getId();
		}
	}
}