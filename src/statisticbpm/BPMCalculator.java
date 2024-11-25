package statisticbpm;

import java.io.IOException;

public interface BPMCalculator {

	/**
	 * Calcula el BPM haciendo uso de un parámetro C variable
	 * @return BPM
	 * @throws IOException 
	 */
	public Double run() throws IOException;

	/**
	 * Calcula el BPM teniendo en cuenta un C fijo, es recomendable el uso de un C
	 * entre 1.1 y 1.5, dependiendo del estilo de música, siendo más bajo cuanto más ruido
	 * tenga el audio
	 * @param C
	 * @return BPM
	 * @throws IOException 
	 */
	public Double run(Double C) throws IOException;
	

}