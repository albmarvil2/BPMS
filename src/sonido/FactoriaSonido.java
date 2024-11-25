package sonido;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

@SuppressWarnings("serial")
/**
 * Factoría para la creación de objetos Sonido con la ayuda de JFileChooser
 * @author Alberto
 *
 */
public class FactoriaSonido extends Component {
	
	/**
	 * Crea un objeto de tipo sonido haciendo uso de JFileChooser
	 * @return
	 */
	public Sonido createSonido(){
		Sonido res = null;
		JFileChooser fc = new JFileChooser();
		int respuesta = fc.showOpenDialog(this);
		if(respuesta == JFileChooser.APPROVE_OPTION){
			res = new SonidoImpl(fc.getSelectedFile());
		}
		
		return res;
	}
	/**
	 * Crea un List<Sonido> a partir de JFileChooser
	 * @return
	 */
	public List<Sonido> createSonidos(){
		
		List<Sonido> list = new ArrayList<Sonido>();
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		
		int respuesta = fc.showOpenDialog(this);
		if(respuesta == JFileChooser.APPROVE_OPTION){
			File[] files = fc.getSelectedFiles();
			for(int i=0;i<files.length;i++){
				list.add(new SonidoImpl(files[i]));
			}
		}
		return list;
	}
	
	public Sonido createSonido(Sonido s){
		return new SonidoImpl(new File(s.getAbsPath()));
	}

}
