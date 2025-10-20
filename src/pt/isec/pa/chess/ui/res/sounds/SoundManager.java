package pt.isec.pa.chess.ui.res.sounds;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundManager {
    private SoundManager() { }

    private static MediaPlayer mp;

    public static boolean play(String filename) {
        try {
            var url = SoundManager.class.getResource("en/" + filename);
            if (url == null) return false;
            String path = url.toExternalForm();
            Media music = new Media(path);
            stop();
            mp = new MediaPlayer(music);
            mp.setStartTime(Duration.ZERO);
            mp.setStopTime(music.getDuration());
            mp.setAutoPlay(true);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPlaying() {
        return mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public static void stop() {
        if (mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING)
            mp.stop();
    }

    public static List<String> getSoundList() {
        File soundsDir = new File(SoundManager.class.getResource("en/").getFile());
        return Arrays.stream(soundsDir.listFiles()).map(x -> x.getName()).toList();
    }

    public static void playSequence(List<String> filenames) {
        if (filenames == null || filenames.isEmpty())
            return;
        playNextInSequence(new ArrayList<>(filenames), 0);
    }

    private static void playNextInSequence(List<String> files, int idx) {
        if (idx >= files.size())
            return;

        String filename = files.get(idx);
        boolean started = SoundManager.play(filename);

        if (!started) {
            // Se falhar, tenta prÃ³ximo imediatamente
            playNextInSequence(files, idx + 1);
            return;
        }

        if (SoundManager.mp != null) {
            SoundManager.mp.setOnEndOfMedia(() -> playNextInSequence(files, idx + 1));
            SoundManager.mp.setOnError(() -> playNextInSequence(files, idx + 1)); // Se der erro ao tocar, ignora
        }
    }
}