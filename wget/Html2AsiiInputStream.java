/*
 * 
 *WGET
 *
 *@author Adonis Gonzalez Godoy
 *
 */

package wget;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/*
 * 
 * En esta clase quita tags html usando sobre escritura 
 *
 */

public class Html2AsiiInputStream extends FilterInputStream {
	
	InputStream in;
	Queue<Integer> returnQueue = new LinkedList<Integer>();
	
	public Html2AsiiInputStream(InputStream in) {
		super(in);
		this.in = in;
	}

	@Override
	public int read() throws IOException { 
		if (returnQueue.size() > 0) return returnQueue.remove();
		
		int c = this.in.read(); //Reads the next byte of data from this input stream
		
		if (c == -1) return -1;
		
		if (c == '<') {
			
			int nc;
			while ((nc = this.in.read()) != '>') {
				if (nc == -1) return -1; //cuando se llega al final
			}
			return this.read();
		}
		
		if (c == '\n') {
			returnQueue.add((int) '\\');
			returnQueue.add((int) 'n');
			returnQueue.add((int) '\n');
			return this.read();
		}

		
		return c;
	}
	

}