/**
 * CCCamp07 ScoringSystem
 * A CTF scoring bot & flag+advisory reporting system
 *
 * (C) 2007, Hans-Christian Esperer
 * hc at hcespererorg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 * * Neither the name of the H. Ch. Esperer nor the names of his
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * POSSIBILITY OF SUCH DAMAGE
 **************************************************************************/

package de.sectud.ctf07.scoringsystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketTimeoutException;

/**
 * Read from an InputStreamReader, enforcing a line-length limit. Use this class
 * to read untrusted input from a stream of some kind, when expecting a newline
 * character to occur sometime.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class LimitedBufferedReader {
	private InputStreamReader reader;

	private static final int LINE_LENGTH = 256;

	private char[] chars = new char[LINE_LENGTH];

	private PrintStream logFile = null;

	public LimitedBufferedReader(InputStreamReader reader) {
		this(reader, null);
	}

	public static int getLineLength() {
		return LINE_LENGTH;
	}

	public LimitedBufferedReader(InputStreamReader reader, PrintStream logFile) {
		super();
		if (reader == null) {
			throw new NullPointerException();
		}
		this.reader = reader;
		this.logFile = logFile;
	}

	public String readLine() throws IOException {
		int j = 0;
		final int R_LINE_LENGTH = LINE_LENGTH - 1;
		try {
			while (true) {
				int i = reader.read();
				if (i == '\n') {
					break;
				}
				if (i == -1) {
					throw new IOException("EOF");
				}
				if (i != '\r') {
					chars[j] = (char) i;
					if (j >= R_LINE_LENGTH) {
						break;
					}
					j++;
				}
			}
			String s = new String(chars).substring(0, j);
			if (logFile != null) {
				logFile.printf("C%d ", s.length());
				logFile.println(s);
			}
			return s;
		} catch (SocketTimeoutException e) {
			return "";
		}
	}

	public void close() throws IOException {
		reader.close();
	}

	public void munch(int charsToMunch) throws IOException {
		for (int i = 0; i < charsToMunch; i++) {
			reader.read();
		}
	}

	public void setLogFile(PrintStream logFile) {
		this.logFile = logFile;
	}

	public PrintStream getLogFile() {
		return this.logFile;
	}
}
