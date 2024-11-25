package utiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class Iterables2 {

	public static Iterable<String> from(String fileName) {
		return new InputStreamSeq(fileName);
	}

	public static <T> Iterable<T> from(T[] array) {
		return Arrays.asList(array);
	}

	private static class InputStreamSeq implements Iterable<String> {
		private String nf;

		public InputStreamSeq(String f) {
			nf = f;
		}

		public Iterator<String> iterator() {
			return new InputStreamIterator();
		}

		public String toString() {
			String result = "[";

			String item = "";
			for (Iterator<String> it = this.iterator(); it.hasNext();) {
				item = it.next();
				if (it.hasNext())
					result += item + ", ";
				else
					result += item + "";
			}

			result += "]";

			return result;

		}
		private class InputStreamIterator implements Iterator<String> {
			private BufferedReader bf;
			private String line;

			public InputStreamIterator() {
				try {
					bf = new BufferedReader(new FileReader(nf));
					line = bf.readLine();
				} catch (IOException e) {
					throw new IllegalArgumentException(
							"No se puede acceder al fichero de entrada");
				}
			}

			public boolean hasNext() {
				return line != null;
			}

			public String next() {
				if (!hasNext())
					throw new NoSuchElementException();
				String pal = line;
				try {
					line = bf.readLine();
				} catch (IOException e) {
					throw new IllegalArgumentException(
							"No se puede acceder al fichero de entrada");
				}
				return pal;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}
}
