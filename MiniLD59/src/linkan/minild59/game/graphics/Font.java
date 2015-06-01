package linkan.minild59.game.graphics;

public class Font {
	
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-&.?!           ";
	private static final SpriteSheet FONT_SHEET = new SpriteSheet("/font_sheet.png");
	
	public static void render(String msg, Screen screen, int x , int y, int scale, int colour, boolean fixed){
		msg = msg.toUpperCase();
		
		for(int i = 0; i < msg.length();i++){
			int charIndex = chars.indexOf(msg.charAt(i));
			if(charIndex >= 0) screen.render(x + (i*16), y, charIndex, scale, colour,fixed ,FONT_SHEET);
		}
	}
}
