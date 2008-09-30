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

package org.hcesperer.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnection {

	private Properties properties;

	private String connectionString;

	private String user;

	private String password;

	private static SQLConnection si = null;

	private SQLConnection() throws FileNotFoundException, IOException {
		properties = new Properties();

		try {
			properties.load(new FileInputStream("settings"));
		} catch (Exception e) {
		}

		connectionString = properties.getProperty("connectionString",
				"jdbc:postgresql://localhost/ctf");
		properties.put("connectionString", connectionString);

		user = properties.getProperty("user", "ctf");
		properties.put("user", user);

		password = properties.getProperty("password", "ctfIScool");
		properties.put("password", password);

		properties.store(new FileOutputStream("settings"),
				"(C) 2007-2008, Hans-Christian Esperer.");

	}

	public String getProperty(String name, String defaultValue) {
		String prop = properties.getProperty(name, defaultValue);
		if (prop.equals(defaultValue)) {
			setProperty(name, prop);
		}
		return prop;
	}

	public void setProperty(String name, String value) {
		properties.setProperty(name, value);
		try {
			properties.store(new FileOutputStream("settings"),
					"(C) 2007, Christian Esperer.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SQLConnection getInstance() {
		if (si == null) {
			try {
				si = new SQLConnection();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return si;
	}

	public Connection newConnection() throws ClassNotFoundException,
			SQLException {
		return newConnection(this.properties);
	}

	private Connection newConnection(Properties props)
			throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");

		return DriverManager.getConnection(connectionString, props);
	}

}
