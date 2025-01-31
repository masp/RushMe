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

package com.tips48.rushMe.arenas;

import com.tips48.rushMe.RushMe;
import com.tips48.rushMe.custom.GUI.MainHUD;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.gamemodes.GameMode;
import com.tips48.rushMe.packets.PacketArenaUpdate;
import com.tips48.rushMe.packets.PacketInfo;
import com.tips48.rushMe.teams.Team;
import com.tips48.rushMe.util.RMUtils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class Arena {

    private final List<Team> teams;
    private final GameMode gamemode;
    private int timeLeft;
    private final String name;
    private final TIntSet players;
    private boolean started;
    private int startingIn;
    private Vector vec1;
    private Vector vec2;

    private int doSecondScheduler;
    private int startingScheduler;

    private final int creator;

    private final List<Vector> flagLocations = new ArrayList<Vector>();
    private final Map<Team, Vector> captureLocations = new HashMap<Team, Vector>();
    private final List<Vector> objectiveLocations = new ArrayList<Vector>();
    private final List<Vector> activeObjectiveLocations = new ArrayList<Vector>();

    private final UUID uuid;

    private final World world;

    public Arena(GameMode gamemode, String name, int creator, World world,
	    UUID uuid) {
	this.gamemode = gamemode;
	this.name = name;
	this.creator = creator;
	this.world = world;

	teams = gamemode.getTeams();

	players = new TIntHashSet();
	started = false;

	startingIn = -1;
	doSecondScheduler = -1;
	this.uuid = uuid;

	for (Team team : this.teams) {
	    team.setOwnerUUID(this.uuid);
	}

	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);

    }

    public void startCountdownTillStart(int s) {
	startingIn = s;
	startingScheduler = RushMe
		.getInstance()
		.getServer()
		.getScheduler()
		.scheduleSyncRepeatingTask(RushMe.getInstance(),
			new Runnable() {
			    public void run() {
				startingIn--;
				if (startingIn <= 0) {
				    start();
				    RushMe.getInstance().getServer()
					    .getScheduler()
					    .cancelTask(startingScheduler);
				}
			    }
			}, 0, 20);
    }

    public List<Team> getTeams() {
	return teams;
    }

    public Team getTeam(String name) {
	for (Team t : getTeams()) {
	    if (t.getName().equalsIgnoreCase(name)) {
		return t;
	    }
	}
	return null;
    }

    public Team getTeam(UUID uuid) {
	for (Team t : getTeams()) {
	    if (t.getUUID().equals(uuid)) {
		return t;
	    }
	}
	return null;
    }

    public Team getTeamOf(Player player) {
	return getTeamOf(player.getEntityId());
    }

    public Team getTeamOf(int player) {
	for (Team t : getTeams()) {
	    if (t.containsPlayer(player)) {
		return t;
	    }
	}
	return null;
    }

    public void replaceTeam(Team team) {
	Iterator<Team> it = teams.iterator();
	while (it.hasNext()) {
	    Team t = it.next();
	    if (t.getUUID().equals(team.getUUID())) {
		teams.remove(t);
		teams.add(team);
	    }
	}
    }

    public void addTeam(Team team) {
	teams.add(team);
    }

    public void removeTeam(Team team) {
	if (teams.contains(team)) {
	    teams.remove(team);
	}
    }

    public GameMode getGameMode() {
	return gamemode;
    }

    public int getTimeLeft() {
	return timeLeft;
    }

    public String getName() {
	return name;
    }

    public void addPlayer(Player player, Team prefered) {
	addPlayer(player.getEntityId(), prefered);
    }

    public void addPlayer(int player, Team prefered) {
	players.add(player);
	boolean team = false;
	if (prefered != null) {
	    team = prefered.addPlayer(player);
	}
	Random r = new Random();
	while (!team) {
	    Team t = teams.get(r.nextInt(teams.size() - 1));
	    team = t.addPlayer(player);
	}

	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p != null) {
	    SavedInventories.addInventory(p, p.getInventory());
	    p.getInventory().clear();
	    RMUtils.giveAllGuns(p);
	    SavedGamemodes.addGameMode(p, p.getGameMode());
	    p.setGameMode(org.bukkit.GameMode.SURVIVAL);
	    p.setSkin(getTeamOf(p).getSkin());
	    MainHUD h = SpoutGUI.getHudOf(p);
	    if (h != null) {
		h.init();
	    }
	}
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void removePlayer(Player player) {
	removePlayer(player.getEntityId());
    }

    public void removePlayer(int player) {
	if (players.contains(player)) {
	    players.remove(player);
	    SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	    if (p != null) {
		p.resetSkin();
		RMUtils.clearInventoryOfGuns(p);
		if (SavedInventories.hasInventory(p)) {
		    PlayerInventory pi = SavedInventories.getInventory(p);
		    p.getInventory().setContents(pi.getContents());
		    p.getInventory().setArmorContents(pi.getArmorContents());
		    SavedInventories.removeInventory(p);
		}
		if (SavedGamemodes.hasGameMode(p)) {
		    p.setGameMode(SavedGamemodes.getGameMode(p));
		    SavedGamemodes.removeGameMode(p);
		}
		MainHUD h = SpoutGUI.getHudOf(p);
		if (h != null) {
		    h.shutdown();
		}
	    }
	}
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public boolean hasPlayer(Player player) {
	return hasPlayer(player.getEntityId());
    }

    public boolean hasPlayer(int player) {
	return players.contains(player);
    }

    public TIntSet getPlayers() {
	return players;
    }

    public boolean isStarted() {
	return started;
    }

    public String getTimeBeforeStart() {
	if (started) {
	    return "Already started";
	}
	if (startingIn == -1) {
	    return "Not starting";
	}
	return RMUtils.parseIntForMinute(startingIn);
    }

    private void doSecond() {
	timeLeft--;
	if (timeLeft == 0) {
	    stop();
	}
	int currentStatus = 0;
	for (Team team : teams) {
	    if (currentStatus == 1) {
		// Won
		team.doWon();
		continue;
	    }
	    if (currentStatus == 2) {
		// Lost
		team.doLost();
	    }
	    int status = team.checkStatus();
	    if (status == 0) {
		// Nothing
		continue;
	    } else if (status == 1) {
		// Won
		team.doWon();
		currentStatus = 1;
	    } else {
		// Lost
		team.doLost();
		currentStatus = 2;
	    }
	}
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public int getCreator() {
	return creator;
    }

    public void start() {
	if (started) {
	    return;
	}
	RushMe.getInstance().getServer().getScheduler()
		.cancelTask(startingScheduler);
	startingScheduler = 0;
	startingIn = 0;
	timeLeft = gamemode.getTime();
	doSecondScheduler = RushMe
		.getInstance()
		.getServer()
		.getScheduler()
		.scheduleSyncRepeatingTask(RushMe.getInstance(),
			new Runnable() {
			    public void run() {
				doSecond();
			    }
			}, 0, 20);
	started = true;
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void stop() {
	if (!started) {
	    return;
	}
	RushMe.getInstance().getServer().getScheduler()
		.cancelTask(doSecondScheduler);
	doSecondScheduler = 0;
	for (int player : getPlayers().toArray()) {
	    Player p = SpoutManager.getPlayerFromId(player);
	    if (p != null) {
		MainHUD hud = SpoutGUI.getHudOf(p);
		if (hud != null) {
		    hud.shutdown();
		}
	    }
	}
	for (Team team : teams) {
	    team.reset();
	}
	startCountdownTillStart(60);
	started = false;
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public Vector getVector1() {
	return vec1;
    }

    public Vector getVector2() {
	return vec2;
    }

    public void setVector1(Vector loc) {
	if (vec1 == null) {
	    vec1 = loc;
	    if (vec2 != null) {
		organizeVectors();
	    }
	}
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    private void organizeVectors() {
	int minX = vec1.getBlockX();
	int minZ = vec1.getBlockZ();
	int maxX = minX;
	int maxZ = minZ;

	int x = vec2.getBlockX();
	int z = vec2.getBlockZ();

	if (x < minX) {
	    minX = x;
	}
	if (z < minZ) {
	    minZ = z;
	}

	if (x > maxX) {
	    maxX = x;
	}
	if (z > maxZ) {
	    maxZ = z;
	}

	vec1 = new Vector(minX, vec1.getY(), minZ);
	vec2 = new Vector(maxX, vec2.getY(), maxZ);
    }

    public void setVector2(Vector loc) {
	if (vec2 == null) {
	    vec2 = loc;
	    if (vec1 != null) {
		organizeVectors();
	    }
	}
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public boolean inArena(Vector vec) {
	if (!getCompleted()) {
	    return true;
	}
	final double x = vec.getX();
	final double z = vec.getZ();
	return (x >= vec1.getBlockX()) && (x < (vec2.getBlockX() + 1))
		&& (z >= vec1.getBlockZ()) && (z < (vec2.getBlockZ() + 1));
    }

    public List<Vector> getFlags() {
	return flagLocations;
    }

    public void addFlag(Vector flag) {
	flagLocations.add(flag);
	flag.toLocation(world).getBlock().setType(Material.BEDROCK);
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public List<Vector> getObjectives() {
	return objectiveLocations;
    }

    public List<Vector> getActiveObjectives() {
	return activeObjectiveLocations;
    }

    public void addActiveObjective(Vector activeObjective) {
	activeObjectiveLocations.add(activeObjective);
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void removeActiveObjective(Vector activeObjective) {
	if (activeObjectiveLocations.contains(activeObjective)) {
	    activeObjectiveLocations.remove(activeObjective);
	    PacketArenaUpdate packet = new PacketArenaUpdate();
	    packet.processArena(this);
	    packet.send(RMUtils.getSpoutPlayers());
	    PacketInfo.addPacket(packet, packet);
	}
    }

    public void addObjective(Vector objective) {
	objectiveLocations.add(objective);
	objective.toLocation(world).getBlock().setType(Material.BEDROCK);
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void addCapturePoint(Team team, Vector capturePoint) {
	captureLocations.put(team, capturePoint);
	capturePoint.toLocation(world).getBlock().setType(Material.BEDROCK);
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public Map<Team, Vector> getCapturePoints() {
	return captureLocations;
    }

    public boolean getCompleted() {
	return (vec1 != null) && (vec2 != null);
    }

    public void onDelete() {
	stop();
	if (flagLocations != null) {
	    for (Vector v : flagLocations) {
		v.toLocation(world).getBlock().setTypeId(0);
	    }
	}
	if (captureLocations != null) {
	    for (Vector v : captureLocations.values()) {
		v.toLocation(world).getBlock().setTypeId(0);
	    }
	}
	if (objectiveLocations != null) {
	    for (Vector v : objectiveLocations) {
		v.toLocation(world).getBlock().setTypeId(0);
	    }
	}
	for (int player : players.toArray()) {
	    removePlayer(player);
	}
	PacketArenaUpdate packet = new PacketArenaUpdate();
	packet.processArena(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public List<Team> getOtherTeams(Team team) {
	List<Team> tempTeams = new ArrayList<Team>(teams.size());
	for (Team t : this.teams) {
	    if (!(t.equals(team))) {
		tempTeams.add(t);
	    }
	}
	return tempTeams;
    }

    public int numberOfTeams() {
	return teams.size();
    }

    public World getWorld() {
	return world;
    }

    public UUID getUUID() {
	return uuid;
    }

    private static class SavedInventories {
	private static final Map<String, PlayerInventory> inventories = new HashMap<String, PlayerInventory>();

	protected static PlayerInventory getInventory(String player) {
	    return inventories.get(player);
	}

	protected static PlayerInventory getInventory(Player player) {
	    return getInventory(player.getName());
	}

	protected static boolean hasInventory(Player player) {
	    return hasInventory(player.getName());
	}

	protected static boolean hasInventory(String player) {
	    return inventories.containsKey(player);
	}

	protected static void removeInventory(Player player) {
	    removeInventory(player.getName());
	}

	protected static void removeInventory(String player) {
	    inventories.remove(player);
	}

	protected static void addInventory(Player player, PlayerInventory pi) {
	    addInventory(player.getName(), pi);
	}

	protected static void addInventory(String player, PlayerInventory pi) {
	    inventories.put(player, pi);
	}

    }

    private static class SavedGamemodes {
	private static final Map<String, org.bukkit.GameMode> gamemodes = new HashMap<String, org.bukkit.GameMode>();

	protected static org.bukkit.GameMode getGameMode(String player) {
	    return gamemodes.get(player);
	}

	protected static org.bukkit.GameMode getGameMode(Player player) {
	    return getGameMode(player.getName());
	}

	protected static boolean hasGameMode(Player player) {
	    return hasGameMode(player.getName());
	}

	protected static boolean hasGameMode(String player) {
	    return gamemodes.containsKey(player);
	}

	protected static void removeGameMode(Player player) {
	    removeGameMode(player.getName());
	}

	protected static void removeGameMode(String player) {
	    gamemodes.remove(player);
	}

	protected static void addGameMode(Player player, org.bukkit.GameMode g) {
	    addGameMode(player.getName(), g);
	}

	protected static void addGameMode(String player, org.bukkit.GameMode g) {
	    gamemodes.put(player, g);
	}

    }
}
