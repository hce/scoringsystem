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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hcesperer.utils.DBConnection;

/**
 * Scoring bot service manager.
 * 
 * This class creates an instance of 'ServiceHandler' for each (service, team)
 * pair, creates a thread for each instance, and then starts these threads.
 * 
 * After all threads are started, it waits for commands on stdin.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class ServiceManager {

	private int serviceID;

	private FlushingLoggingPrintStream writer;

	public ServiceManager(FlushingLoggingPrintStream writer2, int serviceID) {
		this.serviceID = serviceID;
		this.writer = writer2;
	}

	public boolean setscript(String name) {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("update services set service_script=? where uid=?");
			ps.setString(1, name);
			ps.setInt(2, serviceID);
			ps.execute();
			writer.println("Name set");
			return true;
		} catch (SQLException e) {
			writer.println("Error: " + e);
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
		return false;
	}

	public String getscript() {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("select service_script from services where uid=?");
			ps.setInt(1, this.serviceID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				writer.println("Error: can't find team!?");
				return null;
			}
			return rs.getString(1);
		} catch (SQLException e) {
			writer.println("Error: " + e);
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
		return null;
	}

	public boolean setname(String name) {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("update services set service_name=? where uid=?");
			ps.setString(1, name);
			ps.setInt(2, serviceID);
			ps.execute();
			writer.println("Name set");
			return true;
		} catch (SQLException e) {
			writer.println("Error: " + e);
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
		return false;
	}

	public String getname() {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("select service_name from services where uid=?");
			ps.setInt(1, this.serviceID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				writer.println("Error: can't find team!?");
				return null;
			}
			return rs.getString(1);
		} catch (SQLException e) {
			writer.println("Error: " + e);
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
		return null;
	}

	public boolean setinterval(int interval) {
		return setValue("service_check_interval", interval);
	}

	public int getinterval() {
		return getValue("service_check_interval");
	}

	public boolean delete() throws SQLException {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("delete from services where uid=?");
			ps.setInt(1, serviceID);
			return ps.execute();
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
	}

	protected boolean setValue(String valueName, int points) {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("update services set " + valueName
							+ "=? where uid=?");
			ps.setInt(1, points);
			ps.setInt(2, serviceID);
			ps.execute();
			return true;
		} catch (SQLException e) {
			writer.println("Error: " + e);
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
		return false;
	}

	protected int getValue(String valueName) {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection.prepareStatement("select "
					+ valueName + " from services where uid=?");
			ps.setInt(1, serviceID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				writer.println("Service doesn't exist!?");
				return -1;
			}
			return rs.getInt(1);
		} catch (SQLException e) {
			writer.println("Error: " + e);
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
		return -1;
	}

	public String toString() {
		return "Instance of class Service <" + this.serviceID + ":" + getname()
				+ ">";
	}
}
