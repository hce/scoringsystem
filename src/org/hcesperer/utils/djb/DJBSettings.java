package org.hcesperer.utils.djb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Real men do not use config files. Real men use a file per setting. Or at
 * least, DJB does. ;-)
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class DJBSettings {
	/**
	 * Real men do not use arbitrary limits, but in this case, java forces us
	 * to. Okay, we might add some code to work around it, but real men are
	 * lazy.
	 */
	private static final int MAX_CHARS_TO_READ = 65535;

	/**
	 * max chars to read when reading multi-line text
	 */
	private static final int MAX_CHARS_TO_READ_TEXT = 65535;

	/**
	 * Now this is not an arbitrary limit. Well, it is, defined by Integer, but
	 * it wasn't defined by us, but by them evil java creators
	 */
	private static final int MAX_INT_CHARS_TO_READ = String.valueOf(
			Integer.MAX_VALUE).length();

	public static byte[] readBytes(File f, int max_len) {
		try {
			int l; /* real men use one-letter variables */
			l = (int) f.length();
			if (l > max_len) {
				l = max_len;
			}
			byte[] b = new byte[l];
			InputStream s = new FileInputStream(f);
			s.read(b);
			s.close();
			return b;
		} catch (Throwable t) {
			return null;
		}
	}

	public static byte[] readBytes(URL f, int max_len) {
		try {
			URLConnection c = f.openConnection();
			int l; /* real men use one-letter variables */
			l = (int) c.getContentLength();
			if (l > max_len) {
				l = max_len;
			}
			byte[] b = new byte[l];
			InputStream s = c.getInputStream();
			s.read(b);
			s.close();
			return b;
		} catch (Throwable t) {
			return null;
		}
	}

	public static String readLine(String fn, int max_chars) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fn)));
			return br.readLine();
		} catch (Throwable t) {
			return null;
		}
	}

	public static String readText(String fn, int max_chars) {
		try {
			StringBuilder sb = new StringBuilder(MAX_CHARS_TO_READ_TEXT);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fn)));
			while (br.ready()) {
				sb.append(br.readLine());
				sb.append('\n');
			}
			return sb.toString();
		} catch (Throwable t) {
			return null;
		}
	}

	public static boolean writeLine(String fn, String line) {
		if (line.contains("\n")) {
			throw new IllegalArgumentException();
		}
		try {
			FileWriter w = new FileWriter(fn);
			w.write(line);
			w.write("\n");
			w.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static String loadString(String fn, String defaultSetting) {
		String s = readLine(fn, MAX_CHARS_TO_READ);
		return (s == null) ? defaultSetting : s;
	}

	public static String loadText(String fn, String defaultText) {
		String s = readText(fn, MAX_CHARS_TO_READ_TEXT);
		return (s == null) ? defaultText : s;
	}

	public static int loadInt(String fn, int defaultSetting) {
		String s = readLine(fn, MAX_INT_CHARS_TO_READ);
		if (s == null) {
			return defaultSetting;
		}
		try {
			return Integer.valueOf(s);
		} catch (Throwable t) {
			return defaultSetting;
		}
	}
}
