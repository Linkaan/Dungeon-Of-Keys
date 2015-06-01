package linkan.minild59.game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	public String path;
	public BufferedImage image;
	public int width, height;
	public int[] pixels;
	
	public SpriteSheet() {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new URL("http://swapshop.pixelsyntax.com/api/randomImage"));
		} catch (IOException e) {
			e.printStackTrace();

		}
		
		if(image == null) return;
		
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		this.image = image;
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
	}

	public SpriteSheet(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();

		}
		
		if(image == null) return;
		
		this.path = path;
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		this.image = image;
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
	}
	
	public SpriteSheet(int[] pixels, int width, int height){
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}
	
	public SpriteSheet rotate(int tile, double angle){
		int[] result = this.pixels.clone();
		int xTile = tile % 8;
		int yTile = tile / 8;
		//int tileOffset = (xTile << 4) + (yTile << 4) * this.width;
		int[] pixels = new int[16 * 16];
		for(int y = 0; y < this.height; y++){
			int yPixel = y - (yTile << 4);
			for(int x = 0; x < this.width; x++){
				int xPixel = x - (xTile << 4);
				if(x < (xTile << 4) || x >= ((xTile << 4) + 16) || y < (yTile << 4)  || y >= ((yTile << 4)  + 16)) continue;
				pixels[xPixel + yPixel * 16] = result[x + y * this.width];
			}
		}
		int[] rPixels = this.rotate(pixels, 16, 16, angle);
		for(int y = 0; y < this.height; y++){
			int yPixel = y - (yTile << 4);
			for(int x = 0; x < this.width; x++){
				int xPixel = x - (xTile << 4);
				if(x < (xTile << 4) || x >= ((xTile << 4) + 16) || y < (yTile << 4)  || y >= ((yTile << 4)  + 16)) continue;
				result[x + y * this.width] = rPixels[xPixel + yPixel * 16];
				//result[x + y * this.width] = 0xff00ff00;
			}
		}
		return new SpriteSheet(result, this.width, this.height);
	}
	
	private int[] rotate(int[] pixels, int width, int height, double angle){
		int[] result = new int[width * height];
		double nx_x = rot_x(-angle, 1.0, 0.0);
		double nx_y = rot_y(-angle, 1.0, 0.0);
		double ny_x = rot_x(-angle, 0.0, 1.0);
		double ny_y = rot_y(-angle, 0.0, 1.0);
		
		double x0 = rot_x(-angle, -width / 2.0, -height / 2.0) + width / 2.0;
		double y0 = rot_y(-angle, -width / 2.0, -height / 2.0) + height / 2.0;
		
		for(int y = 0; y < height; y++){
			double x1 = x0;
			double y1 = y0;
			for(int x = 0; x < width; x++){
				int xx = (int) (x1);
				int yy = (int) (y1);
				int col = 0x00;
				if(!(xx < 0 || xx >= width || yy < 0 || yy >= height)) col = pixels[xx + yy * width];
				result[x + y * width] = col;
				x1 += nx_x;
				y1 += nx_y;
			}
			x0 += ny_x;
			y0 += ny_y;
		}
		
		return result;
	}
	
	private double rot_x(double angle, double x, double y){
		double cos = Math.cos(angle - Math.PI / 2);
		double sin = Math.sin(angle - Math.PI / 2);
		return x*cos+y*-sin;
	}
	
	private double rot_y(double angle, double x, double y){
		double cos = Math.cos(angle - Math.PI / 2);
		double sin = Math.sin(angle - Math.PI / 2);
		return x*sin+y*cos;
	}
}
