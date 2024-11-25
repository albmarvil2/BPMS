package statisticbpm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import sonido.FactoriaSonido;
import sonido.Sonido;
import utiles.Utiles;

public class BPMCalculatorImpl implements BPMCalculator {
	
	
	
	private float sampleRate;
	private int bitsPerSample;
	private int frameSize;
	private Sonido s;
	private FactoriaSonido f;
	private AudioInputStream input;
	private AudioFormat format;
	
	
	private int beats;
	private int beatsSeguidos;
	
	private List<Integer> listaBeatsSeguidos;
	private boolean beatAnterior;
	
	private List<Double> E;
	
	
	public BPMCalculatorImpl (Sonido s){
				
		this.s = s;
		
		this.f = new FactoriaSonido();
		
		this.input = this.s.getAudioStream();
		
		this.format = this.input.getFormat();
		
		this.bitsPerSample = this.format.getSampleSizeInBits();
		
		this.sampleRate = this.format.getSampleRate();
		
		this.frameSize = this.format.getFrameSize();
		
	}
public Double run(Double C) throws IOException{
		this.beats = 0;
		this.beatsSeguidos = 0;
		
		this.listaBeatsSeguidos = new ArrayList<Integer>();
		this.beatAnterior = false;
		
		this.E = new ArrayList<Double>();
		
		Integer numeroMuestras = 0;//contador de muestras
		/*EXTRACCION DE LOS BYTES DE LAS MUESTRAS*/
		int nBufferSize = 1024 * frameSize;
		byte[]	abBuffer = new byte[nBufferSize];
//		System.out.println("Abriendo el fichero");
		
		while (true){
//			System.out.println(("trying to read (bytes): " + abBuffer.length));
			int	nBytesRead = input.read(abBuffer);
//			System.out.println("read (bytes): " + nBytesRead);
			if (nBytesRead == -1){
				break;
			}
			
			numeroMuestras= numeroMuestras + 1024;//contamos las muestras para luego calcular la duración
			
			/*extracción de un canal*/
			List<List<Short>> muestras = Utiles.extraeMuestras(abBuffer);
			List<Short> a = muestras.get(0);//canal left
			List<Short> b = muestras.get(1);//canal right
						
			//llamada a beatDetector
			
			beatDetector(a,b,C);
			
			//reiniciamos baos
		}
		double duracion = numeroMuestras/this.sampleRate/60;
		
		
		double media = 0.0; //Media de Beats Seguidos Detectados
		for(Integer d : listaBeatsSeguidos){
			media = media + d;
		}
		media = media/listaBeatsSeguidos.size();
		double BPM = (beats/duracion)/media;
		
		resetSonido();
		return BPM;
		
	}
	
