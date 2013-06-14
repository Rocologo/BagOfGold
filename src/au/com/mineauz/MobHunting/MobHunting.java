package au.com.mineauz.MobHunting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.MobHunting.achievements.*;
import au.com.mineauz.MobHunting.commands.CheckGrindingCommand;
import au.com.mineauz.MobHunting.commands.CommandDispatcher;
import au.com.mineauz.MobHunting.commands.ListAchievementsCommand;
import au.com.mineauz.MobHunting.commands.ReloadCommand;
import au.com.mineauz.MobHunting.compatability.MinigamesCompat;
import au.com.mineauz.MobHunting.modifier.*;

public class MobHunting extends JavaPlugin implements Listener
{
	private Economy mEconomy;
	public static MobHunting instance;
	
	private WeakHashMap<Creature, DamageInformation> mDamageHistory = new WeakHashMap<Creature, DamageInformation>();
	private Config mConfig;
	
	private AchievementManager mAchievements;
	public static double cDampnerRange = 15;
	
	private Set<IModifier> mModifiers;
	
	private ArrayList<Area> mKnownGrindingSpots = new ArrayList<Area>();
	
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
		
		CommandDispatcher cmd = new CommandDispatcher("mobhunt", "Allows you to configure Mob Hunting");
		getCommand("mobhunt").setExecutor(cmd);
		getCommand("mobhunt").setTabCompleter(cmd);
		
		cmd.registerCommand(new ReloadCommand());
		cmd.registerCommand(new ListAchievementsCommand());
		cmd.registerCommand(new CheckGrindingCommand());
		
		registerAchievements();
		registerModifiers();
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	private void registerAchievements()
	{
		mAchievements = new AchievementManager();
		
		mAchievements.registerAchievement(new AxeMurderer());
		mAchievements.registerAchievement(new CreeperBoxing());
		mAchievements.registerAchievement(new Electrifying());
		mAchievements.registerAchievement(new RecordHungry());
		mAchievements.registerAchievement(new InFighting());
		mAchievements.registerAchievement(new ByTheBook());
		mAchievements.registerAchievement(new Creepercide());
		mAchievements.registerAchievement(new TheHuntBegins());
		mAchievements.registerAchievement(new ItsMagic());
		mAchievements.registerAchievement(new FancyPants());
		
		mAchievements.initialize();
	}
	
	private void registerModifiers()
	{
		mModifiers = new HashSet<IModifier>();
		mModifiers.add(new BrawlerBonus());
		mModifiers.add(new ProSniperBonus());
		mModifiers.add(new SniperBonus());
		mModifiers.add(new ReturnToSenderBonus());
		mModifiers.add(new ShoveBonus());
		mModifiers.add(new SneakyBonus());
		
		mModifiers.add(new FlyingPenalty());
		mModifiers.add(new GrindingPenalty());
	}
	
	void registerKnownGrindingSpot(Area newArea)
	{
		for(Area area : mKnownGrindingSpots)
		{
			if(newArea.center.getWorld().equals(area.center.getWorld()))
			{
				double dist = newArea.center.distance(area.center);
				
				double remaining = dist;
				remaining -= area.range;
				remaining -= newArea.range;
				
				if(remaining < 0)
				{
					if(dist > area.range)
						area.range = dist;
					
					area.count += newArea.count;
					
					return;
				}
			}
		}
		
		mKnownGrindingSpots.add(newArea);
	}
	
	public Area getGrindingArea(Location location)
	{
		for(Area area : mKnownGrindingSpots)
		{
			if(area.center.getWorld().equals(location.getWorld()))
			{
				if(area.center.distance(location) < area.range)
					return area;
			}
		}
		
		return null;
	}
	
	public static Economy getEconomy()
	{
		return instance.mEconomy;
	}
	
	public static Config config()
	{
		return instance.mConfig;
	}
	
