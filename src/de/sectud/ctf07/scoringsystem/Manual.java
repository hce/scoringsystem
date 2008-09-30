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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.adela.exceptions.FunctionNotFoundException;
import net.sourceforge.adela.exceptions.WrongParameterCountException;
import net.sourceforge.adela.interfaces.IFunctionCallback;

/**
 * Online documentation system. This class has facilities to make it easily
 * accessibly from an interactive ADeLa prompt to read documentation. ATM, this
 * class contains a hardcoded list of filenames to read for documentation
 * purposes. For each read file, the class creates a virtual get$FILE() function
 * that can be used to get the file contents returned as a string.
 * 
 * If instanciated as variable 'man' in ADeLa, and fed with the files 'intro'
 * and 'fubar', one can write at the interactive ADeLa prompt:
 * 
 * <pre>
 * man       to get the list 'intro, fubar'
 * man.intro to get the contents of the file intro
 * man.fubar to get the contents of the file fubar
 * </pre>
 * 
 * Occurrences of <b> and </b> are translated to ANSI control characters $ESC[1m
 * (highlight) and $ESC[0m (reset all formatting)
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class Manual implements IFunctionCallback {
	private Map<String, String> topics = new HashMap<String, String>();

	private static Manual instance = new Manual();

	private void addTopic(String topic) {
		URL url = this.getClass().getResource("man/" + topic + ".txt");
		URLConnection conn;
		try {
			conn = url.openConnection();
			InputStream reader = conn.getInputStream();
			byte[] b = new byte[(int) conn.getContentLength()];
			reader.read(b);
			String text = new String(b);
			text = text.replaceAll("<b>", Stuff.TEXT_ATTRS_BOLD).replaceAll(
					"<\\/b>", Stuff.TEXT_ATTRS_OFF);
			topics.put(topic, text);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getPage(String topic) throws IOException {
		URL url = this.getClass().getResource("man/" + topic + ".txt");
		URLConnection conn;
		conn = url.openConnection();
		InputStream reader = conn.getInputStream();
		byte[] b = new byte[(int) conn.getContentLength()];
		reader.read(b);
		return new String(b);
	}

	private Manual() {
		try {
			addTopic("reportflag");
			addTopic("reportadvisory");
			addTopic("functions");
			addTopic("team");
			addTopic("service");
			addTopic("admin");
		} catch (Throwable t) {
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> itr = topics.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = topics.get(key).split("\n")[0];
			value = value.length() < 35 ? value : value.substring(0, 35)
					+ "...";
			key = "man." + key;
			sb.append(key);
			for (int i = key.length(); i < 25; i++) {
				sb.append(' ');
			}
			sb.append(' ');
			sb.append(value + "\n");
		}
		return "Which manual page do you want?\n" + sb;
	}

	public static Manual getInstance() {
		return instance;
	}

	public Object FunctionCallback(String funcName, Object[] vParams)
			throws WrongParameterCountException, FunctionNotFoundException {
		if (funcName.startsWith("get")) {
			funcName = funcName.substring(3);
			String topic = topics.get(funcName);
			if (topic == null) {
				return "No help on " + funcName + ".";
			}
			return Stuff.TEXT_ATTRS_BOLD
					+ "Welcome to the online documentation system\n"
					+ Stuff.TEXT_ATTRS_OFF
					+ "-----------------------------------------------------------------\n"
					+ "Help on " + Stuff.TEXT_ATTRS_BOLD + funcName
					+ Stuff.TEXT_ATTRS_OFF + "\n\n" + topic;
		}
		throw new FunctionNotFoundException(funcName);
	}

}
