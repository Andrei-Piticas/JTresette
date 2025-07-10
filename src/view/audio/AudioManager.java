package view.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;


//*Gestisce tutta la riproduzione audio dell'applicazione.
// Questa classe è implementata seguendo il singleton,quindi ne puo esistere solamente una in tutto il progetto*//
public class AudioManager {
    private static AudioManager instance;
    private Clip musicClip;
    private boolean soundEffectsMuted = false;

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    private AudioManager() {}

    public void playMusic(String filename) {
        if (musicClip != null && musicClip.isRunning()) return;
        try (InputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void playSoundEffect(String filename) {

        if (soundEffectsMuted) {
            return;
        }

        try (InputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioIn);
            soundClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*Attiva o disattiva la musica*//
    public void toggleMusic() {
        if (musicClip == null) return;
        if (musicClip.isRunning()) {
            musicClip.stop();
        } else {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    //*Attiva o disattiva gli effetti sonori*//
    public void toggleSoundEffects() {
        this.soundEffectsMuted = !this.soundEffectsMuted; // Inverte lo stato del muto
        System.out.println("Effetti sonori " + (soundEffectsMuted ? "disattivati" : "attivati"));
    }
}