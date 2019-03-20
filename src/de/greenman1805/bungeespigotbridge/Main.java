package de.greenman1805.bungeespigotbridge;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements PluginMessageListener, Listener {
	public static String PluginChannel = "BungeeCord";
	public static Economy econ = null;

	@Override
	public void onEnable() {
		setupEconomy();
		this.getServer().getMessenger().registerIncomingPluginChannel(this, PluginChannel, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, PluginChannel);
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		if (channel.equals(PluginChannel)) {
			String type = in.readUTF();
			if (type.equalsIgnoreCase("TeleportPlayerToPlayer")) {
				Player player1 = Bukkit.getPlayer(in.readUTF());
				Player player2 = Bukkit.getPlayer(in.readUTF());

				if (player1 != null && player2 != null) {
					player1.teleport(player2);
				}
			} else if (type.equalsIgnoreCase("GetLocation")) {
				String name = in.readUTF();
				String id = in.readUTF();
				Player p = Bukkit.getPlayer(name);
				if (p != null) {
					Location l = p.getLocation();
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("SentLocation");
					out.writeUTF(id + "");
					out.writeUTF(l.getWorld().getName());
					out.writeUTF(l.getBlockX() + "");
					out.writeUTF(l.getBlockY() + "");
					out.writeUTF(l.getBlockZ() + "");
					p.sendPluginMessage(this, PluginChannel, out.toByteArray());
				}
			} else if (type.equalsIgnoreCase("GetBaseLocation")) {
				String name = in.readUTF();
				Player p = Bukkit.getPlayer(name);
				if (p != null) {
					Location l = p.getLocation();
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("SentBaseLocation");
					out.writeUTF(name);
					out.writeUTF(l.getWorld().getName());
					out.writeUTF(l.getBlockX() + "");
					out.writeUTF(l.getBlockY() + "");
					out.writeUTF(l.getBlockZ() + "");
					p.sendPluginMessage(this, PluginChannel, out.toByteArray());
				}
			} else if (type.equalsIgnoreCase("Teleport")) {
				String name = in.readUTF();
				Player p = Bukkit.getPlayer(name);
				if (p != null) {
					Location l = new Location(Bukkit.getWorld(in.readUTF()), Double.parseDouble(in.readUTF()), Double.parseDouble(in.readUTF()), Double.parseDouble(in.readUTF()));
					p.teleport(l);
				}
			} else if (type.equalsIgnoreCase("GiveMoney")) {
				String name = in.readUTF();
				String money = in.readUTF();
				Player p = Bukkit.getPlayer(name);
				if (p != null) {
					econ.depositPlayer(p, Double.parseDouble(money));
				}
			}

		}
	}
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}


}