	public Double run() throws IOException{
		
		this.beats = 0;
		this.beatsSeguidos = 0;
		
		this.listaBeatsSeguidos = new ArrayList<Integer>();
		this.beatAnterior = false;
		
		this.E = new ArrayList<Double>();
		
		Integer numeroMuestras = 0;//contador de muestras
		/*EXTRACCION DE LOS BYTES DE LAS MUESTRAS*/
		int nBufferSize = 1024 * frameSize;
		byte[]	abBuffer = new byte[nBufferSize];
//		System.out.println("Abriendo el fichero");
		
		while (true){
//			System.out.println(("trying to read (bytes): " + abBuffer.length));
			int	nBytesRead = input.read(abBuffer);
//			System.out.println("read (bytes): " + nBytesRead);
			if (nBytesRead == -1){
				break;
			}
			
			numeroMuestras= numeroMuestras + 1024;//contamos las muestras para luego calcular la duración
			
			/*Extraccion de un canal*/
			List<List<Short>> muestras = Utiles.extraeMuestras(abBuffer);
			List<Short> a = muestras.get(0);//canal left
			List<Short> b = muestras.get(1);//canal right
			
			//llamada a beatDetector
			
			beatDetector(a,b);
			//reiniciamos baos
		}
		
		double duracion = numeroMuestras/this.sampleRate/60;
		
		
		double media = 0.0; //Media de Beats Seguidos Detectados
		for(Integer d : listaBeatsSeguidos){
			media = media + d;
		}
		media = media/listaBeatsSeguidos.size();
		double BPM = (beats/duracion)/media;
		resetSonido();
		return BPM;
		
	}
	/* (non-Javadoc)
	 * @see statisticbpm.BPMCalculator#Calculate()
	 */
	private void beatDetector(List<Short> a, List<Short> b){
		
		
		int n_samples = a.size();
		
		Double energy = 0.0;
		for (int i = 0; i< n_samples; i++){
			energy = energy + a.get(i)*a.get(i) + b.get(i)*b.get(i);
		}
		
		if(E.size()<43){//Por defecto esperamos a que se llene el buffer y luego operamos, podemos perder el primer beat.
			E.add(energy);
		}else{
			//Energía media de las 44100 muestras anteriores
			Double Em = 0.0;
				
			for (Double aux : E){
				Em = Em + aux;//SIN CUADRADO!
			}
			Em = Em/E.size();
				
				
				
			//Varianza	y C variable
			Double v  = 0.0;
				
			for (Double e : E){
				v = v + (e - Em);//SIN CUADRADO
			}
				
			v  = v/E.size();
				
			Double C =  (-0.0025714*v) + 1.5142857;
				
			//Actualización de E
			if(E.size() >= 43){
				E.remove(0);
			}
			E.add(energy);
			//Detección del beat!
			if(energy> C*Em){
				beats++;
				if(beatAnterior){
					beatAnterior = true;
					beatsSeguidos++;
				}else{
					beatAnterior = true;
					listaBeatsSeguidos.add(beatsSeguidos);
					beatsSeguidos = 1;
				}
				beatAnterior = true;
			}else{
				beatAnterior = false;
			}
		}
		
	}
	/* (non-Javadoc)
	 * @see statisticbpm.BPMCalculator#Calculate(java.lang.Double)
	 */
	private void beatDetector(List<Short> a, List<Short> b, Double C){
		/**
		 * Hacemos uso de una media, debido al problema de que el algoritmo detecta mas
		 * de un beat seguido cuando en realidad sólo se debería detectar uno.
		 */
		
		int n_samples = a.size();
		
		Double energy = 0.0;
		for (int i = 0; i< n_samples; i++){
			energy = energy + a.get(i)*a.get(i) + b.get(i)*b.get(i);
		}
		
		if(E.size()<43){//Por defecto esperamos a que se llene el buffer y luego operamos, podemos perder el primer beat.
			E.add(energy);
		}else{
			//Energía media de las 44100 muestras anteriores
			Double Em = 0.0;
				
			for (Double aux : E){
				Em = Em + aux;//SIN CUADRADO!
			}
			Em = Em/E.size();
				
			//Actualización de E
			if(E.size() >= 43){
				E.remove(0);
			}
			E.add(energy);
			//Detección del beat!
			if(energy> C*Em){
				beats++;
				if(beatAnterior){
					beatAnterior = true;
					beatsSeguidos++;
				}else{
					beatAnterior = true;
					listaBeatsSeguidos.add(beatsSeguidos);
					beatsSeguidos = 1;
				}
				beatAnterior = true;
			}else{
				beatAnterior = false;
			}
		}
	}
	
	
	private void resetSonido(){
		this.s = this.f.createSonido(this.s);
		
		this.input = this.s.getAudioStream();
		
		this.format = this.input.getFormat();
		
		this.bitsPerSample = this.format.getSampleSizeInBits();
		
		this.sampleRate = this.format.getSampleRate();
		
		this.frameSize = this.format.getFrameSize();
	}
	public String toString(){
		return "[Bits por muestra: "+this.bitsPerSample+", Sample Rate: "+this.sampleRate+", FrameSize: "+this.frameSize+", Nº Muestras: "+this.input.getFrameLength()+"]";
		
	}
}
