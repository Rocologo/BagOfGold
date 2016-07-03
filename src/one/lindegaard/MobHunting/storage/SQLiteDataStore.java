package one.lindegaard.MobHunting.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.util.UUIDHelper;

public class SQLiteDataStore extends DatabaseDataStore {

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws SQLException, DataStoreException {
		try {
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:" + MobHunting.getInstance().getDataFolder().getPath() + "/"
					+ MobHunting.getConfigManager().databaseName + ".db");
		} catch (ClassNotFoundException e) {
			throw new DataStoreException("SQLite not present on the classpath");
		}
	}

	@Override
	protected void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException {
		switch (preparedConnectionType) {
		case GET1PLAYER:
			mGetPlayerData[0] = connection.prepareStatement("SELECT * FROM mh_Players WHERE UUID=?;");
			break;
		case GET2PLAYERS:
			mGetPlayerData[1] = connection.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?);");
			break;
		case GET5PLAYERS:
			mGetPlayerData[2] = connection.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?,?,?,?);");
			break;
		case GET10PLAYERS:
			mGetPlayerData[3] = connection
					.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?,?,?,?,?,?,?,?,?);");
			break;
		case SAVE_ACHIEVEMENTS:
			mSaveAchievement = connection.prepareStatement("INSERT OR REPLACE INTO mh_Achievements VALUES(?,?,?,?);");
			break;
		case SAVE_PLAYER_STATS:
			mSavePlayerStats = connection.prepareStatement(
					"INSERT OR IGNORE INTO mh_Daily(ID, PLAYER_ID) VALUES(strftime(\"%Y%j\",\"now\"),?);");
			break;
		case LOAD_ARCHIEVEMENTS:
			mLoadAchievements = connection
					.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM mh_Achievements WHERE PLAYER_ID = ?;");
			break;
		case GET_PLAYER_UUID:
			mGetPlayerUUID = connection.prepareStatement("SELECT UUID FROM mh_Players WHERE NAME=?;");
			break;
		case UPDATE_PLAYER_NAME:
			mUpdatePlayerName = connection.prepareStatement("UPDATE mh_Players SET NAME=? WHERE UUID=?;");
			break;
		case INSERT_PLAYER_DATA:
			mInsertPlayerData = connection
					.prepareStatement("INSERT INTO mh_Players (UUID,NAME,PLAYER_ID,LEARNING_MODE,MUTE_MODE) "
							+ "VALUES(?,?,(SELECT IFNULL(MAX(PLAYER_ID),0)+1 FROM mh_Players),?,?);");
			break;
		case UPDATE_PLAYER_SETTINGS:
			mUpdatePlayerSettings = connection
					.prepareStatement("UPDATE mh_Players SET LEARNING_MODE=?,MUTE_MODE=? WHERE UUID=?;");
			break;
		case GET_BOUNTIES:
			mGetBounties = connection.prepareStatement(
					"SELECT * FROM mh_Bounties where STATUS=0 AND (BOUNTYOWNER_ID=? OR WANTEDPLAYER_ID=? OR NOT NPC_ID=0);");
			break;
		case INSERT_BOUNTY:
			mInsertBounty = connection.prepareStatement("REPLACE INTO mh_Bounties "
					+ "(MOBTYPE, BOUNTYOWNER_ID, WANTEDPLAYER_ID, NPC_ID, MOB_ID, WORLDGROUP, "
					+ "CREATED_DATE, END_DATE, PRIZE, MESSAGE, STATUS) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?);");
			break;
		case UPDATE_BOUNTY:
			mUpdateBounty = connection.prepareStatement("UPDATE mh_Bounties SET PRIZE=?,MESSAGE=?,END_DATE=?,STATUS=?"
					+ " WHERE WANTEDPLAYER_ID=? AND BOUNTYOWNER_ID=? AND WORLDGROUP=?;");
			break;
		case GET_PLAYER_BY_PLAYER_ID:
			mGetPlayerByPlayerId = connection.prepareStatement("SELECT UUID FROM mh_Players WHERE PLAYER_ID=?;");
		case DELETE_BOUNTY:
			mDeleteBounty = connection.prepareStatement(
					"DELETE FROM mh_Bounties WHERE WANTEDPLAYER_ID=? AND BOUNTYOWNER_ID=? AND WORLDGROUP=?;");
		}
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}
	}

	// *******************************************************************************
	@SuppressWarnings("deprecation")
	// LoadStats / SaveStats
	// *******************************************************************************
	@Override
	public List<StatStore> loadPlayerStats(StatType type, TimePeriod period, int count) throws DataStoreException {
		ArrayList<StatStore> list = new ArrayList<StatStore>();
		String id;
		// If The NPC has an invalid period or timeperiod return and empty list
		if (period == null || type == null)
			return list;
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
			if (MobHunting.getConfigManager().debugSQL) {
				connections++;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
			}
			ResultSet results = statement
					.executeQuery("SELECT " + type.getDBColumn() + ", mh_Players.UUID, mh_Players.NAME from mh_"
							+ period.getTable() + " inner join mh_Players using (PLAYER_ID)"
							+ (id != null ? " where mh_Players.NAME!='' and ID=" + id : "") + " order by "
							+ type.getDBColumn() + " desc limit " + count);
			while (results.next()) {
				OfflinePlayer offlinePlayer;
				try {
					offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(results.getString(2)));
					if (offlinePlayer == null)
						MobHunting.debug("getOfflinePlayer(%s) was not in cache.", results.getString(3));
					else
						list.add(new StatStore(type, offlinePlayer, results.getInt(1)));
				} catch (Exception e) {
					MobHunting.debug("getOfflinePlayer(%s) was not in cache.", results.getString(3));
					//e.printStackTrace();
				}
				
			}
			results.close();
			statement.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
			}

			return list;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	@Override
	public void savePlayerStats(Set<StatStore> stats) throws DataStoreException {
		try {
			MobHunting.debug("Saving PlayerStats to Database.");

			// Make sure the stats are available for each player
			openPreparedStatements(mConnection, PreparedConnectionType.SAVE_PLAYER_STATS);
			mSavePlayerStats.clearBatch();
			for (StatStore st : stats) {
				mSavePlayerStats.setInt(1, getPlayerId(st.getPlayer()));
				mSavePlayerStats.addBatch();
			}
			mSavePlayerStats.executeBatch();
			mSavePlayerStats.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
			}

			// Now add each of the stats
			Statement statement = mConnection.createStatement();
			if (MobHunting.getConfigManager().debugSQL) {
				connections++;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore(xxx): Open - connections=%s", connections);
			}
			for (StatStore stat : stats)
				statement.addBatch(String.format(
						"UPDATE mh_Daily SET %1$s = %1$s + %3$d WHERE ID = strftime(\"%%Y%%j\",\"now\") AND PLAYER_ID = %2$d;",
						stat.getType().getDBColumn(), getPlayerId(stat.getPlayer()), stat.getAmount()));
			statement.executeBatch();
			statement.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore(xxx): close - connections=%s", connections);
			}

			mConnection.commit();
			MobHunting.debug("Saved.");
		} catch (

		SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	// *******************************************************************************
	// DATABASE SETUP / MIGRATION
	// *******************************************************************************

	@Override
	protected void setupTables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}
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
		String lm = MobHunting.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players (UUID TEXT PRIMARY KEY, NAME TEXT, "
				+ "PLAYER_ID INTEGER NOT NULL, LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm
				+ ", MUTE_MODE INTEGER NOT NULL DEFAULT 0 )");
		String dataString = "";
		for (StatType type : StatType.values())
			dataString += ", " + type.getDBColumn() + " INTEGER NOT NULL DEFAULT 0";
		create.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Daily (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
						+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
						+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
						+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Yearly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
						+ dataString + ", PRIMARY KEY(PLAYER_ID, ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_AllTime (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)"
				+ dataString + ", PRIMARY KEY(PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Achievements "
				+ "(PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) NOT NULL, ACHIEVEMENT TEXT NOT NULL, "
				+ "DATE INTEGER NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), "
				+ "FOREIGN KEY(PLAYER_ID) REFERENCES mh_Players(PLAYER_ID))");
		if (!MobHunting.getConfigManager().disablePlayerBounties)
			create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Bounties ("
					+ "BOUNTYOWNER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) NOT NULL, " + "MOBTYPE TEXT, "
					+ "WANTEDPLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID), " + "NPC_ID INTEGER, "
					+ "MOB_ID TEXT, " + "WORLDGROUP TEXT NOT NULL, " + "CREATED_DATE INTEGER NOT NULL, "
					+ "END_DATE INTEGER NOT NULL, " + "PRIZE FLOAT NOT NULL, " + "MESSAGE TEXT, "
					+ "STATUS INTEGER NOT NULL DEFAULT 0, "
					+ "PRIMARY KEY(WORLDGROUP, WANTEDPLAYER_ID, BOUNTYOWNER_ID), "
					+ "FOREIGN KEY(BOUNTYOWNER_ID) REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE, "
					+ "FOREIGN KEY(WANTEDPLAYER_ID) REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE" + ")");

		setupTrigger(connection);

		create.close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections--;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Close - connections=%s", connections);
		}
		connection.commit();

		performUUIDMigrate(connection);
		performAddNewMobs(connection);
	}

	private void setupTrigger(Connection connection) throws SQLException {

		Statement create = connection.createStatement();
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}

		create.executeUpdate(
				"create trigger if not exists mh_DailyInsert after insert on mh_Daily begin insert or ignore into mh_Weekly(ID, PLAYER_ID) values(strftime(\"%Y%W\",\"now\"), NEW.PLAYER_ID); insert or ignore into mh_Monthly(ID, PLAYER_ID) values(strftime(\"%Y%m\",\"now\"), NEW.PLAYER_ID); insert or ignore into mh_Yearly(ID, PLAYER_ID) values(strftime(\"%Y\",\"now\"), NEW.PLAYER_ID); insert or ignore into mh_AllTime(PLAYER_ID) values(NEW.PLAYER_ID); end");

		// Create the cascade update trigger. It will allow us to only modify
		// the Daily table, and the rest will happen automatically
		StringBuilder updateStringBuilder = new StringBuilder();

		for (StatType type : StatType.values()) {
			if (updateStringBuilder.length() != 0)
				updateStringBuilder.append(", ");

			updateStringBuilder.append(String.format("%s = (%1$s + (NEW.%1$s - OLD.%1$s)) ", type.getDBColumn()));
		}

		String updateString = updateStringBuilder.toString();

		StringBuilder updateTrigger = new StringBuilder();
		updateTrigger.append("create trigger if not exists mh_DailyUpdate after update on mh_Daily begin ");

		// Weekly
		updateTrigger.append(" update mh_Weekly set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y%W','now') AND PLAYER_ID=New.PLAYER_ID;");

		// Monthly
		updateTrigger.append(" update mh_Monthly set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y%m','now') AND PLAYER_ID=New.PLAYER_ID;");

		// Yearly
		updateTrigger.append(" update mh_Yearly set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y','now') AND PLAYER_ID=New.PLAYER_ID;");

		// AllTime
		updateTrigger.append(" update mh_AllTime set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where PLAYER_ID=New.PLAYER_ID;");

		updateTrigger.append("END");

		create.executeUpdate(updateTrigger.toString());
		create.close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections--;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Close - connections=%s", connections);
		}

		connection.commit();
	}

	private void performTableMigrate(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}
		try {
			ResultSet rs = statement.executeQuery("SELECT UUID from mh_Players LIMIT 0");
			rs.close();
			statement.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
			}

			return; // Tables will be fine
		} catch (SQLException e) {
		}

		statement.executeUpdate("ALTER TABLE mh_Players RENAME TO mh_PlayersOLD");
		statement.executeUpdate("ALTER TABLE mh_Achievements RENAME TO mh_AchievementsOLD");
		statement.executeUpdate("ALTER TABLE mh_Daily RENAME TO mh_DailyOLD");
		statement.executeUpdate("ALTER TABLE mh_Weekly RENAME TO mh_WeeklyOLD");
		statement.executeUpdate("ALTER TABLE mh_Monthly RENAME TO mh_MonthlyOLD");
		statement.executeUpdate("ALTER TABLE mh_Yearly RENAME TO mh_YearlyOLD");
		statement.executeUpdate("ALTER TABLE mh_AllTime RENAME TO mh_AllTimeOLD");

		// Create new empty tables if they do not exist
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Players (UUID TEXT PRIMARY KEY, NAME TEXT, PLAYER_ID INTEGER NOT NULL,"
						+ "LEARNIN_MODE INTEGER NOT NULL, MUTE_MODE INTEGER NOT NULL )");
		String dataString = "";
		for (StatType type : StatType.values())
			dataString += ", " + type.getDBColumn() + " INTEGER NOT NULL DEFAULT 0";
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Daily (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" //$NON-NLS-1$
						+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" //$NON-NLS-1$
						+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" //$NON-NLS-1$
						+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Yearly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" //$NON-NLS-1$
						+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_AllTime (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID)" + dataString //$NON-NLS-1$
						+ ", PRIMARY KEY(PLAYER_ID))"); //$NON-NLS-1$
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS mh_Achievements (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) NOT NULL, ACHIEVEMENT TEXT NOT NULL, DATE INTEGER NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), FOREIGN KEY(PLAYER_ID) REFERENCES mh_Players(PLAYER_ID))"); //$NON-NLS-1$

		statement.executeUpdate("INSERT INTO mh_Players SELECT * FROM mh_PlayersOLD");
		statement.executeUpdate("INSERT INTO mh_Achievements SELECT * FROM mh_AchievementsOLD");
		statement.executeUpdate("INSERT INTO mh_Daily SELECT * FROM mh_DailyOLD");
		statement.executeUpdate("INSERT INTO mh_Weekly SELECT * FROM mh_WeeklyOLD");
		statement.executeUpdate("INSERT INTO mh_Monthly SELECT * FROM mh_MonthlyOLD");
		statement.executeUpdate("INSERT INTO mh_Yearly SELECT * FROM mh_YearlyOLD");
		statement.executeUpdate("INSERT INTO mh_AllTime SELECT * FROM mh_AllTimeOLD");

		statement.executeUpdate("DROP TABLE mh_Players");
		statement.executeUpdate("DROP TABLE mh_AchievementsOLD");
		statement.executeUpdate("DROP TABLE mh_DailyOLD");
		statement.executeUpdate("DROP TABLE mh_WeeklyOLD");
		statement.executeUpdate("DROP TABLE mh_MonthlyOLD");
		statement.executeUpdate("DROP TABLE mh_YearlyOLD");
		statement.executeUpdate("DROP TABLE mh_AllTimeOLD");
		statement.close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections--;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
		}
	}

	private void performUUIDMigrate(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}
		try {
			ResultSet rs = statement.executeQuery("SELECT UUID from mh_Players LIMIT 0");
			rs.close();
			statement.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= 10)
					MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
			}

			return; // UUIDs are in place
		} catch (SQLException e) {
			performTableMigrate(connection);
		}

		System.out.println("[MobHunting] Migrating MobHunting Database User ID to User UUID.");

		// Add missing columns
		performTableMigrate(connection);

		// Get UUID and update table
		ResultSet rs = statement.executeQuery("select `NAME`,`PLAYER_ID` from `mh_Players`");
		UUIDHelper.initialize();

		PreparedStatement insert = connection.prepareStatement("INSERT INTO mh_Players VALUES(?,?,?)");
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}
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
			System.err.println("[MobHunting] " + failCount + " accounts failed to convert:");
			System.err.println("[MobHunting] " + failString.toString());
		}

		insert.executeBatch();
		insert.close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections--;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Close - connections=%s", connections);
		}

		System.out.println("[MobHunting] Player UUID migration complete.");

		statement.close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections--;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
		}
		connection.commit();
	}

	private void performAddNewMobs(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: Open - connections=%s", connections);
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT Bat_kill from `mh_Daily` LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding Passive Mobs to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Bat_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Bat_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Chicken_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Chicken_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Cow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Cow_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Horse_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Horse_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Daily` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Weekly` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Yearly` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `MushroomCow_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `MushroomCow_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Ocelot_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Ocelot_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Pig_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Pig_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate(
					"alter table `mh_Daily` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Daily` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Weekly` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Weekly` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Yearly` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Yearly` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `PassiveRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `PassiveRabbit_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Sheep_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Sheep_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Snowman_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Snowman_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Squid_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Squid_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Villager_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Villager_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Wolf_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Wolf_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding passive mobs complete.");

		}

		try {
			ResultSet rs = statement.executeQuery("SELECT EnderDragon_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding EnderDragon to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Daily` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Weekly` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Yearly` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `EnderDragon_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `EnderDragon_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding EnderDragon complete.");
		}
		try {
			ResultSet rs = statement.executeQuery("SELECT IronGolem_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding IronGolem to MobHunting Database ");

			statement.executeUpdate("alter table `mh_Daily` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `IronGolem_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `IronGolem_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding IronGolem complete.");
		}
		try {
			ResultSet rs = statement.executeQuery("SELECT PvpPlayer_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding new PvpPlayer to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `PvpPlayer_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `PvpPlayer_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding new PvpPlayer complete.");
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT Giant_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding new Mobs to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Endermite_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `Endermite_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Giant_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Giant_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Monthly` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_AllTime` add column `Guardian_assist`  INTEGER NOT NULL DEFAULT 0");

			statement
					.executeUpdate("alter table `mh_Daily` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Daily` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Weekly` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Weekly` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Yearly` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Yearly` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `KillerRabbit_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `KillerRabbit_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding new Mobs complete.");
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT Shulker_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding new 1.9 Mobs (Shulker) to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `Shulker_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Shulker_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Shulker_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Shulker_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Shulker_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Shulker_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Shulker_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Shulker_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Shulker_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Shulker_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding new 1.9 Mobs (Shulker) complete.");
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT PolarBear_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding new 1.10 Mobs (Polar Bear) to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `PolarBear_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `PolarBear_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `PolarBear_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Weekly` add column `PolarBear_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `PolarBear_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_Monthly` add column `PolarBear_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `PolarBear_kill`  INTEGER NOT NULL DEFAULT 0");
			statement
					.executeUpdate("alter table `mh_Yearly` add column `PolarBear_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `PolarBear_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate(
					"alter table `mh_AllTime` add column `PolarBear_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding new 1.10 Mobs (Polar Bear) complete.");
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT Stray_kill from mh_Daily LIMIT 0");
			rs.close();
		} catch (SQLException e) {

			System.out.println("[MobHunting] Adding new 1.10 Mobs (Stray + Husk) to MobHunting Database.");

			statement.executeUpdate("alter table `mh_Daily` add column `Stray_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Stray_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Stray_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Stray_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Stray_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Stray_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Stray_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Stray_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Stray_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Stray_assist`  INTEGER NOT NULL DEFAULT 0");

			statement.executeUpdate("alter table `mh_Daily` add column `Husk_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Daily` add column `Husk_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Husk_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Weekly` add column `Husk_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Husk_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Monthly` add column `Husk_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Husk_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_Yearly` add column `Husk_assist`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Husk_kill`  INTEGER NOT NULL DEFAULT 0");
			statement.executeUpdate("alter table `mh_AllTime` add column `Husk_assist`  INTEGER NOT NULL DEFAULT 0");

			System.out.println("[MobHunting] Adding new 1.10 Mobs (Stray + Husk) complete.");
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT LEARNING_MODE from mh_Players LIMIT 0");
			rs.close();
		} catch (SQLException e) {
			System.out.println("[MobHunting] Adding new Player leaning mode to MobHunting Database.");
			String lm = MobHunting.getConfigManager().learningMode ? "1" : "0";
			statement.executeUpdate(
					"alter table `mh_Players` add column `LEARNING_MODE` INTEGER NOT NULL DEFAULT " + lm);
		}

		try {
			ResultSet rs = statement.executeQuery("SELECT MUTE_MODE from mh_Players LIMIT 0");
			rs.close();
		} catch (SQLException e) {
			System.out.println("[MobHunting] Adding new Player mute mode to MobHunting Database.");
			statement.executeUpdate("alter table `mh_Players` add column `MUTE_MODE` INTEGER NOT NULL DEFAULT 0");
		}

		MobHunting.debug("Updating database triggers.");
		statement.executeUpdate("DROP TRIGGER IF EXISTS `mh_DailyInsert`");
		statement.executeUpdate("DROP TRIGGER IF EXISTS `mh_DailyUpdate`");
		setupTrigger(connection);

		statement.close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections--;
			if (connections >= 10)
				MobHunting.debug("SQLiteDataStore: close - connections=%s", connections);
		}

		connection.commit();
	}

}
