package charts;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import sonido.Sonido;
import utiles.Utiles;

public class ChartEnergy extends Thread{
	private AudioInputStream input;
	private XYSeries serieA;
	private Integer count;
	
	
	public ChartEnergy(Sonido s){
		this.input = s.getAudioStream();
		this.serieA = new XYSeries ("Energía");
		this.count = 0;
	}
	
	private void añadeMuestras() throws IOException{
		/*EXTRACCION DE LOS BYTES DE LAS MUESTRAS*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nBufferSize = 1024 * input.getFormat().getFrameSize();
		byte[]	abBuffer = new byte[nBufferSize];
		while (true){
//			System.out.println(("trying to read (bytes): " + abBuffer.length));
			int	nBytesRead = input.read(abBuffer);
//			System.out.println("read (bytes): " + nBytesRead);
			if (nBytesRead == -1){
				break;
			}
			baos.write(abBuffer, 0, nBytesRead);
		}
		byte[] abAudioData = baos.toByteArray();
		
		/*extracción de un canal*/
		List<List<Short>> muestras = Utiles.extraeMuestras(abAudioData);
		List<Short> a = muestras.get(0);//canal left
		List<Short> b = muestras.get(1);//canal right
		
		List<Double> energies = energies(a,b);
		Integer j = 0;
		Integer corteMuestras;
		if(a.size()<500000){			
			corteMuestras = 100;
		}else{
			corteMuestras = 1000;
		}
		Double aux = 0.;
		for(Double d:energies){
			if(j<corteMuestras){
				aux = aux + d;
				j++;
				count++;
			}else{
				aux = aux/corteMuestras;
				serieA.add(count, aux);
				j = 0;
			}
		}
	}
	
	private List<Double> energies(List<Short> a, List<Short> b){
		List<Double> res = new ArrayList<Double>();
		int n_samples = 1024;
		Integer j = 0;
		Double energy = 0.0;
		for (int i = 0; i< a.size(); i++){
			if(j < n_samples){
				energy = energy + a.get(i)*a.get(i) + b.get(i)*b.get(i);
				res.add(energy);
				j++;
			}else{
				energy = (double) (a.get(i)*a.get(i) + b.get(i)*b.get(i));
				res.add(energy);
				j = 0;
			}
		}
		return res;
	}
	
	public XYSeriesCollection getDataset(){		
		XYSeriesCollection datasetAB = new XYSeriesCollection();
		datasetAB.addSeries(serieA);
		return datasetAB;
	}
	
	public void muestraChart() throws IOException{
		añadeMuestras();
		JFreeChart chartAB = ChartFactory.createXYLineChart( 
				"Energía", // chart title 
				"Muestras", // x axis label 
				"Energía", // y axis label 

				getDataset(), // data 
				PlotOrientation.VERTICAL, 
				true, // include legend 
				true, // tooltips 
				false // urls 
				);
		ChartFrame frameAB = new ChartFrame("Energy", chartAB); 
		frameAB.pack(); 
		frameAB.setVisible(true);
	}
	
	public void run(){
		try {
			muestraChart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
