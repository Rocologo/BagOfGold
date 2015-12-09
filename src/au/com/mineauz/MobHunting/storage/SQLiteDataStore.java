package au.com.mineauz.MobHunting.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.util.UUIDHelper;

public class SQLiteDataStore extends DatabaseDataStore {
	@Override
	protected Connection setupConnection() throws SQLException,
			DataStoreException {
		try {
			Class.forName("org.sqlite.JDBC"); 
			return DriverManager
					.getConnection("jdbc:sqlite:" + MobHunting.instance.getDataFolder().getPath() + "/" + MobHunting.config().databaseName + ".db"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (ClassNotFoundException e) {
			throw new DataStoreException("SQLite not present on the classpath"); 
		}
	}

	@Override
	protected void setupTables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();
		// Prefix tables to mh_
		try {
			ResultSet rs = create.executeQuery("SELECT * from Players LIMIT 0");
			rs.close();
			create.executeUpdate("ALTER TABLE Players RENAME TO mh_Players");
			create.executeUpdate("ALTER TABLE Achievements RENAME TO mh_Achievements");
			create.executeUpdate("ALTER TABLE Daily RENAME TO mh_Daily");
			create.executeUpdate("ALTER TABLE Weekly RENAME TO mh_Weekly");
			create.executeUpdate("ALTER TABLE Monthly RENAME TO mh_Monthly");
			create.executeUpdate("ALTER TABLE Yearly RENAME TO mh_Yearly");
			create.executeUpdate("ALTER TABLE AllTime RENAME TO mh_AllTime");

			create.executeUpdate("DROP TRIGGER IF EXISTS DailyInsert");
			create.executeUpdate("DROP TRIGGER IF EXISTS DailyUpdate");

		} catch (SQLException e) {
		}

		// Create new empty tables if they do not exist
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players (UUID TEXT PRIMARY KEY, NAME TEXT, PLAYER_ID INTEGER NOT NULL)"); //$NON-NLS-1$
		String dataString = "";
		for (StatType type : StatType.values())
			dataString += ", " + type.getDBColumn()
					+ " INTEGER NOT NULL DEFAULT 0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Daily (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
				+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
				+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
				+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Yearly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
				+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_AllTime (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
				+ dataString + ", PRIMARY KEY(PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Achievements (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) NOT NULL, ACHIEVEMENT TEXT NOT NULL, DATE INTEGER NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), FOREIGN KEY(PLAYER_ID) REFERENCES mh_Players(PLAYER_ID))"); //$NON-NLS-1$

		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_MobTypes (MOB_ID INTEGER PRIMARY KEY, MOB_NAME TEXT NOT NULL)");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Kills ("
				+ " PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID),"
				+ " MOB_ID INTEGER REFERENCES mh_MobTypes(MOB_ID),"
				+ " PERIOD TEXT NOT NULL,"
				// PERIOD={D,W,M,Y,A} (Day, Week, Month, Year, All Time)
				+ " ID CHAR(6) NOT NULL,"
				+ " KILLS INTEGER NOT NULL DEFAULT 0,"
				+ " ASSISTS INTEGER NOT NULL DEFAULT 0,"
				+ " PRIMARY KEY(PLAYER_ID, MOB_ID, PERIOD))");

		setupTrigger(connection);

		create.close();
		connection.commit();

		performUUIDMigrate(connection);
		performAddNewMobs(connection);
	}

