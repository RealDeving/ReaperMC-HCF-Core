package me.rainny.reaper.listener;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.event.FactionChatEvent;
import me.rainny.reaper.factionutils.struct.ChatChannel;
import me.rainny.reaper.factionutils.type.PlayerFaction;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener
  implements Listener
{
  private static final String EOTW_CAPPER_PREFIX = ChatColor.YELLOW + " Capper ";
  private static final ImmutableSet<UUID> EOTW_CAPPERS;
  private static final String DOUBLE_POST_BYPASS_PERMISSION = "hcf.doublepost.bypass";
  
  static
  {
    ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
    
    EOTW_CAPPERS = builder.build();
  }
  
  private static final Pattern PATTERN = Pattern.compile("\\W");
  private final Map<UUID, String> messageHistory;
  private final HCF plugin;
  
  public ChatListener(HCF plugin)
  {
    this.plugin = plugin;
    
    this.messageHistory = new MapMaker().makeMap();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onPlayerChat(AsyncPlayerChatEvent event)
  {
    String message = event.getMessage();
    Player player = event.getPlayer();
    
    PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
    ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
    
    Set<Player> recipients = event.getRecipients();
    if ((chatChannel == ChatChannel.FACTION) || (chatChannel == ChatChannel.ALLIANCE)) {
      if (isGlobalChannel(message))
      {
        message = message.substring(1, message.length()).trim();
        event.setMessage(message);
      }
      else
      {
        Collection<Player> online = playerFaction.getOnlinePlayers();
        if (chatChannel == ChatChannel.ALLIANCE)
        {
          Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
          for (PlayerFaction ally : allies) {
            online.addAll(ally.getOnlinePlayers());
          }
        }
        recipients.retainAll(online);
        event.setFormat(chatChannel.getRawFormat(player));
        
        Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
        return;
      }
    }
    String rank = ChatColor.translateAlternateColorCodes('&', PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
    String playertag = (playerFaction == null) ? (ChatColor.GRAY + "*") :   playerFaction.getDisplayName(Bukkit.getConsoleSender());
    for (Player recipient : event.getRecipients()) {
    		recipient.sendMessage((ChatColor.DARK_GRAY + "[" + playertag + ChatColor.DARK_GRAY + "] " + rank + player.getName() + ChatColor.DARK_GRAY +  " » " + ChatColor.WHITE + message));
    	

    	
    }
    event.setFormat(ChatColor.DARK_GRAY + "[" + playertag + ChatColor.DARK_GRAY + "] " + rank + player.getName() + ChatColor.DARK_GRAY +  " » " + ChatColor.WHITE + message);
    event.setCancelled(true);
  }
    

  
  private String getFormattedMessage(Player player, PlayerFaction playerFaction, String playerDisplayName, String message, CommandSender viewer)
  {
    String tag = playerFaction == null ? ChatColor.RED + "*" : playerFaction.getDisplayName(viewer);
    return ChatColor.DARK_GRAY + "[" + tag + ChatColor.DARK_GRAY + "] " + (EOTW_CAPPERS.contains(player.getUniqueId()) ? EOTW_CAPPER_PREFIX : "") + 
      String.format(Locale.ENGLISH, playerDisplayName, new Object[] { player.getName(), message });
  }
  
  private boolean isGlobalChannel(String input)
  {
    int length = input.length();
    if ((length <= 1) || (!input.startsWith("!"))) {
      return false;
    }
    for (int i = 1; i < length; i++)
    {
      char character = input.charAt(i);
      if (character != ' ')
      {
        if (character != '/') {
          break;
        }
        return false;
      }
    }
    return true;
  }
}
