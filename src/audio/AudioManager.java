package audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {
    private static AudioManager instance;
    private Clip bgClip;

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }
    private AudioManager() { }
    public void play(String filename) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            bgClip = AudioSystem.getClip();
            bgClip.open(audioIn);
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void toggleMusic() {
        if (bgClip == null) return;
        if (bgClip.isRunning()) {
            bgClip.stop();
        } else {
            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