	private void setupTrigger(Connection connection) throws SQLException {

		Statement create = connection.createStatement();

		create.executeUpdate("create trigger if not exists mh_DailyInsert after insert on mh_Daily begin insert or ignore into mh_Weekly(ID, PLAYER_ID) values(strftime(\"%Y%W\",\"now\"), NEW.PLAYER_ID); insert or ignore into mh_Monthly(ID, PLAYER_ID) values(strftime(\"%Y%m\",\"now\"), NEW.PLAYER_ID); insert or ignore into mh_Yearly(ID, PLAYER_ID) values(strftime(\"%Y\",\"now\"), NEW.PLAYER_ID); insert or ignore into mh_AllTime(PLAYER_ID) values(NEW.PLAYER_ID); end");

		// Create the cascade update trigger. It will allow us to only modify
		// the Daily table, and the rest will happen automatically
		StringBuilder updateStringBuilder = new StringBuilder();

		for (StatType type : StatType.values()) {
			if (updateStringBuilder.length() != 0)
				updateStringBuilder.append(", ");

			updateStringBuilder
					.append(String.format(
							"%s = (%1$s + (NEW.%1$s - OLD.%1$s)) ",
							type.getDBColumn()));
		}

		String updateString = updateStringBuilder.toString();

		StringBuilder updateTrigger = new StringBuilder();
		updateTrigger
				.append("create trigger if not exists mh_DailyUpdate after update on mh_Daily begin ");

		// Weekly
		updateTrigger.append(" update mh_Weekly set ");
		updateTrigger.append(updateString);
		updateTrigger
				.append(" where ID=strftime('%Y%W','now') AND PLAYER_ID=New.PLAYER_ID;");

		// Monthly
		updateTrigger.append(" update mh_Monthly set ");
		updateTrigger.append(updateString);
		updateTrigger
				.append(" where ID=strftime('%Y%m','now') AND PLAYER_ID=New.PLAYER_ID;");

		// Yearly
		updateTrigger.append(" update mh_Yearly set ");
		updateTrigger.append(updateString);
		updateTrigger
				.append(" where ID=strftime('%Y','now') AND PLAYER_ID=New.PLAYER_ID;");

		// AllTime
		updateTrigger.append(" update mh_AllTime set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where PLAYER_ID=New.PLAYER_ID;");

		updateTrigger.append("END");

		create.executeUpdate(updateTrigger.toString());
		create.close();

		connection.commit();
	}

