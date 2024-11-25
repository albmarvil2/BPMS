package sonido;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;

public interface Sonido {

	public abstract void abrir();

	public abstract String getDuracion();

	public abstract String getNombre();

	public abstract AudioInputStream getAudioStream();

	public abstract Clip getClip();

	public abstract String getAbsPath();
}