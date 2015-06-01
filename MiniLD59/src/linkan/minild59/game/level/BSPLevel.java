package linkan.minild59.game.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import linkan.minild59.game.Game;
import linkan.minild59.game.InputHandler;
import linkan.minild59.game.entities.Entity;
import linkan.minild59.game.entities.mob.Player;
import linkan.minild59.game.entities.pickups.HealthPickup;
import linkan.minild59.game.entities.pickups.KeyPickup;
import linkan.minild59.game.level.tiles.Tile;
import linkan.minild59.game.util.Vector2i;

public class BSPLevel extends Level {
	
	private int maxLeafSize = 20;

	public BSPLevel(Game game, int width, int height, InputHandler input, int maxLeafSize) {
		super(game, width, height, input);
		this.maxLeafSize = maxLeafSize;
		new Thread(() -> {
			this.generateLevel(input);
			this.addEntity(player);
			this.loading = false;
		}).start();
		//this.addEntity(new Shooter(this, "Shooter" ,(int)player.getX(), (int)player.getY(), 1.0, 0.5, 16.0));
	}
	
	private boolean IsLegalMap(List<Leaf> _leafs, Vector2i start){
		for(Leaf other : _leafs){
			if(other.room == null) continue;
			List<Node> path = this.findPath(start, other.room.center);
			if(path == null){
				for(int y = 0; y < height; y++){
					for(int x = 0; x < width;x++){
						switch(tiles[x + y * width]){
						case 2 :{
							System.out.printf("#");
							break;
						}
						case 1 :
						{
							if(start.equals(new Vector2i(x, y)) || other.room.center.equals(new Vector2i(x, y))){
								System.out.printf("@");
							}else{									
								System.out.printf(" ");
							}
							break;
						}
						}
					}
					System.out.printf("\n");
				}
				return false;
			}
		}
		return true;
	}
	
	private Rectangle placePlayer(InputHandler input, List<Leaf> _leafs){
		Rectangle biggest = null;
		
		for(Leaf l : _leafs){
			if(l.room == null) continue;
			if(biggest != null){
				if(l.room.size() > biggest.size()) biggest = l.room;
			}else{
				biggest = l.room;
			}
		}
		if(biggest != null) {
			for (Iterator<Entity> iterator = this.entities.iterator(); iterator.hasNext();) {
			    Entity e = iterator.next();
			    if(biggest.intersects(new Rectangle((int)e.getX()>>4, (int)e.getY()>>4, 16, 16))){
			    	iterator.remove();
			    }
			}
			player = new Player(this, biggest.center.getX() << 4, biggest.center.getY() << 4, input);
		}
		return biggest;
	}
	
	private void placeKeys(List<Leaf> _leafs){
		Collections.shuffle(_leafs);
		int placedKeys = 0;
		for(Leaf l : _leafs){
			if(l.room == null || l.room.contains((int)player.getX()>>4, (int)player.getY()>>4)) continue;
			for (Iterator<Entity> iterator = this.entities.iterator(); iterator.hasNext();) {
			    Entity e = iterator.next();
			    if(e instanceof HealthPickup && l.room.intersects(new Rectangle((int)e.getX()>>4, (int)e.getY()>>4, 16, 16))){
			    	iterator.remove();
			    }
			}
			if(placedKeys++ < Math.max(3, Math.random()*_leafs.size()/2)){ this.addEntity(new KeyPickup(this, l.room.center.getX()<<4, l.room.center.getY()<<4));} else { return;};
		}
	}
	
	private void makeMap(int mapWidth, int mapHeight){
		for(int y = 0; y < mapHeight; y++){
			for(int x = 0; x < mapWidth;x++){
				tiles[x + y * mapWidth] = Tile.STONE.getId();
			}
		}
	}
	
	private void carveLevel(List<Leaf> _leafs){
		for(Leaf l : _leafs){
			if(l.halls != null){
				for(Rectangle r : l.halls){ // carve hallways
					for(int x = r.x1; x < r.x2; x++){
						for(int y = r.y1; y < r.y2; y++){
							tiles[x + y * width] = Tile.GRASS.getId();
						}
					}
				}
			}
			if(l.room != null) {
				for(int x = l.room.x1; x < l.room.x2; x++){ // carve rooms
					for(int y = l.room.y1; y < l.room.y2; y++){
						tiles[x + y * width] = Tile.GRASS.getId();
					}
				}
			}
		}
	}
	
	private boolean generateLevel(InputHandler input){
		this.makeMap(width, height);
		List<Leaf> _leafs = new ArrayList<Leaf>();
		 
		Leaf root = new Leaf(0, 0, width, height);
		_leafs.add(root);
		 
		boolean did_split = true;
		// we loop through every Leaf in our Vector over and over again, until no more Leafs can be split.
		while (did_split)
		{
		    did_split = false;
		    List<Leaf> leafs = new ArrayList<Leaf>(_leafs);
		    for(Leaf l : leafs)
		    {
		        if (l.leftChild == null && l.rightChild == null) // if this Leaf is not already split...
		        {
		            // if this Leaf is too big, or 75% chance...
		            if (l.width > maxLeafSize || l.height > maxLeafSize || Math.random() > 0.25)
		            {
		                if (l.split()) // split the Leaf!
		                {
		                    // if we did split, push the child leafs to the Vector so we can loop into them next
		                    _leafs.add(l.leftChild);
		                    _leafs.add(l.rightChild);
		                    did_split = true;
		                }
		            }
		        }
		    }
		}
		root.createRooms(this);
		this.carveLevel(_leafs);
		if(!this.IsLegalMap(_leafs, this.placePlayer(input, _leafs).center)){
			System.err.println("ILLEGAL MAP GENERATED!!!");
			this.entities = new ArrayList<Entity>();
			return this.generateLevel(input);
		}else{
			System.err.println("LEGAL MAP GENERATED.");
			this.placeKeys(_leafs); // might want to create a copy of _leafs using new ArrayList<>(_leafs);
			return true;
		}
	}
}
