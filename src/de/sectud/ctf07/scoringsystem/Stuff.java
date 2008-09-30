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

public class Stuff {

	public static final String TEXT_ATTRS_OFF = "\033[0m";

	public static final String TEXT_ATTRS_BOLD = "\033[1m";

	/**
	 * Returns an uppercase and with ':' separated hexadecimal String of the
	 * given byte array.
	 * 
	 * @param buf
	 *            The byte array that should be displayed as a hexadecimal
	 *            String.
	 * @return The corresponding hexadecimal String
	 * @author Tobias Kern
	 */
	public static String toHex(byte[] buf) {
		StringBuffer strBuf = new StringBuffer(buf.length * 2);
		for (int i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				strBuf.append("0");
			}
			strBuf.append(Long.toString((int) buf[i] & 0xff, 16));
			/*
			 * if (i != buf.length - 1) { strBuf.append(":"); }//
			 */
		}
		return strBuf.toString().toUpperCase();
	}

	public static byte[] noLocalEcho() {
		byte[] toc = new byte[3];
		toc[0] = (byte) 255;
		toc[1] = (byte) 251;
		toc[2] = (byte) 1;
		return toc;
	}

	public static byte[] localEcho() {
		byte[] toc = new byte[3];
		toc[0] = (byte) 255;
		toc[1] = (byte) 252;
		toc[2] = (byte) 1;
		return toc;
	}

	public static String beginPrompt() {
		return "\033[0;40;32m";
	}

	public static String endPrompt() {
		return "\033[0m";
	}

}
