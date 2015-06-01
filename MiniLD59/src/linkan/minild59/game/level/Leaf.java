package linkan.minild59.game.level;

import java.util.ArrayList;
import java.util.List;

import linkan.minild59.game.entities.mob.Enemy;
import linkan.minild59.game.entities.mob.Shooter;
import linkan.minild59.game.util.Utility;
import linkan.minild59.game.util.Vector2i;

public class Leaf {
 
    private static final int MIN_LEAF_SIZE = 6;
 
    public int y, x, width, height;
    public Leaf leftChild, rightChild;
    public Rectangle room;
    public List<Rectangle> halls;
 
    public Leaf(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Rectangle getRoom()
    {
        // iterate all the way through these leafs to find a room, if one exists.
        if (room != null)
            return room;
        else
        {
            Rectangle lRoom = null;
            Rectangle rRoom = null;
            if (leftChild != null)
            {
                lRoom = leftChild.getRoom();
            }
            if (rightChild != null)
            {
                rRoom = rightChild.getRoom();
            }
            if (lRoom == null && rRoom == null)
                return null;
            else if (rRoom == null)
                return lRoom;
            else if (lRoom == null)
                return rRoom;
            else if (Math.random() > .5)
                return lRoom;
            else
                return rRoom;
        }
    }
    
    public void createHall(Rectangle l, Rectangle r)
    {
        // now we connect these two rooms together with hallways.
        // this looks pretty complicated, but it's just trying to figure out which point is where and then either draw a straight line, or a pair of lines to make a right-angle to connect them.
        // you could do some extra logic to make your halls more bendy, or do some more advanced things if you wanted.
     
        halls = new ArrayList<Rectangle>();
     
        Vector2i point1 = new Vector2i(Utility.random(l.x1 + 1, l.x2 - 2), Utility.random(l.y1 + 1, l.y2 - 2));
        Vector2i point2 = new Vector2i(Utility.random(r.x1 + 1, r.x2 - 2), Utility.random(r.y1 + 1, r.y2 - 2));
     
        int w = point2.getX() - point1.getX();
        int h = point2.getY() - point1.getY();
     
        if (w < 0)
        {
            if (h < 0)
            {
                if (Math.random() < 0.5)
                {
                    halls.add(new Rectangle(point2.getX(), point1.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point2.getX(), point2.getY(), 1, Math.abs(h)));
                }
                else
                {
                    halls.add(new Rectangle(point2.getX(), point2.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point1.getX(), point2.getY(), 1, Math.abs(h)));
                }
            }
            else if (h > 0)
            {
                if (Math.random() < 0.5)
                {
                    halls.add(new Rectangle(point2.getX(), point1.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point2.getX(), point1.getY(), 1, Math.abs(h)));
                }
                else
                {
                    halls.add(new Rectangle(point2.getX(), point2.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point1.getX(), point1.getY(), 1, Math.abs(h)));
                }
            }
            else // if (h == 0)
            {
                halls.add(new Rectangle(point2.getX(), point2.getY(), Math.abs(w), 1));
            }
        }
        else if (w > 0)
        {
            if (h < 0)
            {
                if (Math.random() < 0.5)
                {
                    halls.add(new Rectangle(point1.getX(), point2.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point1.getX(), point2.getY(), 1, Math.abs(h)));
                }
                else
                {
                    halls.add(new Rectangle(point1.getX(), point1.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point2.getX(), point2.getY(), 1, Math.abs(h)));
                }
            }
            else if (h > 0)
            {
                if (Math.random() < 0.5)
                {
                    halls.add(new Rectangle(point1.getX(), point1.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point2.getX(), point1.getY(), 1, Math.abs(h)));
                }
                else
                {
                    halls.add(new Rectangle(point1.getX(), point2.getY(), Math.abs(w), 1));
                    halls.add(new Rectangle(point1.getX(), point1.getY(), 1, Math.abs(h)));
                }
            }
            else // if (h == 0)
            {
                halls.add(new Rectangle(point1.getX(), point1.getY(), Math.abs(w), 1));
            }
        }
        else // if (w == 0)
        {
            if (h < 0)
            {
                halls.add(new Rectangle(point2.getX(), point2.getY(), 1, Math.abs(h)));
            }
            else if (h > 0)
            {
                halls.add(new Rectangle(point1.getX(), point1.getY(), 1, Math.abs(h)));
            }
        }
    }
    
    public void createRooms(Level level) {
        // this function generates all the rooms and hallways for this Leaf and all of its children.
        if (leftChild != null || rightChild != null)
        {
            // this leaf has been split, so go into the children leafs
            if (leftChild != null)
            {
                leftChild.createRooms(level);
            }
            if (rightChild != null)
            {
                rightChild.createRooms(level);
            }
            // if there are both left and right children in this Leaf, create a hallway between them
            if (leftChild != null && rightChild != null)
            {
                createHall(leftChild.getRoom(), rightChild.getRoom());
            }
        }
        else
        {
            // this Leaf is the ready to make a room
            Vector2i roomSize;
            Vector2i roomPos;
            // the room can be between 3 x 3 tiles to the size of the leaf - 2.
            roomSize = new Vector2i(Utility.random(3, width - 2), Utility.random(3, height - 2));
            // place the room within the Leaf, but don't put it right 
            // against the side of the Leaf (that would merge rooms together)
            roomPos = new Vector2i(Utility.random(1, width - roomSize.getX() - 1), Utility.random(1, height - roomSize.getY() - 1));
            room = new Rectangle(x + roomPos.getX(), y + roomPos.getY(), roomSize.getX(), roomSize.getY());
                        
            // add enemies
            if(room.size() >= (width >> 1) || Math.random() > .25){
            	if(Math.random() > (1.0/room.size())*15.0){
            		level.addEntity(new Shooter(level, "Shooter", room.center.getX() << 4,room.center.getY() << 4, 1.0, 0.5, 16.0));
            		//level.addEntity(new HealthPickup(level, room.center.getX() << 4,room.center.getY() << 4));
            		return;
            	}
            	else {
            		level.addEntity(new Enemy(level, "Enemy", room.center.getX() << 4,room.center.getY() << 4, 1.0, 0.5, 8.0));
            		//level.addEntity(new HealthPickup(level, room.center.getX() << 4,room.center.getY() << 4));
            		return;
            	}
            }
            
            /**
            // add pickups
            if(Math.random() > (1.0/room.size())*15.0){
            	level.addEntity(new HealthPickup(level, room.center.getX() << 4,room.center.getY() << 4));
            	if(room.size() >= (width >> 1)){
            		level.addEntity(new HealthPickup(level, room.center.getX() << 4,room.center.getY() << 4));
            	}
            }
            */
        }
    }
 
    public boolean split() {
        // begin splitting the leaf into two children
        if (leftChild != null || rightChild != null)
            return false; // we're already split! Abort!
 
        // determine direction of split
        // if the width is >25% larger than height, we split vertically
        // if the height is >25% larger than the width, we split horizontally
        // otherwise we split randomly
        boolean splitH = Math.random() > 0.5;
        if (width > height && height / width >= 0.05)
            splitH = false;
        else if (height > width && width / height >= 0.05)
            splitH = true;
 
        int max = (splitH ? height : width) - MIN_LEAF_SIZE; // determine the maximum height or width
        if (max <= MIN_LEAF_SIZE)
            return false; // the area is too small to split any more...
 
        int split = Utility.random(MIN_LEAF_SIZE, max); // determine where we're going to split
        
 
        // create our left and right children based on the direction of the split
        if (splitH)
        {
            leftChild = new Leaf(x, y, width, split);
            rightChild = new Leaf(x, y + split, width, height - split);
        }
        else
        {
            leftChild = new Leaf(x, y, split, height);
            rightChild = new Leaf(x + split, y, width - split, height);
        }
        return true; // split successful!
    }
}
