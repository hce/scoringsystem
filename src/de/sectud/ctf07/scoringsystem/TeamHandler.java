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

public class TeamHandler {

	private int teamID;

	private FlushingLoggingPrintStream writer;

	public TeamHandler(FlushingLoggingPrintStream writer2, int teamID) {
		this.teamID = teamID;
		this.writer = writer2;
	}

	public boolean sethost(String name) {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("update teams set team_host=? where uid=?");
			ps.setString(1, name);
			ps.setInt(2, teamID);
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

	public boolean delete() throws SQLException {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("delete from teams where uid=?");
			ps.setInt(1, teamID);
			return ps.execute();
		} finally {
			DBConnection.getInstance().returnConnection(connection);
			connection = null;
		}
	}

	public String gethost() {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("select team_host from teams where uid=?");
			ps.setInt(1, this.teamID);
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
					.prepareStatement("update teams set team_name=? where uid=?");
			ps.setString(1, name);
			ps.setInt(2, teamID);
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
					.prepareStatement("select team_name from teams where uid=?");
			ps.setInt(1, this.teamID);
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

	public boolean setop(int points) {
		return setValue("team_points_offensive", points);
	}

	public int getop() {
		return getValue("team_points_offensive");
	}

	public boolean setdp(int points) {
		return setValue("team_points_defensive", points);
	}

	public int getdp() {
		return getValue("team_points_defensive");
	}

	public boolean setap(int points) {
		return setValue("team_points_advisories", points);
	}

	public int getap() {
		return getValue("team_points_advisories");
	}

	public boolean sethp(int points) {
		return setValue("team_points_hacking", points);
	}

	public int gethp() {
		return getValue("team_points_hacking");
	}

	protected boolean setValue(String valueName, int points) {
		Connection connection = DBConnection.getInstance().getDB();
		try {
			PreparedStatement ps = connection
					.prepareStatement("update teams set " + valueName
							+ "=? where uid=?");
			ps.setInt(1, points);
			ps.setInt(2, teamID);
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
					+ valueName + " from teams where uid=?");
			ps.setInt(1, teamID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				writer.println("Team doesn't exist!?");
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
		return "Instance of class Team <" + this.teamID + ":" + getname() + ">";
	}
}
