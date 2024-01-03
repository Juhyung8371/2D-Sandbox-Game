
package dev.game.sounds;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Sound.java - Sound in the game
 *
 * @author Juhyung Kim
 */
public enum Sound {

	PLAYER_ATTACK("entity/player_attack.wav"),
	ENTITY_PERSON_DIE("entity/entity_person_die.wav"),
	ENTITY_WOLF_DIE("entity/entity_wolf_die.wav"),
	ENTITY_GOLEM_DIE("entity/entity_golem_die.wav"),
	ENTITY_TREANT_DIE("entity/entity_treant_die.wav"),
	ENTITY_PERSON_TALK("entity/entity_person_talk.wav"),
	ENTITY_WOLF_ATTACK("entity/entity_wolf_attack.wav"),
	ENTITY_GOLEM_ATTACK("entity/entity_golem_attack.wav"),
	ENTITY_TREANT_ATTACK("entity/entity_treant_attack.wav"), DAY_BGM("bgm/day.wav"),
	NIGHT_BGM("bgm/night.wav"), CAVE_BGM("bgm/cave.wav"),
	GAME_OVER("bgm/gameover.wav");

	private static final String parent = "/res/sounds/";

	private Clip clip;

	/**
	 * Constructor
	 *
	 * @param path use .wave file
	 */
	Sound(String path) {

		AudioInputStream audioIn = null;

		try {

			// File file = new File(parent + path);
			InputStream stream = Sound.class.getResourceAsStream(parent + path);
			BufferedInputStream bis = new BufferedInputStream(stream);

			audioIn = AudioSystem.getAudioInputStream(bis);
			clip = AudioSystem.getClip();
			clip.open(audioIn);

		} catch (UnsupportedAudioFileException | IOException
				| LineUnavailableException ex) {
			Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				audioIn.close();
			} catch (IOException ex) {
				Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Play the sound once.
	 */
	public void play() {

		stop();
		clip.start();

	}

	/**
	 * Stop the sound.
	 */
	public void stop() {

		clip.stop();
		clip.setFramePosition(0);

	}

	/**
	 * Loop the sound.
	 */
	public void loop() {

		stop();
		clip.loop(Clip.LOOP_CONTINUOUSLY);

	}

	/**
	 * Stop all the sounds in the game.
	 */
	public static void stopAll() {
		PLAYER_ATTACK.stop();

		ENTITY_PERSON_DIE.stop();
		ENTITY_WOLF_DIE.stop();
		ENTITY_GOLEM_DIE.stop();
		ENTITY_TREANT_DIE.stop();

		ENTITY_PERSON_TALK.stop();
		ENTITY_WOLF_ATTACK.stop();
		ENTITY_GOLEM_ATTACK.stop();
		ENTITY_TREANT_ATTACK.stop();

		DAY_BGM.stop();
		NIGHT_BGM.stop();
		CAVE_BGM.stop();
		GAME_OVER.stop();

	}

}
