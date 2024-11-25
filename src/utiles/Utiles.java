package utiles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Utiles {
	
	public static void imprimeFicheroMuestras(List<Short> l1 , List<Short> l2, String file) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(file);
		for(int i = 0; i<l1.size(); i++){
			out.println(l1.get(i).toString()+ " ; "+l2.get(i).toString());
//			out.println(l2.get(i).toString());
		}
		out.close();
	}
	public static void imprimeFicheroMuestras(List<Double> l1 , List<Double> l2, List<Double> l3, List<Double> l4, String file) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(file);
		for(int i = 0; i<l1.size(); i++){
			out.println(l1.get(i).toString()+ " ; "+l2.get(i).toString()+ " ; "+l3.get(i).toString()+ " ; "+l4.get(i).toString());
//			out.println(l2.get(i).toString());
		}
		out.close();
	}
	/*
	 * TODO extracción de muestras
	 * 
	 * MEJORAR ESTO A PIÑON!
	 * JAVADOC*/
	/**
	 * 
	 * @param abAudioData un byte[]
	 * @return "Lista de listas de Short"
	 */
	public static List<List<Short>> extraeMuestras(byte[] abAudioData){
		List<Short> a = new ArrayList<Short>();//canal left
		List<Short> b = new ArrayList<Short>();//canal right
		for(int offset = 0; offset<abAudioData.length; offset+=4){
//			System.out.println(offset+" of "+ (abAudioData.length-32));
			short sample = (short) (  (abAudioData[offset + 0] & 0xFF) | (abAudioData[offset + 1] << 8)  );
			a.add(sample);
	
			short sample2 = (short) (  (abAudioData[offset+2 + 0] & 0xFF) | (abAudioData[offset+2 + 1] << 8)  );
			b.add(sample2);
		}
		List<List<Short>> res = new ArrayList<List<Short>>();
		res.add(a);
		res.add(b);
		return res;
	}
}
