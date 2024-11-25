package sonido;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

@SuppressWarnings("serial")
public class SonidoImpl extends Component implements Sonido {
	private String nombre;
	private AudioInputStream audioStream;
	private Clip clip;
	private String AbsPath;
	/**
	 * Crea un sonido a partir del fichero correspondiente. Se analiza el formato que tiene dicho fichero
	 * para su posterior tratamiento. En el caso de ser mp3 la codificación se hace con las librerías 
	 * jl1.0.1.jar, tritonus_share.jar y mp3spi1.9.5.jar.
	 * @author Alberto Martínez Villarán
	 * @param nombre
	 */
	public SonidoImpl(File fichero){
		this.AbsPath = fichero.getAbsolutePath();
		this.nombre = fichero.getName();
		String[] list = fichero.getName().split("[.]");
		String formato = list[list.length-1];
		if(formato.equals("wav")){
			try {
				clip = AudioSystem.getClip();
				audioStream = AudioSystem.getAudioInputStream(fichero);
			} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
				System.out.println("No se encuentra el archivo o no es soportado");
			}
		}else if(formato.equals("mp3")){
			try{
				AudioInputStream in = AudioSystem.getAudioInputStream(fichero);
				AudioInputStream din =  null;
				AudioFormat formatoBase = in.getFormat();
				AudioFormat formatoDecodificado = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, formatoBase.getSampleRate(),16,formatoBase.getChannels(),formatoBase.getChannels()*2,formatoBase.getSampleRate(),false);
				din = AudioSystem.getAudioInputStream(formatoDecodificado, in);
				//creamos un AudioInputStream con el formato nuevo(mp3) creado a partir del base.
				
				this.audioStream = din;
				this.clip = AudioSystem.getClip();
			}catch(Exception e){
				System.out.println("No se encuentra el archivo o no es soportado");
			}
		}else{
			System.out.println("No se encuentra el archivo o no es soportado");
		}
		
	}
	
	public void abrir(){
		if(!this.getClip().isOpen()){
			try {
				this.getClip().open(this.getAudioStream());// Abre el AudioInputStream que guarda el sonido del fichero
			} catch (LineUnavailableException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	

	

	public String getDuracion() {
		this.abrir();
		long microsegundos = clip.getMicrosecondLength();
		double segundos =  (microsegundos*Math.pow(10, -6));
		int min = (int)(segundos/60);
		int seg = (int) (segundos-min*60);
		String s = min+":";
		if(seg<10){
			s = s+"0"+seg;
		}else{
			s = s+seg;
		}
		return s;
		
	}
	


	public String getNombre() {
		return this.nombre;
	}
	
	public String toString(){
		String s = this.getNombre()+" - "+this.getDuracion();
		return s;
	}



	public AudioInputStream getAudioStream() {
		return audioStream;
	}




	public Clip getClip() {
		return clip;
	}

	public String getAbsPath() {
		return AbsPath;
	}

}