	@Override
	protected void setupStatements(Connection connection) throws SQLException {
		mGetPlayerStatement[0] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID=?;");
		mGetPlayerStatement[1] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?);"); //$NON-NLS-1$
		mGetPlayerStatement[2] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?,?,?,?);"); //$NON-NLS-1$
		mGetPlayerStatement[3] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?,?,?,?,?,?,?,?,?);"); //$NON-NLS-1$

		mRecordAchievementStatement = connection
				.prepareStatement("INSERT OR REPLACE INTO mh_Achievements VALUES(?,?,?,?);"); //$NON-NLS-1$

		mAddPlayerStatsStatement = connection
				.prepareStatement("INSERT OR IGNORE INTO mh_Daily(ID, PLAYER_ID) VALUES(strftime(\"%Y%j\",\"now\"),?);"); //$NON-NLS-1$

		mLoadAchievementsStatement = connection
				.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM mh_Achievements WHERE PLAYER_ID = ?;"); //$NON-NLS-1$

		mGetPlayerUUID = connection
				.prepareStatement("SELECT UUID FROM mh_Players WHERE NAME=?;");
		mUpdatePlayerName = connection
				.prepareStatement("UPDATE mh_Players SET NAME=? WHERE UUID=?;");
	}

	@Override
	protected void setupStatement_1(Connection connection) throws SQLException {
		myAddPlayerStatement = connection
				.prepareStatement("INSERT OR IGNORE INTO mh_Players VALUES(?, ?, (SELECT IFNULL(MAX(PLAYER_ID),0)+1 FROM mh_Players));");
	}

	@Override
	public void saveStats(Set<StatStore> stats) throws DataStoreException {
		try {
			MobHunting.debug("Saving stats to Database.", "");
			Statement statement = mConnection.createStatement();

			HashSet<OfflinePlayer> names = new HashSet<OfflinePlayer>();
			for (StatStore stat : stats)
				names.add(stat.getPlayer());
			Map<UUID, Integer> ids = getPlayerIds(names);

			// Make sure the stats are available for each player
			mAddPlayerStatsStatement.clearBatch();
			for (OfflinePlayer player : names) {
				mAddPlayerStatsStatement.setInt(1,
						ids.get(player.getUniqueId()));
				mAddPlayerStatsStatement.addBatch();
			}
			mAddPlayerStatsStatement.executeBatch();

			// Now add each of the stats
			for (StatStore stat : stats)
				statement
						.addBatch(String
								.format("UPDATE mh_Daily SET %1$s = %1$s + %3$d WHERE ID = strftime(\"%%Y%%j\",\"now\") AND PLAYER_ID = %2$d;",
										stat.getType().getDBColumn(),
										ids.get(stat.getPlayer().getUniqueId()),
										stat.getAmount()));
			statement.executeBatch();
			statement.close();
			MobHunting.debug("Saved.", "");
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public List<StatStore> loadStats(StatType type, TimePeriod period, int count)
			throws DataStoreException {
		String id;
		switch (period) {
		case Day:
			id = "strftime('%Y%j','now')";
			break;
		case Week:
			id = "strftime('%Y%W','now')";
			break;
		case Month:
			id = "strftime('%Y%m','now')";
			break;
		case Year:
			id = "strftime('%Y','now')";
			break;
		default:
			id = null;
			break;
		}
		try {
			Statement statement = mConnection.createStatement();
			ResultSet results = statement.executeQuery("SELECT "
					+ type.getDBColumn() + ", mh_Players.UUID from mh_"
					+ period.getTable()
					+ " inner join mh_Players using (PLAYER_ID)"
					+ (id != null ? " where ID=" + id : "") + " order by "
					+ type.getDBColumn() + " desc limit " + count);
			ArrayList<StatStore> list = new ArrayList<StatStore>();

			while (results.next())
				list.add(new StatStore(type, Bukkit.getOfflinePlayer(UUID
						.fromString(results.getString(2))), results.getInt(1)));
			results.close();
			statement.close();
			return list;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	private void performTableMigrate(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet rs = statement
					.executeQuery("SELECT UUID from mh_Players LIMIT 0");
			rs.close();
			statement.close();
			return; // Tables will be fine
		} catch (SQLException e) {
		}

		statement
				.executeUpdate("ALTER TABLE mh_Players RENAME TO mh_PlayersOLD");
		statement
				.executeUpdate("ALTER TABLE mh_Achievements RENAME TO mh_AchievementsOLD");
		statement.executeUpdate("ALTER TABLE mh_Daily RENAME TO mh_DailyOLD");
		statement.executeUpdate("ALTER TABLE mh_Weekly RENAME TO mh_WeeklyOLD");
		statement
				.executeUpdate("ALTER TABLE mh_Monthly RENAME TO mh_MonthlyOLD");
		statement.executeUpdate("ALTER TABLE mh_Yearly RENAME TO mh_YearlyOLD");
		statement
				.executeUpdate("ALTER TABLE mh_AllTime RENAME TO mh_AllTimeOLD");

		// Create new empty tables if they do not exist
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players (UUID TEXT PRIMARY KEY, NAME TEXT, PLAYER_ID INTEGER NOT NULL)"); //$NON-NLS-1$
		String dataString = "";
		for (StatType type : StatType.values())
			dataString += ", " + type.getDBColumn()
					+ " INTEGER NOT NULL DEFAULT 0";
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Daily (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Yearly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_AllTime (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		statement
				.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Achievements (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) NOT NULL, ACHIEVEMENT TEXT NOT NULL, DATE INTEGER NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), FOREIGN KEY(PLAYER_ID) REFERENCES mh_Players(PLAYER_ID))"); //$NON-NLS-1$

		statement
				.executeUpdate("INSERT INTO mh_Players SELECT * FROM mh_PlayersOLD");
		statement
				.executeUpdate("INSERT INTO mh_Achievements SELECT * FROM mh_AchievementsOLD");
		statement
				.executeUpdate("INSERT INTO mh_Daily SELECT * FROM mh_DailyOLD");
		statement
				.executeUpdate("INSERT INTO mh_Weekly SELECT * FROM mh_WeeklyOLD");
		statement
				.executeUpdate("INSERT INTO mh_Monthly SELECT * FROM mh_MonthlyOLD");
		statement
				.executeUpdate("INSERT INTO mh_Yearly SELECT * FROM mh_YearlyOLD");
		statement
				.executeUpdate("INSERT INTO mh_AllTime SELECT * FROM mh_AllTimeOLD");

		statement.executeUpdate("DROP TABLE mh_Players");
		statement.executeUpdate("DROP TABLE mh_AchievementsOLD");
		statement.executeUpdate("DROP TABLE mh_DailyOLD");
		statement.executeUpdate("DROP TABLE mh_WeeklyOLD");
		statement.executeUpdate("DROP TABLE mh_MonthlyOLD");
		statement.executeUpdate("DROP TABLE mh_YearlyOLD");
		statement.executeUpdate("DROP TABLE mh_AllTimeOLD");
		statement.close();
	}

	private void performUUIDMigrate(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet rs = statement
					.executeQuery("SELECT UUID from mh_Players LIMIT 0");
			rs.close();
			statement.close();
			return; // UUIDs are in place
		} catch (SQLException e) {
			performTableMigrate(connection);
		}

		System.out
				.println("[MobHunting]*** Migrating MobHunting Database to UUIDs ***");

		// Add missing columns
		performTableMigrate(connection);

		// Get UUID and update table
		ResultSet rs = statement
				.executeQuery("select `NAME`,`PLAYER_ID` from `mh_Players`");
		UUIDHelper.initialize();

		PreparedStatement insert = connection
				.prepareStatement("INSERT INTO mh_Players VALUES(?,?,?)");
		StringBuilder failString = new StringBuilder();
		int failCount = 0;
		while (rs.next()) {
			String player = rs.getString(1);
			int pId = rs.getInt(2);
			UUID id = UUIDHelper.getKnown(player);
			if (id != null) {
				insert.setString(1, id.toString());
				insert.setString(2, player);
				insert.setInt(3, pId);
				insert.addBatch();
			} else {
				if (failString.length() != 0)
					failString.append(", ");
				failString.append(player);
				++failCount;
			}
		}

		rs.close();
		UUIDHelper.clearCache();

		if (failCount > 0) {
			System.err.println("* " + failCount
					+ " accounts failed to convert:");
			System.err.println("** " + failString.toString());
		}

		insert.executeBatch();
		insert.close();

		System.out
				.println("[MobHunting]*** Player UUID migration complete ***");

		statement.close();
		connection.commit();
	}

	private void performAddNewMobs(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet rs = statement
					.executeQuery("SELECT Bat_kill from `mh_Daily` LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out
					.println("[MobHunting]*** Adding Passive Mobs to MobHunting Database ***");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out
					.println("[MobHunting]*** Adding passive mobs complete ***");

		}

		try {
			ResultSet rs = statement
					.executeQuery("SELECT EnderDragon_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out
					.println("[MobHunting]*** Adding EnderDragon to MobHunting Database ***");

			statement
					.executeUpdate("alter table `mh_Daily` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out
					.println("[MobHunting]*** Adding EnderDragon complete ***");
		}
		try {
			ResultSet rs = statement
					.executeQuery("SELECT IronGolem_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out
					.println("[MobHunting]*** Adding IronGolem to MobHunting Database ***");

			statement
					.executeUpdate("alter table `mh_Daily` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting]*** Adding IronGolem complete ***");
		}
		try {
			ResultSet rs = statement
					.executeQuery("SELECT PvpPlayer_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out
					.println("[MobHunting]*** Adding new PvpPlayer to MobHunting Database ***");

			statement
					.executeUpdate("alter table `mh_Daily` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out
					.println("[MobHunting]*** Adding new PvpPlayer complete ***");
		}

		try {
			ResultSet rs = statement
					.executeQuery("SELECT Giant_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out
					.println("[MobHunting]*** Adding new Mobs to MobHunting Database ***");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Daily` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting]*** Adding new Mobs complete ***");
		}

		MobHunting.debug("Updating database triggers.");
		statement.executeUpdate("DROP TRIGGER IF EXISTS `mh_DailyInsert`");
		statement.executeUpdate("DROP TRIGGER IF EXISTS `mh_DailyUpdate`");
		setupTrigger(connection);

		statement.close();
		connection.commit();
	}
}
