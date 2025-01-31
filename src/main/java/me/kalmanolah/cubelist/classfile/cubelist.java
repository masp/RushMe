/*
 * This file is part of RushMe.
 *
 * RushMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RushMe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.kalmanolah.cubelist.classfile;

import com.tips48.rushMe.RushMe;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

public class cubelist {
    private static Plugin plugin;
    private static final String cubelistserverurl = "http://cubelist.me/sync/syncanon.php";

    public cubelist(Plugin plug) {
	plugin = plug;
	File thelockfile = new File("cubelist.lck");
	if (thelockfile.exists()) {
	    if (!(thelockfile.delete())) {
		RushMe.getInstance()
			.getLogger()
			.log(Level.SEVERE,
				"Error deleting " + thelockfile.getName());
	    }
	}
	final File lockfile = thelockfile;
	plugin.getServer().getScheduler()
		.scheduleAsyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
			// It probably wouldn't be healthy for my server if
			// people sync
			// ten times during a minute.
			if (!lockfile.exists()) {
			    if (plugin.getServer().getPluginManager()
				    .getPlugin("CubeList") == null) {
				try {
				    if (!(lockfile.createNewFile())) {
					RushMe.getInstance()
						.getLogger()
						.log(Level.SEVERE,
							"Error creating the file "
								+ lockfile
									.getName());
				    }
				} catch (IOException e) {
				    e.printStackTrace();
				}
				plugin.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(plugin,
						new Runnable() {
						    public void run() {
							if (!(lockfile.delete())) {
							    RushMe.getInstance()
								    .getLogger()
								    .log(Level.SEVERE,
									    "Error deleting the file "
										    + lockfile
											    .getName());
							}
						    }
						}, 20 * 540);
				syncData();
			    }
			}
		    }
		}, 20 * 120, 20 * 600);
    }

    // Do you like POST requests? I love POST requests!
    public static String sendPost(String vars) {
	String result = null;
	try {
	    URL url = new URL(cubelistserverurl);
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(
		    conn.getOutputStream());
	    wr.write(vars);
	    wr.flush();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(
		    conn.getInputStream()));
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = rd.readLine()) != null) {
		sb.append(line);
	    }
	    rd.close();
	    result = sb.toString();
	    wr.close();
	} catch (Exception e) {
	    RushMe.getInstance().getLogger()
		    .log(e, "Error sending post request!");
	}
	return result;
    }

    public static void syncData() {
	Boolean error = false;
	try {
	    // Let's go ahead and sort player names alphabetically, though none
	    // of them will ever get displayed. Genius.
	    String players = "";
	    Player[] plrs = plugin.getServer().getOnlinePlayers();
	    if (plrs.length > 1) {
		List<String> tempplayers = new ArrayList<String>();
		for (Player p : plrs) {
		    tempplayers.add(p.getName());
		}
		String[] tempplayerstwo = new String[tempplayers.size()];
		tempplayers.toArray(tempplayerstwo);
		Arrays.sort(tempplayerstwo, String.CASE_INSENSITIVE_ORDER);
		for (String plr : tempplayerstwo) {
		    players = players + plr + ",";
		}
	    } else {
		for (Player p : plrs) {
		    players = players + p.getName() + ",";
		}
	    }
	    String result = sendPost("seven="
		    + URLEncoder.encode(
			    String.valueOf(plugin.getServer().getPort()),
			    "UTF-8")
		    + "&nine="
		    + URLEncoder.encode(String.valueOf(plugin.getServer()
			    .getOnlinePlayers().length), "UTF-8") + "&ten="
		    + URLEncoder.encode(players, "UTF-8"));
	    for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
		if (!p.getDescription().getName().equals("CubeList")) {
		    String authors = "";
		    for (String s : p.getDescription().getAuthors()) {
			authors = authors + s + ",";
		    }
		    String syncedplugins = p.getDescription().getName()
			    + "%BREAK%" + p.getDescription().getVersion()
			    + "%BREAK%" + p.getDescription().getDescription()
			    + "%BREAK%" + authors + "%BREAK%"
			    + p.getDescription().getWebsite();
		    String results = sendPost("six="
			    + URLEncoder.encode(syncedplugins, "UTF-8")
			    + "&seven="
			    + URLEncoder.encode(String.valueOf(plugin
				    .getServer().getPort()), "UTF-8"));
		    if (results != null) {
			if (!results.equalsIgnoreCase("OK")) {
			    error = true;
			}
		    } else {
			error = true;
		    }
		}
	    }
	    if (result != null) {
		if (!result.equalsIgnoreCase("OK")) {
		    error = true;
		}
	    } else {
		error = true;
	    }
	} catch (Exception e) {
	    error = true;
	}
	if (error) {
	    // This never happens because I am awesome.
	} else {
	    // Just as planned.
	}
    }
}
