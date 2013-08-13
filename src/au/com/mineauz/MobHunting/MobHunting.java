package au.com.mineauz.MobHunting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import au.com.mineauz.MobHunting.achievements.*;
import au.com.mineauz.MobHunting.commands.CheckGrindingCommand;
import au.com.mineauz.MobHunting.commands.CommandDispatcher;
import au.com.mineauz.MobHunting.commands.ListAchievementsCommand;
import au.com.mineauz.MobHunting.commands.ReloadCommand;
import au.com.mineauz.MobHunting.compatability.MinigamesCompat;
import au.com.mineauz.MobHunting.modifier.*;
import au.com.mineauz.MobHunting.util.Misc;

public class MobHunting extends JavaPlugin implements Listener
{
	private Economy mEconomy;
	public static MobHunting instance;
	
	private WeakHashMap<LivingEntity, DamageInformation> mDamageHistory = new WeakHashMap<LivingEntity, DamageInformation>();
	private Config mConfig;
	
	private AchievementManager mAchievements;
	public static double cDampnerRange = 15;
	
	private Set<IModifier> mModifiers;
	
	private ArrayList<Area> mKnownGrindingSpots = new ArrayList<Area>();
	
	private ParticleManager mParticles = new ParticleManager();
	private Random mRand = new Random();

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
			getLogger().severe("Unable to hook into an economy! Make sure you have one available that Vault accepts.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		mEconomy = economyProvider.getProvider();
		
		// Move the old data folder
		File oldData = new File(getDataFolder().getParentFile(), "Mob Hunting");
		if(oldData.exists())
		{
			try
			{
				Files.move(oldData.toPath(), getDataFolder().toPath(), StandardCopyOption.ATOMIC_MOVE);
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		mConfig = new Config(new File(getDataFolder(), "config.yml"));
		
		if(mConfig.load())
			mConfig.save();
		else
			throw new RuntimeException("There was a problem loading the MobHunting config");
		
		// Handle compatability stuff
		if(Bukkit.getPluginManager().isPluginEnabled("Minigames"))
			mMinigames = new MinigamesCompat();
		
		CommandDispatcher cmd = new CommandDispatcher("mobhunt", "Mob Hunting Version " + getDescription().getVersion());
		getCommand("mobhunt").setExecutor(cmd);
		getCommand("mobhunt").setTabCompleter(cmd);
		
		cmd.registerCommand(new ReloadCommand());
		cmd.registerCommand(new ListAchievementsCommand());
		cmd.registerCommand(new CheckGrindingCommand());
		
		registerAchievements();
		registerModifiers();
		
		getServer().getPluginManager().registerEvents(this, this);
		
		for(Player player : Bukkit.getOnlinePlayers())
			mAchievements.load(player);
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
		mAchievements.registerAchievement(new MasterSniper());
		mAchievements.registerAchievement(new WolfKillAchievement());
		
		for(ExtendedMobType type : ExtendedMobType.values())
		{
			mAchievements.registerAchievement(new BasicHuntAchievement(type));
			mAchievements.registerAchievement(new SecondHuntAchievement(type));
			mAchievements.registerAchievement(new ThirdHuntAchievement(type));
			mAchievements.registerAchievement(new FourthHuntAchievement(type));
		}
		
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
		mModifiers.add(new FriendleFireBonus());
		mModifiers.add(new BonusMobBonus());
		
		mModifiers.add(new FlyingPenalty());
		mModifiers.add(new GrindingPenalty());
		
		// Check if horses exist
		try
		{
			Class.forName("org.bukkit.entity.Horse");
			mModifiers.add(new MountedBonus());
		}
		catch(ClassNotFoundException e) {}
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
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerDeath(PlayerDeathEvent event)
	{
		if(!isHuntEnabledInWorld(event.getEntity().getWorld()) || !isHuntEnabled(event.getEntity()))
			return;
		
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
		
		if(!isHuntEnabledInWorld(event.getEntity().getWorld()) || !isHuntEnabled((Player)event.getEntity()))
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
		if(!(event.getEntity() instanceof LivingEntity) || !isHuntEnabledInWorld(event.getEntity().getWorld()))
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
		
		if(event.getDamager() instanceof Wolf && ((Wolf)event.getDamager()).isTamed())
		{
			if(cause == null)
				cause = Bukkit.getPlayerExact(((Wolf)event.getDamager()).getOwner().getName());

			info.mele = false;
			info.wolfAssist = true;
		}
		
		if(weapon == null && cause != null)
			weapon = cause.getItemInHand();
		
		if(weapon != null)
			info.weapon = weapon;
		
		// Take note that a weapon has been used at all
		if(info.weapon != null && (isSword(info.weapon) || Misc.isAxe(info.weapon) || isPick(info.weapon) || projectile))
			info.usedWeapon = true;
		
		if(cause != null)
		{
			info.attacker = cause;
			if(cause.isFlying() && !cause.isInsideVehicle())
				info.wasFlying = true;
			
			info.attackerPosition = cause.getLocation().clone();
			mDamageHistory.put((LivingEntity)event.getEntity(), info);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onMobDeath(EntityDeathEvent event)
	{
		if(event.getEntity() instanceof Player || getBaseKillPrize(event.getEntity()) == 0 || !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		if(event.getEntity().hasMetadata("MH:blocked"))
			return;
		
		Player killer = event.getEntity().getKiller();
		
		DamageInformation info = null;
		if(event.getEntity() instanceof LivingEntity && mDamageHistory.containsKey((LivingEntity)event.getEntity()))
		{
			info = mDamageHistory.get(event.getEntity());
			
			if(System.currentTimeMillis() - info.time > 4000)
				info = null;
			else if(killer == null)
				killer = info.attacker;
		}
		
		EntityDamageByEntityEvent lastDamageCause = null;
		
		if(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
			lastDamageCause = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();
		
		if(killer == null || killer.getGameMode() == GameMode.CREATIVE || !isHuntEnabled(killer))
			return;
		
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
			if(mod.doesApply(event.getEntity(), killer, data, info, lastDamageCause))
			{
				double amt = mod.getMultiplier(event.getEntity(), killer, data, info, lastDamageCause);
				
				if(amt != 1.0)
				{
					modifiers.add(mod.getName());
					multiplier *= amt;
				}
			}
		}
		
		
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
			Bukkit.getPluginManager().callEvent(new MobHuntKillEvent(data, info, event.getEntity(), killer));
			mEconomy.depositPlayer(killer.getName(), cash);
			killer.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + String.format("You gained $%.2f! %s", cash, extraString));
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerJoin(PlayerJoinEvent event)
	{
		setHuntEnabled(event.getPlayer(), true);
	}
	
	public DamageInformation getDamageInformation(LivingEntity entity)
	{
		return mDamageHistory.get(entity);
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
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void bonusMobSpawn(CreatureSpawnEvent event)
	{
		if(!isHuntEnabledInWorld(event.getLocation().getWorld()) || getBaseKillPrize(event.getEntity()) <= 0 || event.getSpawnReason() != SpawnReason.NATURAL)
			return;
		
		if(event.getEntityType() == EntityType.ENDER_DRAGON)
			return;
		
		if(mRand.nextDouble() * 100 < mConfig.bonusMobChance)
		{
			mParticles.attachEffect(event.getEntity(), Effect.MOBSPAWNER_FLAMES);
			if(mRand.nextBoolean())
				event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 3));
			else
				event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
			
			event.getEntity().setMetadata("MH:hasBonus", new FixedMetadataValue(this, true));
		}
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void spawnerMobSpawn(CreatureSpawnEvent event)
	{
		if(!isHuntEnabledInWorld(event.getLocation().getWorld()) || getBaseKillPrize(event.getEntity()) <= 0)
			return;
		
		if(event.getSpawnReason() != SpawnReason.SPAWNER && event.getSpawnReason() != SpawnReason.SPAWNER_EGG)
			return;
		
		event.getEntity().setMetadata("MH:blocked", new FixedMetadataValue(this, true));
	}
	
	public AchievementManager getAchievements()
	{
		return mAchievements;
	}
}
