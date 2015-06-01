package linkan.minild59.game.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

public class BasicSoundEffect {
	
	public static final int MAX_THREADS = 20;
	
	public static final int HIGH_PRIORITY 	= 2;
	public static final int NORMAL_PRIORITY = 1;
	public static final int LOW_PRIORITY  	= 0;
	
	public static AudioInputStream SFX_BOSS;
	public static AudioInputStream SFX_HIT;
	public static AudioInputStream SFX_HURT;
	public static AudioInputStream SFX_LOSE;
	public static AudioInputStream SFX_PICKUP;
	public static AudioInputStream SFX_SHOOT;
	public static AudioInputStream SFX_WIN;
	
	static{
		try {
			SFX_BOSS 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/boss.wav"));
			SFX_HIT 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/hit.wav"));
			SFX_HURT 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/hurt.wav"));
			SFX_LOSE 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/lose.wav"));
			SFX_PICKUP 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/pickup.wav"));
			SFX_SHOOT 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/shoot.wav"));
			SFX_WIN 	= createReusableAudioInputStream(BasicSoundEffect.class.getResource("/win.wav"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static AudioInputStream createReusableAudioInputStream(URL url) 
	        throws IOException, UnsupportedAudioFileException
	    {
	        AudioInputStream ais = null;
	        try
	        {
	            ais = AudioSystem.getAudioInputStream(url);
	            byte[] buffer = new byte[1024 * 32];
	            int read = 0;
	            ByteArrayOutputStream baos = 
	                new ByteArrayOutputStream(buffer.length);
	            while ((read = ais.read(buffer, 0, buffer.length)) != -1)
	            {
	                baos.write(buffer, 0, read);
	            }
	            AudioInputStream reusableAis = 
	                new AudioInputStream(
	                        new ByteArrayInputStream(baos.toByteArray()),
	                        ais.getFormat(),
	                        AudioSystem.NOT_SPECIFIED);
	            return reusableAis;
	        }
	        finally
	        {
	            if (ais != null)
	            {
	                ais.close();
	            }
	        }
	    }
	
	private static volatile int nThreads = 0;
	
	public static synchronized void playSound(final AudioInputStream inputStream, int priority) {
		switch(priority){
		default:
		case LOW_PRIORITY:
			if(nThreads>= MAX_THREADS>>1) return;
			break;
		case NORMAL_PRIORITY:
			if(nThreads>= MAX_THREADS) return;
			break;
		case HIGH_PRIORITY:
			break;
		}
		new Thread(() -> {
			nThreads++;
			try {
				Clip clip = AudioSystem.getClip();
				inputStream.reset();
				clip.open(inputStream);
				clip.start();
				while(clip.getMicrosecondPosition() < clip.getMicrosecondLength());
			} catch (Exception e) {
				e.printStackTrace();
			}
			nThreads--;
		}).start();
	}
}