	public HuntData getHuntData(Player player)
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
		player.setMetadata("MH:enabled", new FixedMetadataValue(instance, enabled));
	}
	
	private boolean isSword(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.GOLD_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.WOOD_SWORD);
	}
	
	private boolean isPick(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.GOLD_PICKAXE || item.getType() == Material.IRON_PICKAXE || item.getType() == Material.STONE_PICKAXE || item.getType() == Material.WOOD_PICKAXE);
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
	private void onSkeletonShoot(ProjectileLaunchEvent event)
	{
		if(!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Skeleton) || !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		Skeleton shooter = (Skeleton)event.getEntity().getShooter();
		
		if(shooter.getTarget() instanceof Player && isHuntEnabled((Player)shooter.getTarget()) && ((Player)shooter.getTarget()).getGameMode() != GameMode.CREATIVE)
		{
			DamageInformation info = null;
			info = mDamageHistory.get(shooter);
			
			if(info == null)
				info = new DamageInformation();
			
			info.time = System.currentTimeMillis();
			
			info.attacker = (Player)shooter.getTarget();
			
			info.attackerPosition = shooter.getTarget().getLocation().clone();
			mDamageHistory.put(shooter, info);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onMobDamage(EntityDamageByEntityEvent event)
	{
		if(!(event.getEntity() instanceof Creature) || !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		DamageInformation info = null;
		info = mDamageHistory.get(event.getEntity());
		if(info == null)
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
		
		// Take note that a weapon has been used at all
		if(info.weapon != null && (isSword(info.weapon) || isAxe(info.weapon) || isPick(info.weapon) || projectile))
			info.usedWeapon = true;
		
		if(cause != null)
		{
			info.attacker = cause;
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
			EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();
			
			if(dmg.getDamager() instanceof Arrow && ((Arrow)dmg.getDamager()).getShooter() instanceof Skeleton)
			{
				Skeleton skele = (Skeleton)((Arrow)dmg.getDamager()).getShooter();
				
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
						DamageInformation a,b;
						a = mDamageHistory.get(event.getEntity());
						b = mDamageHistory.get(((Skeleton)event.getEntity()).getTarget());
						
						Player initiator = null;
						if(a != null)
							initiator = a.attacker;
						
						if(b != null && initiator == null)
							initiator = b.attacker;
						
						
						if(initiator != null && isHuntEnabled(initiator))
							mAchievements.awardAchievement("infighting", initiator);
					}
				}
			}
			else if(event.getEntity() instanceof Creeper && dmg.getDamager() instanceof Creeper)
			{
				Player initiator = null;
				
				if(((Creeper)event.getEntity()).getTarget() instanceof Player)
					initiator = (Player)((Creeper)event.getEntity()).getTarget();
				else
				{
					DamageInformation a,b;
					a = mDamageHistory.get(event.getEntity());
					b = mDamageHistory.get(dmg.getDamager());
					
					if(a != null)
						initiator = a.attacker;
					
					if(b != null && initiator == null)
						initiator = b.attacker;
				}
				
				if(initiator != null && isHuntEnabled(initiator))
					mAchievements.awardAchievement("creepercide", initiator);
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
		
		if(killer == null || killer.getGameMode() == GameMode.CREATIVE || !isHuntEnabled(killer))
			return;
		
		mAchievements.awardAchievement("huntbegins", killer);
		
		if(info == null)
		{
			info = new DamageInformation();
			info.time = System.currentTimeMillis();
			info.attacker = killer;
			info.attackerPosition = killer.getLocation();
			info.usedWeapon = true;
		}
		
		if(info.weapon == null)
			info.weapon = new ItemStack(Material.AIR);
		
		HuntData data = getHuntData(killer);
		int lastKillstreakLevel = data.getKillstreakLevel();
		data.killStreak++;
		
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
		
		// If its a charged creeper, give the award
		if(event.getEntity() instanceof Creeper)
		{
			if(((Creeper)event.getEntity()).isPowered())
				mAchievements.awardAchievement("electrifying", killer);
		}
		
		if(info.weapon.getType() == Material.POTION)
			mAchievements.awardAchievement("itsmagic", killer);
		
		if(info.weapon.getType() == Material.DIAMOND_SWORD && !info.weapon.getEnchantments().isEmpty() && 
		   killer.getInventory().getHelmet() != null && killer.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET && !killer.getInventory().getHelmet().getEnchantments().isEmpty() &&
		   killer.getInventory().getChestplate() != null && killer.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE && !killer.getInventory().getChestplate().getEnchantments().isEmpty() &&
		   killer.getInventory().getLeggings() != null && killer.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS && !killer.getInventory().getLeggings().getEnchantments().isEmpty() &&
		   killer.getInventory().getBoots() != null && killer.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS && !killer.getInventory().getBoots().getEnchantments().isEmpty())
		{
			mAchievements.awardAchievement("fancypants", killer);
		}
		
		// Record kills that are still within a small area
		Location loc = event.getEntity().getLocation();
		
		Area detectedGrindingArea = getGrindingArea(loc);
		
		if(detectedGrindingArea == null)
			detectedGrindingArea = data.getGrindingArea(loc);
		
		
		// Slimes are except from grinding due to their splitting nature
		if(!(event.getEntity() instanceof Slime) && mConfig.penaltyGrindingEnable)
		{
			if(detectedGrindingArea != null)
			{
				data.lastKillAreaCenter = null;
				data.dampenedKills = detectedGrindingArea.count++;
				
				if(data.dampenedKills == 20)
					registerKnownGrindingSpot(detectedGrindingArea);
			}
			else
			{
				if(data.lastKillAreaCenter != null)
				{
					if(loc.getWorld().equals(data.lastKillAreaCenter.getWorld()))
					{
						if(loc.distance(data.lastKillAreaCenter) < cDampnerRange)
						{
							data.dampenedKills++;
							if(data.dampenedKills == 10)
								data.recordGrindingArea();
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
				}
				else
				{
					data.lastKillAreaCenter = loc.clone();
					data.dampenedKills = 0;
				}
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
		
		
		// Apply the modifiers
		ArrayList<String> modifiers = new ArrayList<String>();
		for(IModifier mod : mModifiers)
		{
			if(mod.doesApply(event.getEntity(), killer, data, info))
			{
				double amt = mod.getMultiplier(event.getEntity(), killer, data, info);
				
				if(amt != 1.0)
				{
					modifiers.add(mod.getName());
					multiplier *= amt;
				}
			}
		}
		
		// This achievement only cares about the death blow
		if(info.weapon.getType() == Material.BOOK || info.weapon.getType() == Material.WRITTEN_BOOK || info.weapon.getType() == Material.BOOK_AND_QUILL)
			mAchievements.awardAchievement("bythebook", killer);
		
		if(isAxe(info.weapon))
			mAchievements.awardAchievement("axemurderer", killer);
		
		multiplier *= data.getKillstreakMultiplier();
		
		String extraString = "";
		
		// Only display the multiplier if its not 1
		if(Math.abs(multiplier - 1) > 0.05)
			extraString += String.format("x%.1f", multiplier);
		
		// Add on modifiers
		for(String modifier : modifiers)
			extraString += ChatColor.WHITE + " * " + modifier;
		
		cash *= multiplier;
		
		if(!extraString.trim().isEmpty())
			extraString = "With: " + extraString.trim();
		
		if(cash >= 0.01)
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
		if(mob instanceof PigZombie)
			return mConfig.pigMan;
		
		return 0;
	}
	
	public AchievementManager getAchievements()
	{
		return mAchievements;
	}
}
