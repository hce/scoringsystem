package de.sectud.ctf07.scoringsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.hcesperer.utils.DBConnection;

public class ServiceStatusUI implements Runnable {
	private JFrame frame;

	private JLabel[][] labels;

	private Map<String, JLabel> hashMap = new HashMap<String, JLabel>();

	private static Map<String, Color> stateColors = new HashMap<String, Color>();

	private JPanel statusPanel;

	static {
		stateColors.put("not running", Color.RED);
		stateColors.put("accepting flags", Color.YELLOW);
		stateColors.put("timeout", Color.RED);
		stateColors.put("broken", Color.YELLOW);
		stateColors.put("running", Color.GREEN);
	}

	public ServiceStatusUI() {
		super();

		frame = new JFrame();
		statusPanel = new JPanel();
		statusPanel.setLayout(new GridBagLayout());
		frame.add(statusPanel);

		try {
			addUI(frame);
		} catch (SQLException e) {
		}

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void addUI(JFrame frame2) throws SQLException {
		Connection c = DBConnection.getInstance().getDB();
		try {
			ArrayList<String> services = new ArrayList<String>();
			Statement s = c.createStatement();
			ResultSet rs = s
					.executeQuery("select service_name from services order by service_name");
			while (rs.next()) {
				services.add(rs.getString(1));
			}
			rs.close();

			ArrayList<String> teams = new ArrayList<String>();
			rs = s
					.executeQuery("select team_name from teams order by team_name");
			while (rs.next()) {
				teams.add(rs.getString(1));
			}
			rs.close();

			int nServices = services.size();
			int nTeams = teams.size();
			labels = new JLabel[nServices + 1][nTeams + 1];
			for (int i = 0; i <= nServices; i++) {
				for (int j = 0; j <= nTeams; j++) {
					labels[i][j] = new JLabel();
					GridBagConstraints gbc = new GridBagConstraints();
					gbc.gridx = i;
					gbc.gridy = j;
					labels[i][j].setPreferredSize(new Dimension(210, 20));
					statusPanel.add(labels[i][j], gbc);
				}
			}
			for (int i = 0; i < nServices; i++) {
				for (int j = 0; j < nTeams; j++) {
					hashMap.put(teams.get(j) + " " + services.get(i),
							labels[i + 1][j + 1]);
				}
			}

			Font fnt = labels[0][0].getFont().deriveFont(Font.BOLD, 18);
			for (int i = 0; i < nServices; i++) {
				labels[i + 1][0].setText(services.get(i));
				labels[i + 1][0].setFont(fnt);
			}
			for (int i = 0; i < nTeams; i++) {
				labels[0][i + 1].setText(teams.get(i));
				labels[0][i + 1].setFont(fnt);
			}
		} finally {
			DBConnection.getInstance().returnConnection(c);
			c = null;
		}
	}

	public void run() {
		try {
			while (true) {
				Connection c = DBConnection.getInstance().getDB();
				try {
					Statement s = c.createStatement();
					ResultSet rs = s
							.executeQuery("select status_team,status_service,status_text from states");
					while (rs.next()) {
						String team = rs.getString(1);
						String service = rs.getString(2);
						String status = rs.getString(3);
						JLabel l = hashMap.get(team + " " + service);
						Color clr = stateColors.get(status);
						if (l == null) {
							System.err.println("WTF!? combination (" + team
									+ ";" + service + ") doesn't exist!");
						} else {
							l.setText(status);
							if (clr != null) {
								l.setForeground(clr);
							} else {
								l.setForeground(Color.BLACK);
							}
						}
					}
					rs.close();
				} catch (SQLException e) {
					System.err.println(e.getLocalizedMessage());
				} finally {
					DBConnection.getInstance().returnConnection(c);
					c = null;
				}
				Thread.sleep(60000);
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted!");
		}
	}

	public static void main(String[] args) {
		ServiceStatusUI ssui = new ServiceStatusUI();
		new Thread(ssui).run();
	}
}
