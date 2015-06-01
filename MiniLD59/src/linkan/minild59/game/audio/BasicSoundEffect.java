package linkan.minild59.game.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class BasicSoundEffect {
	
	public static synchronized void playSound(final String url) {
		new Thread(() -> {	
			try {
				Clip clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(BasicSoundEffect.class.getResource(url));
				clip.open(inputStream);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
