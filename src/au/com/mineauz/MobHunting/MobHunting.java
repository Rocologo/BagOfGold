package au.com.mineauz.MobHunting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.MobHunting.achievements.*;
import au.com.mineauz.MobHunting.compatability.MinigamesCompat;

public class MobHunting extends JavaPlugin implements Listener
{
	private Economy mEconomy;
	public static MobHunting instance;
	
	private WeakHashMap<Creature, DamageInformation> mDamageHistory = new WeakHashMap<Creature, DamageInformation>();
	private Config mConfig;
	
	private AchievementManager mAchievements;
	private double cDampnerRange = 15;
	
	// Compatability classes
	@SuppressWarnings( "unused" )
	private MinigamesCompat mMinigames;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if(economyProvider == null)
		{
			instance = null;
			throw new RuntimeException("Unable to hook into an economy! Make sure you have one available that Vault accepts.");
		}
		
		mEconomy = economyProvider.getProvider();
		
		mConfig = new Config(new File(getDataFolder(), "config.yml"));
		
		if(mConfig.load())
			mConfig.save();
		else
			throw new RuntimeException("There was a problem loading the MobHunting config");
		
		// Handle compatability stuff
		if(Bukkit.getPluginManager().isPluginEnabled("Minigames"))
			mMinigames = new MinigamesCompat();
		
		// Load all the achievements
		mAchievements = new AchievementManager();
		
		mAchievements.registerAchievement(new AxeMurderer());
		mAchievements.registerAchievement(new CreeperBoxing());
		mAchievements.registerAchievement(new Electrifying());
		mAchievements.registerAchievement(new RecordHungry());
		mAchievements.registerAchievement(new InFighting());
		mAchievements.registerAchievement(new ByTheBook());
		
		mAchievements.initialize();
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public static Economy getEconomy()
	{
		return instance.mEconomy;
	}
	
	public static Config config()
	{
		return instance.mConfig;
	}
	
	private HuntData getHuntData(Player player)
	{
		HuntData data = null;
		if(!player.hasMetadata("MobHuntData"))
		{
			data = new HuntData();
			player.setMetadata("MobHuntData", new FixedMetadataValue(this, data));
		}
		else
		{
			if(!(player.getMetadata("MobHuntData").get(0).value() instanceof HuntData))
			{
				player.getMetadata("MobHuntData").get(0).invalidate();
				player.setMetadata("MobHuntData", new FixedMetadataValue(this, new HuntData()));
			}
			
			data = (HuntData)player.getMetadata("MobHuntData").get(0).value();
		}

		return data;
	}
	
	public static boolean isHuntEnabled(Player player)
	{
		if(!player.hasMetadata("MH:enabled"))
			return false;
		
		List<MetadataValue> values = player.getMetadata("MH:enabled");
		
		// Use the first value that matches the required type
		boolean enabled = false;
		for(MetadataValue value : values)
		{
			if(value.value() instanceof Boolean)
				enabled = value.asBoolean();
		}
		
		if(enabled && !player.hasPermission("mobhunting.enable"))
			return false;
		
		return enabled;
	}
	
	public static boolean isHuntEnabledInWorld(World world)
	{
		for(String worldName : config().disabledInWorlds)
		{
			if(world.getName().equalsIgnoreCase(worldName))
				return false;
		}
		
		return true;
	}
	
	public static void setHuntEnabled(Player player, boolean enabled)
	{
		if(enabled)
			instance.getLogger().info("Enabling MobHunt for " + player.getName());
		else
			instance.getLogger().info("Disabling MobHunt for " + player.getName());
		
		player.setMetadata("MH:enabled", new FixedMetadataValue(instance, enabled));
	}
	
