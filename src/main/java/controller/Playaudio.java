package controller;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Playaudio implements Runnable {
	Clip clip;
	
	public void run() {
		File f = new File("output.wav");
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(f); 
			clip = AudioSystem.getClip();
	    
	        clip.open(stream);
	        clip.start();
	        
		}
        catch(Exception e) {        	
        	System.out.println("Exception["+ e.toString());
        }
	}
	public boolean isrunning() {
		return clip.isRunning();
	}
}
