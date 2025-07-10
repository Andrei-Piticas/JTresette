package view.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;

public class AudioManager {
    private static AudioManager instance;
    private Clip musicClip; // Clip dedicato solo alla musica
    private boolean soundEffectsMuted = false; // Interruttore per gli effetti sonori

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    private AudioManager() {}

    // Metodo specifico per la MUSICA di sottofondo
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

    // Metodo specifico per gli EFFETTI SONORI
    public void playSoundEffect(String filename) {
        // Se i suoni sono disattivati, esce subito senza fare nulla
        if (soundEffectsMuted) {
            return;
        }

        try (InputStream in = new BufferedInputStream(new FileInputStream(filename))) {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            Clip soundClip = AudioSystem.getClip(); // Crea un clip temporaneo solo per questo suono
            soundClip.open(audioIn);
            soundClip.start(); // Suona l'effetto una sola volta
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Attiva/Disattiva la musica
    public void toggleMusic() {
        if (musicClip == null) return;
        if (musicClip.isRunning()) {
            musicClip.stop();
        } else {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Attiva/Disattiva gli effetti sonori
    public void toggleSoundEffects() {
        this.soundEffectsMuted = !this.soundEffectsMuted; // Inverte lo stato del muto
        System.out.println("Effetti sonori " + (soundEffectsMuted ? "disattivati" : "attivati"));
    }
}