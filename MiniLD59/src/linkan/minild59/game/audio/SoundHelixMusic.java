package linkan.minild59.game.audio;

import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

import com.soundhelix.component.player.Player;
import com.soundhelix.misc.SongContext;
import com.soundhelix.util.SongUtils;

public class SoundHelixMusic {
	
	private volatile Player player;
	
	public void stop(){
		player.abortPlay();
	}
	
	public void GenerateAndPlay(long randomSeed){
		if(player != null) player.abortPlay();
		new Thread(() -> {
			try {
				// configure log4j
				PropertyConfigurator.configureAndWatch("log4j.properties", 60 * 1000);
				
				SongContext songContext = SongUtils.generateSong(new URL("http://www.soundhelix.com/applet/examples/SoundHelix-Popcorn.xml"), randomSeed);
				
				player = songContext.getPlayer();
				
				player.open();
				player.play(songContext);
				player.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

}
