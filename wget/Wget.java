/*
 * 
 *WGET
 *
 *@author Adonis Gonzalez Godoy
 *
 */

package wget;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/*
 * Clase principal
*/
public class Wget {
	
	public static void main(String[] args) {
		
		Hashtable<String, String> argsList = new Hashtable<String, String>();
		argsList.put("f", "");
		argsList.put("a", "");
		argsList.put("z", "");
		argsList.put("gz", "");
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				String argKey = args[i].substring(1, args[i].length());//verificar
				
				if (argsList.containsKey(argKey)) {
					switch (argKey) {
					case "f": { //tratamiento (f)					
						if (args.length <= i+1) {
							System.out.format("Error: Argument -f needs a filename\n");
							System.exit(1);
							}
						
						String fValue = args[i+1];
						i++;
						argsList.put("f", fValue);
						break;
						}
						default:
						argsList.put(argKey, "1");
					}
				}
				else {
						System.out.format("Error: Argumento desconocido'%s'\n", argKey);
						System.exit(1);
					 }
			}
			else {
				System.out.format("Error: Sintaxis de argumento '%s'\n", args[i]);
				System.exit(1);
				 }
		}
		//Lista donde se guardara las urls
		List<String> files = new ArrayList<String>();
		try {
			InputStream is = null;
			if (argsList.containsKey("f")) {
				//abrir el file parametro (-f) para leer  
				is = new FileInputStream(argsList.get("f")); 
			}
			else {
				System.out.format("Error: Debes definir el nombre de el fichero con el argumento -f.\n");
				System.exit(1);
			}
				//leer contenido del file y poner en la lista
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			try {
				line = reader.readLine();
				while (line != null) {
					files.add(line);
					line = reader.readLine();
					}
				} 
			catch (IOException e) {
				e.printStackTrace();
					}
			} 
		
		catch (FileNotFoundException e) {
			System.out.format("Error: se necesita 'urls.txt' para descargar..\n");
			System.exit(1);
			}
		//Lista para guardar urls descargas
		List<Descarga> descargas = new ArrayList<Descarga>();
		int index_count = 0;
		for (int i = 0; i < files.size(); i++) {
			// Pasamos el nombre del archivo y la tabla del constructor a la instancia de Descarga
			Descarga descarga = new Descarga(index_count, files.get(i), argsList); 
			descargas.add(descarga);
			descarga.start();
			if (descarga.withoutName == true) index_count++;
		}
		//Se verifica que cada hilo ha terminado
		for (int i = 0; i < descargas.size(); i++) {
			try {	
				descargas.get(i).join(); 
				}
			catch (InterruptedException e) {
				System.out.println("Se ha generado un error");  
				}
		}
		
		System.out.println("Todas las descargas han terminado");
	}

}