	private boolean isSword(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.GOLD_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.WOOD_SWORD);
	}
	
	private boolean isAxe(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_AXE || item.getType() == Material.GOLD_AXE || item.getType() == Material.IRON_AXE || item.getType() == Material.STONE_AXE || item.getType() == Material.WOOD_AXE);
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event)
	{
		HuntData data = getHuntData(event.getEntity());
		if(data.getKillstreakLevel() != 0)
			event.getEntity().sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Killstreak ended");
		data.killStreak = 0;
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerDamage(EntityDamageByEntityEvent event)
	{
		if(!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player)event.getEntity();
		HuntData data = getHuntData(player);
		if(data.getKillstreakLevel() != 0)
			player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Killstreak ended");
		data.killStreak = 0;
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onMobDamage(EntityDamageByEntityEvent event)
	{
		if(!(event.getEntity() instanceof Creature) || !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		DamageInformation info = null;
		if(mDamageHistory.containsKey(event.getEntity()))
			info = mDamageHistory.get(event.getEntity());
		else
			info = new DamageInformation();
		
		info.time = System.currentTimeMillis();
		
		Player cause = null;
		ItemStack weapon = null;
		
		if(event.getDamager() instanceof Player)
			cause = (Player)event.getDamager();
			
		boolean projectile = false;
		if(event.getDamager() instanceof Projectile)
		{
			if(((Projectile)event.getDamager()).getShooter() instanceof Player)
				cause = (Player)((Projectile)event.getDamager()).getShooter();
			
			if(event.getDamager() instanceof ThrownPotion)
				weapon = ((ThrownPotion)event.getDamager()).getItem();
			
			info.mele = false;
			projectile = true;
		}
		else
			info.mele = true;
		
		if(weapon == null && cause != null)
			weapon = cause.getItemInHand();
		
		if(weapon != null)
			info.weapon = weapon;
		
		info.attacker = cause;
		
		// Take note that a weapon has been used at all
		if(info.weapon != null && (isSword(info.weapon) || isAxe(info.weapon) || projectile))
			info.usedWeapon = true;
		
		if(cause != null)
		{
			if(cause.isFlying())
				info.wasFlying = true;
			
			info.attackerPosition = cause.getLocation().clone();
			mDamageHistory.put((Creature)event.getEntity(), info);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onMobDeath(EntityDeathEvent event)
	{
		if(event.getEntity() instanceof Player || getBaseKillPrize(event.getEntity()) == 0 || !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		Player killer = event.getEntity().getKiller();
		
		// Handle special case of skele kill creeper, and skeke kills a skele
		if(event.getEntity() instanceof Monster && killer == null && event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			if(((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager() instanceof Arrow && 
					((Arrow)((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager()).getShooter() instanceof Skeleton)
			{
				Skeleton skele = (Skeleton)((Arrow)((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager()).getShooter();
				
				if(event.getEntity() instanceof Creeper)
				{
					if(((Creeper)event.getEntity()).getTarget() instanceof Player)
					{
						Player target = (Player)((Creeper)event.getEntity()).getTarget();
						
						if(skele.getTarget() == target && target.getGameMode() != GameMode.CREATIVE && isHuntEnabled(target))
							mAchievements.awardAchievement("recordhungry", target);
					}
				}
				else if(event.getEntity() instanceof Skeleton)
				{
					if(((Skeleton)event.getEntity()).getTarget() == skele)
					{
						// Unfortunatly there is no way of finding who caused the fight, so just use who ever the closest player is
						Player closest = null;
						for(Player player : Bukkit.getOnlinePlayers())
						{
							if(player.getWorld() != event.getEntity().getWorld())
								continue;
							
							if(player.getGameMode() == GameMode.CREATIVE)
								continue;
							
							if(closest == null || closest.getLocation().distanceSquared(event.getEntity().getLocation()) > player.getLocation().distanceSquared(event.getEntity().getLocation()))
								closest = player;
						}
						
						if(closest != null && closest.getLocation().distance(event.getEntity().getLocation()) < 50 && isHuntEnabled(closest))
							mAchievements.awardAchievement("infighting", closest);
					}
				}
			}
		}
		
		DamageInformation info = null;
		if(event.getEntity() instanceof Creature && mDamageHistory.containsKey((Creature)event.getEntity()))
		{
			info = mDamageHistory.get(event.getEntity());
			
			if(System.currentTimeMillis() - info.time > 4000)
				info = null;
			else if(killer == null)
				killer = info.attacker;
		}
		else if(killer != null)
		{
			info = new DamageInformation();
			info.time = System.currentTimeMillis();
			info.attacker = killer;
			info.attackerPosition = killer.getLocation();
			info.usedWeapon = true;
		}
		
		if(killer == null || killer.getGameMode() == GameMode.CREATIVE || !isHuntEnabled(killer))
			return;
		
		HuntData data = getHuntData(killer);
		int lastKillstreakLevel = data.getKillstreakLevel();
		data.killStreak++;
		
		// If its a charged creeper, give the award
		if(event.getEntity() instanceof Creeper)
		{
			if(((Creeper)event.getEntity()).isPowered())
				mAchievements.awardAchievement("electrifying", killer);
		}
		
		// Give a message notifying of killstreak increase
		if(data.getKillstreakLevel() != lastKillstreakLevel)
		{
			switch(data.getKillstreakLevel())
			{
			case 1:
				killer.sendMessage(ChatColor.BLUE + "Nice!");
				break;
			case 2:
				killer.sendMessage(ChatColor.BLUE + "Super!");
				break;
			case 3:
				killer.sendMessage(ChatColor.BLUE + "Killing Machine!");
				break;
			default:
				killer.sendMessage(ChatColor.BLUE + "Unstoppable!");
				break;
			}
			
			killer.sendMessage(ChatColor.GRAY + String.format("x%.1f Activated", data.getKillstreakMultiplier()));
		}
		
		// Record kills that are still within a small area
		Location loc = event.getEntity().getLocation();
		
		// Slimes are except from grinding due to their splitting nature
		if(!(event.getEntity() instanceof Slime))
		{
			if(data.lastKillAreaCenter != null)
			{
				if(loc.getWorld().equals(data.lastKillAreaCenter.getWorld()))
				{
					if(loc.distance(data.lastKillAreaCenter) < cDampnerRange)
						data.dampenedKills++;
					else
					{
						data.lastKillAreaCenter = loc.clone();
						data.dampenedKills = 0;
					}
				}
				else
				{
					data.lastKillAreaCenter = loc.clone();
					data.dampenedKills = 0;
				}
			}
			else
			{
				data.lastKillAreaCenter = loc.clone();
				data.dampenedKills = 0;
			}
			
			if(data.dampenedKills > 14)
			{
				if(data.getKillstreakLevel() != 0)
					killer.sendMessage(ChatColor.RED + "Killstreak Lost");
				data.killStreak = 0;
			}
		}
		
		double cash = getBaseKillPrize(event.getEntity());
		double multiplier = 1.0;
		
		ArrayList<String> bonuses = new ArrayList<String>();
		
		Location attackLocation = killer.getLocation();
		
		ItemStack weapon = null;
		LivingEntity ent = event.getEntity();
		
		if(ent.getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			weapon = info.weapon;
			
			attackLocation = info.attackerPosition;

			Entity cause = ((EntityDamageByEntityEvent)ent.getLastDamageCause()).getDamager();
			if(cause instanceof Arrow && weapon == null)
				weapon = new ItemStack(Material.BOW);
			else if(cause instanceof ThrownPotion)
				weapon = ((ThrownPotion)cause).getItem();
			else if(cause instanceof LargeFireball && event.getEntity() instanceof Ghast)
			{
				// Return to sender bonus
				multiplier *= mConfig.bonusReturnToSender;
				bonuses.add(ChatColor.GOLD + "Return To Sender!");
			}
			else if(cause instanceof Player && ent instanceof Creature)
			{
				// If they werent targeting anything, you get a sneaky bonus
				if(((Creature)ent).getTarget() == null)
				{
					multiplier *= mConfig.bonusSneaky;
					bonuses.add(ChatColor.BLUE + "Sneaky!");
				}
			}
		}
		else if(info.attacker == killer)
		{
			switch(ent.getLastDamageCause().getCause())
			{
			case FALL:
				multiplier *= mConfig.bonusSendFalling;
				bonuses.add(ChatColor.AQUA + "A Shove");
				break;
			case FALLING_BLOCK:
				// TODO: Give the player a special kill for this 
				break;
				
			case SUFFOCATION:
				// TODO: Give the player a special kill for this
				break;
			default:
				return;
			}
		}
		
		if(weapon == null)
			weapon = new ItemStack(Material.AIR);

		// This achievement only cares about the death blow
		if(weapon.getType() == Material.BOOK || weapon.getType() == Material.WRITTEN_BOOK || weapon.getType() == Material.BOOK_AND_QUILL)
			mAchievements.awardAchievement("bythebook", killer);
		
		if(!info.usedWeapon)
		{
			multiplier *= mConfig.bonusNoWeapon;
			bonuses.add(ChatColor.RED + "Brawler");
			
			if(event.getEntity() instanceof Creeper)
				mAchievements.awardAchievement("creeperboxing", killer);
		}
		else if(weapon.getType() == Material.BOW && !info.mele)
		{
			double dist = attackLocation.distance(event.getEntity().getLocation());
			// TODO: Make sure target was moving at the time
			if(dist > 50)
			{
				multiplier *= mConfig.bonusFarShot;
				bonuses.add(ChatColor.GRAY + "Pro Sniper");
			}
			else if(dist > 20)
			{
				multiplier *= mConfig.bonusFarShot / 2;
				bonuses.add(ChatColor.GRAY + "Sniper");
			}
		}
		else if(isSword(weapon))
		{
			// Do nothing different?
		}
		else if(isAxe(weapon))
			mAchievements.awardAchievement("axemurderer", killer);
		
		multiplier *= data.getKillstreakMultiplier();
		
		String extraString = "";
		
		// Only display the multiplier if its not 1
		if(Math.abs(multiplier - 1) > 0.05)
			extraString += String.format("x%.1f", multiplier);
		
		// Add on bonuses
		for(String bonus : bonuses)
			extraString += ChatColor.WHITE + " + " + bonus;
		
		// Add on penalties
		if(data.getDampnerMultiplier() < 1)
		{
			extraString += ChatColor.WHITE + " - " + ChatColor.RED + "Grinding Penalty";
			cash *= data.getDampnerMultiplier();
		}
		
		if(info.wasFlying)
		{
			extraString += ChatColor.WHITE + " - " + ChatColor.RED + "Flying Penalty";
			cash *= 0.5;
		}
		
		cash *= multiplier;
		
		if(!extraString.isEmpty())
			extraString = "With: " + extraString;
		
		if(cash > 0.01)
		{
			mEconomy.depositPlayer(killer.getName(), cash);
			killer.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + String.format("You gained $%.2f! %s", cash, extraString));
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event)
	{
		setHuntEnabled(event.getPlayer(), true);
	}
	
	public double getBaseKillPrize(LivingEntity mob)
	{
		if(mob instanceof Blaze)
			return mConfig.blazePrize;
		if(mob instanceof Creeper)
			return mConfig.creeperPrize;
		if(mob instanceof Silverfish)
			return mConfig.silverfishPrize;
		if(mob instanceof Enderman)
			return mConfig.endermenPrize;
		if(mob instanceof Giant)
			return mConfig.giantPrize;
		if(mob instanceof Skeleton)
		{
			switch(((Skeleton)mob).getSkeletonType())
			{
			case NORMAL:
				return mConfig.skeletonPrize;
			case WITHER:
				return mConfig.witherSkeletonPrize;
			}
		}
		if(mob instanceof CaveSpider)
			return mConfig.caveSpiderPrize;
		if(mob instanceof Spider)
			return mConfig.spiderPrize;
		if(mob instanceof Witch)
			return mConfig.witchPrize;
		if(mob instanceof Zombie)
			return mConfig.zombiePrize;
		if(mob instanceof Ghast)
			return mConfig.ghastPrize;
		if(mob instanceof Slime)
			return mConfig.slimeTinyPrize * ((Slime)mob).getSize();
		if(mob instanceof EnderDragon)
			return mConfig.enderdragonPrize;
		if(mob instanceof Wither)
			return mConfig.witherPrize;
		
		return 0;
	}
}
