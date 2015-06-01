package linkan.minild59.game.graphics;

import java.util.List;

import linkan.minild59.game.util.Vector2i;

public class Screen {
	
	public static final byte BIT_MIRROR_X = 0x01;
	public static final byte BIT_MIRROR_Y = 0x02;
	
	public static final byte BIT_CUT_X = 0x01;
	public static final byte BIT_CUT_Y = 0x02;
	
	public int xOffset, yOffset, width, height;
	public int[] pixels;
	
	public SpriteSheet sheet;
	
	public Screen(int width, int height, int[] pixels, SpriteSheet sheet){
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		this.sheet = sheet;
	}
	
    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    
    public void renderBox(int xPos, int yPos, int width, int height, int colour, boolean fixed){
    	if(fixed){
    		xPos -= xOffset;
    		yPos -= yOffset;
    	}
		for(int y = 0; y < height; y++){
			int yPixel = y + yPos;
			for(int x = 0; x < width; x++){
				int xPixel = x + xPos;
				if(xPixel < 0 || xPixel >= this.width || yPixel < 0 || yPixel >= this.height) continue;
				pixels[xPixel + yPixel * this.width] = colour;
			}
		}
    }
	
	public void render(int xPos, int yPos, int tile, int mirrorDir, int cut, int scale, boolean fixed, boolean grayScale){
    	if(fixed){
    		xPos -= xOffset;
    		yPos -= yOffset;
    	}
		
		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;
		
		boolean cutX = (cut & BIT_CUT_X) > 0;
		boolean cutY = (cut & BIT_CUT_Y) > 0;
		
		int scaleMap = scale - 1;
		int xTile = tile % 8;
		int yTile = tile / 8;
		int tileOffset = (xTile << 4) + (yTile << 4) * sheet.width;
		
		for(int y = 0; y < (cutY ? 8 : 16); y++){
			int ySheet = y;
			if(mirrorY) ySheet = 15 - y;
			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << 4)/2);
			for(int x = 0; x < (cutX ? 8 : 16); x++){
				int xSheet = x;
				if(mirrorX) xSheet = 15 - x;
				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << 4)/2);
 				int col = sheet.pixels[xSheet + ySheet * sheet.width + tileOffset];
				if((col >> 24) != 0x00){
					for(int yScale = 0; yScale < scale; yScale++){						
						if(yPixel + yScale < 0 || yPixel + yScale >=  height) continue;
						for(int xScale = 0; xScale < scale; xScale++){
							if(xPixel + xScale < 0 || xPixel + xScale >= width) continue;							
							if(grayScale){
								int r = (col>>16) & 0xff;
								int g = (col>> 8) & 0xff;
								int b = (col>> 0) & 0xff;
								int lum = (int) Math.round(0.2126*r + 0.7152*g + 0.0722*b);
								col = 0xff000000 | ((lum << 16) | (lum << 8) | lum);
							}
							pixels[(xPixel + xScale) + (yPixel+yScale) * width] = col;
						}
					}
				}
			}
		}
	}
	
	public void render(int xPos, int yPos, int tile, int mirrorDir, int cut, int scale, SpriteSheet sheet){
		xPos -= xOffset;
		yPos -= yOffset;
		
		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;
		
		boolean cutX = (cut & BIT_CUT_X) > 0;
		boolean cutY = (cut & BIT_CUT_Y) > 0;
		
		int scaleMap = scale - 1;
		int xTile = tile % 8;
		int yTile = tile / 8;
		int tileOffset = (xTile << 4) + (yTile << 4) * sheet.width;
		
		for(int y = 0; y < (cutY ? 8 : 16); y++){
			int ySheet = y;
			if(mirrorY) ySheet = 15 - y;
			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << 4)/2);
			for(int x = 0; x < (cutX ? 8 : 16); x++){
				int xSheet = x;
				if(mirrorX) xSheet = 15 - x;
				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << 4)/2);
 				int col = sheet.pixels[xSheet + ySheet * sheet.width + tileOffset];
				if((col >> 24) != 0x00){
					for(int yScale = 0; yScale < scale; yScale++){						
						if(yPixel + yScale < 0 || yPixel + yScale >=  height) continue;
						for(int xScale = 0; xScale < scale; xScale++){
							if(xPixel + xScale < 0 || xPixel + xScale >= width) continue;							
							pixels[(xPixel + xScale) + (yPixel+yScale) * width] = col;
						}
					}
				}
			}
		}
	}
	
	public void render(int xPos, int yPos, int tile, int scale, int colour, boolean fixed, SpriteSheet sheet){
    	if(fixed){
    		xPos -= xOffset;
    		yPos -= yOffset;
    	}
		
		int scaleMap = scale - 1;
		int xTile = tile % (sheet.width >> 4);
		int yTile = tile / (sheet.width >> 4);
		int tileOffset = (xTile << 4) + (yTile << 4) * sheet.width;
		
		for(int y = 0; y < 16; y++){
			int ySheet = y;
			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << 4)/2);
			for(int x = 0; x < 16; x++){
				int xSheet = x;
				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << 4)/2);
 				int col = (sheet.pixels[xSheet + ySheet * sheet.width + tileOffset] >> 24) & colour;
				if((col >> 24) != 0x00){
					for(int yScale = 0; yScale < scale; yScale++){						
						if(yPixel + yScale < 0 || yPixel + yScale >= height) continue;
						for(int xScale = 0; xScale < scale; xScale++){
							if(xPixel + xScale < 0 || xPixel + xScale >= width) continue;							
							pixels[(xPixel + xScale) + (yPixel+yScale) * width] = col;
						}
					}
				}
			}
		}
	}

	/** debugging purposes */
	public void render(SpriteSheet sheet, int xPos, int yPos){
		for(int y = 0; y < sheet.height; y++){
			int yPixel = y + yPos;
			for(int x = 0; x < sheet.width; x++){
				int xPixel = x + xPos;
				if(xPixel < 0 || xPixel >= this.width || yPixel < 0 || yPixel >= this.height) continue;
				int col = sheet.pixels[xPixel + yPixel * sheet.width];
				if((col >> 24) != 0x00){
					pixels[xPixel + yPixel * this.width] = col;
				}
			}
		}
	}
	
	public void drawRect(int xPos, int yPos, int width, int height, int colour, boolean fixed) {
    	if(fixed){
    		xPos -= xOffset;
    		yPos -= yOffset;
    	}
		for(int x = xPos; x < xPos + width; x++){
			if(x < 0|| x >= this.width || yPos >= this.height) continue;
			if(yPos > 0) pixels[x + yPos * this.width] = colour;
			if(yPos + height >= this.height) continue;
			if(yPos + height> 0) pixels[x + (yPos + height) * this.width] = colour;
		}
		for(int y = yPos; y <= yPos + height; y++){
			if(xPos >= this.width || y < 0 || y >= this.height) continue;
			if(xPos > 0) pixels[xPos + y * this.width] = colour;
			if(xPos + width >= this.width) continue;
			if(xPos + width > 0) pixels[(xPos + width) + y * this.width] = colour;
		}
	}

	public void drawVectors(List<Vector2i> list, int colour, boolean fixed){
		for(Vector2i vec : list){
			int xPixel = vec.getX();
			int yPixel = vec.getY();
			if(fixed){
				xPixel -= xOffset;
				yPixel -= yOffset;
			}
			if(xPixel < 0 || xPixel >= this.width || yPixel < 0 || yPixel >= this.height) continue;
			pixels[xPixel + yPixel * this.width] = colour;
		}
	}
}
