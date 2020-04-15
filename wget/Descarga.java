/*
 * 
 *WGET
 *
 *@author Adonis Gonzalez Godoy
 *
 */

package wget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Descarga extends Thread {

	private String url;
	private Hashtable<String, String> argsList;
	public boolean withoutName = false;
	private String filename = "", fullname = "";
	
	public Descarga(int index_count, String url, Hashtable<String, String> argsList) {
		this.url = url;
		this.argsList = argsList;	// obtener la tabla de argumentos desde el constructor
		
		/*
		 * recorremos url para solo mostrar el nombre a partir de la '/'
		 * se recorre de final a inicio
		 * 
		 */
		for (int i = this.url.length()-1; i > 0; i--) {	
			// se comprueba extension la url
			if (this.url.charAt(i) == '/') {
				if (this.url.charAt(i-1) == '/') {
					this.withoutName = true;
					if (index_count == 0) this.filename = "index.html";
					else this.filename = "index (" + String.valueOf(index_count) + ").html";
				}
				break;
			}
			else this.filename = url.charAt(i) + this.filename;
		}
		
		this.fullname = this.filename;
	}
	
	
	public void run() {
		try {
			this.Descargar();
			} catch (IOException e) {
			System.out.format("ERROR: no se puede descargar %s:", this.url);
		}
	}
	
	
	public void Descargar() throws IOException {
		
		//Establecer conexion
		URL connection = new URL(url);
		
		HttpURLConnection conn = (HttpURLConnection) connection.openConnection();
		conn.setRequestMethod("HEAD");
		conn.connect();
		String contentType = conn.getContentType();
		
		FilterInputStream in = null;
		
		//argumento -a comprueba que sea de tipo 'txt/html' 
		if (contentType.contains("text/html") && argsList.get("a") == "1") {
			in = new Html2AsiiInputStream(connection.openStream()); // in control de flujo que se va filtrar
			this.fullname += ".asc";
		}
		//No filtrar
		else in = new BufferedInputStream(connection.openStream()); 
		
		String extensions = "";
		if (argsList.get("z") == "1") extensions += ".zip";
		if (argsList.get("gz") == "1") extensions += ".gz";
		
		//verificar si el fichero existe y enumerarlos
		File f = new File(this.fullname+extensions);
		if(f.exists()) { 
			int n = 1;
			while (true) {
				// Indice del punto de la extension en el nombre del archivo
				int ext_index = this.fullname.lastIndexOf('.'); 
				String new_filename = this.fullname.substring(0, ext_index) + 
										" (" + String.valueOf(n) + ")" + 
										this.fullname.substring(ext_index, this.fullname.length());
				
				File f2 = new File(new_filename);
				if(!f2.exists()) { 
					this.fullname = new_filename;
					break;
				}
				n++;
			}
		}
		
		this.fullname += extensions;
		
		OutputStream os = new FileOutputStream(this.fullname);
	
		if (argsList.get("gz") == "1") os = new GZIPOutputStream(os); //gzip
		if (argsList.get("z") == "1") {								  //zip			  
			os = new ZipOutputStream(os);
			((ZipOutputStream) os).putNextEntry(new ZipEntry(this.filename));
		}
		
		// Escribir cada caracter al fichero;
		int c;
		while ((c = in.read()) != -1) os.write(c); 

		System.out.format("%s --->downloaded.\n", this.fullname);
		
		in.close();
		os.close();
	}

}
