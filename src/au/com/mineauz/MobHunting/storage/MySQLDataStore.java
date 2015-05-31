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

public class MySQLDataStore extends DatabaseDataStore {
	
	//int n=0;

	@Override
	public void saveStats(Set<StatStore> stats) throws DataStoreException {
		try {
			MobHunting.debug("Saving stats to Database.", "");
			Statement statement = mConnection.createStatement();
			//n++;MobHunting.debug("MySQLDD: Number of connections=%s (32)(savestats)",n);

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
								.format("UPDATE mh_Daily SET %1$s = %1$s + %3$d WHERE ID = DATE_FORMAT(NOW(), '%%Y%%j') AND PLAYER_ID = %2$d;",
										stat.getType().getDBColumn(),
										ids.get(stat.getPlayer().getUniqueId()),
										stat.getAmount()));
			statement.executeBatch();
			statement.close();
			//n--;MobHunting.debug("MySQLDD: Number of connections=%s (58)(saveStats)",n);
			mConnection.commit();
			MobHunting.debug("Saved.", "");
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	protected Connection setupConnection() throws SQLException,
			DataStoreException {
		try {
			Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
			return DriverManager.getConnection(
					"jdbc:mysql://" + MobHunting.config().databaseHost + "/"
							+ MobHunting.config().databaseName
							+ "?autoReconnect=true",
					MobHunting.config().databaseUsername,
					MobHunting.config().databasePassword);
		} catch (ClassNotFoundException e) {
			throw new DataStoreException("MySQL not present on the classpath"); //$NON-NLS-1$
		}
	}

	@Override
	protected void setupTables(Connection connection) throws SQLException {
		
		Statement create = connection.createStatement();
		//n++;MobHunting.debug("MySQLDD: Number of connections=%s (87)(setupTables)",n);
		
		// Prefix tables to mh_
		try {
			ResultSet rs = create.executeQuery("SELECT * from Daily LIMIT 0");
			rs.close();
			create.executeUpdate("RENAME TABLE Players TO mh_Players");
			create.executeUpdate("RENAME TABLE Daily TO mh_Daily");
			create.executeUpdate("RENAME TABLE Weekly TO mh_Weekly");
			create.executeUpdate("RENAME TABLE Monthly TO mh_Monthly");
			create.executeUpdate("RENAME TABLE Yearly TO mh_Yearly");
			create.executeUpdate("RENAME TABLE AllTime TO mh_AllTime");
			create.executeUpdate("RENAME TABLE Achievements TO mh_Achievements");

			create.executeUpdate("DROP TRIGGER IF EXISTS DailyInsert");
			create.executeUpdate("DROP TRIGGER IF EXISTS DailyUpdate");

		} catch (SQLException e) {
		}

		// Create new empty tables if they do not exist
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players (UUID CHAR(40) PRIMARY KEY, NAME CHAR(20), PLAYER_ID INTEGER NOT NULL AUTO_INCREMENT, KEY PLAYER_ID (PLAYER_ID))"); //$NON-NLS-1$
		String dataString = ""; //$NON-NLS-1$
		for (StatType type : StatType.values())
			dataString += ", " + type.getDBColumn() + " INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$ //$NON-NLS-2$
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Daily (ID CHAR(7) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE"
				+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE"
				+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE"
				+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Yearly (ID CHAR(4) NOT NULL, PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE"
				+ dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_AllTime (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE"
				+ dataString + ", PRIMARY KEY(PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Achievements (PLAYER_ID INTEGER REFERENCES mh_Players(PLAYER_ID) ON DELETE CASCADE, ACHIEVEMENT VARCHAR(64) NOT NULL, DATE DATETIME NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT))");

		// Setup Database triggers
		setupTrigger(connection);

		create.close();
		//n--;MobHunting.debug("MySQLDD: Number of connections=%s (128)(setupTables)",n);
		connection.commit();

		performUUIDMigrate(connection);
		performAddNewMobs(connection);
	}

	private void setupTrigger(Connection connection) throws SQLException {
		
		Statement create = connection.createStatement();
		//n++;MobHunting.debug("MySQLDD: Number of connections=%s (138)(setupTrigger)",n);
		
		// Workaround for no create trigger if not exists
		try {
			create.executeUpdate("create trigger mh_DailyInsert after insert on mh_Daily for each row begin insert ignore into mh_Weekly(ID, PLAYER_ID) values(DATE_FORMAT(NOW(), '%Y%U'), NEW.PLAYER_ID); insert ignore into mh_Monthly(ID, PLAYER_ID) values(DATE_FORMAT(NOW(), '%Y%c'), NEW.PLAYER_ID); insert ignore into mh_Yearly(ID, PLAYER_ID) values(DATE_FORMAT(NOW(), '%Y'), NEW.PLAYER_ID); insert ignore into mh_AllTime(PLAYER_ID) values(NEW.PLAYER_ID); end"); //$NON-NLS-1$

			// Create the cascade update trigger. It will allow us to only
			// modify the Daily table, and the rest will happen automatically
			StringBuilder updateStringBuilder = new StringBuilder();

			for (StatType type : StatType.values()) {
				if (updateStringBuilder.length() != 0)
					updateStringBuilder.append(", "); //$NON-NLS-1$

				updateStringBuilder
						.append(String
								.format("%s = (%1$s + (NEW.%1$s - OLD.%1$s)) ", type.getDBColumn())); //$NON-NLS-1$
			}

			String updateString = updateStringBuilder.toString();

			StringBuilder updateTrigger = new StringBuilder();
			updateTrigger
					.append("create trigger mh_DailyUpdate after update on mh_Daily for each row begin "); //$NON-NLS-1$

			// Weekly
			updateTrigger.append(" update mh_Weekly set "); //$NON-NLS-1$
			updateTrigger.append(updateString);
			updateTrigger
					.append(" where ID=DATE_FORMAT(NOW(), '%Y%U') AND PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$

			// Monthly
			updateTrigger.append(" update mh_Monthly set "); //$NON-NLS-1$
			updateTrigger.append(updateString);
			updateTrigger
					.append(" where ID=DATE_FORMAT(NOW(), '%Y%c') AND PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$

			// Yearly
			updateTrigger.append(" update mh_Yearly set "); //$NON-NLS-1$
			updateTrigger.append(updateString);
			updateTrigger
					.append(" where ID=DATE_FORMAT(NOW(), '%Y') AND PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$

			// AllTime
			updateTrigger.append(" update mh_AllTime set "); //$NON-NLS-1$
			updateTrigger.append(updateString);
			updateTrigger.append(" where PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$

			updateTrigger.append("END"); //$NON-NLS-1$

			create.executeUpdate(updateTrigger.toString());
		} catch (SQLException e) {
			// Do Nothing
		}

		create.close();
		//n--;MobHunting.debug("MySQLDD: Number of connections=%s (194)(setupTrigger)",n);
		connection.commit();
	}

	//TODO: This must be changed, so its possible to prepare and close statements once in while.
	@Override
	protected void setupStatements(Connection connection) throws SQLException {
		//mAddPlayerStatement = connection
		//		.prepareStatement("INSERT IGNORE INTO mh_Players(UUID,NAME) VALUES(?,?);");
		mGetPlayerStatement[0] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID=?;");
		mGetPlayerStatement[1] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?);");
		mGetPlayerStatement[2] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?,?,?,?);");
		mGetPlayerStatement[3] = connection
				.prepareStatement("SELECT * FROM mh_Players WHERE UUID IN (?,?,?,?,?,?,?,?,?,?);");

		mRecordAchievementStatement = connection
				.prepareStatement("REPLACE INTO mh_Achievements VALUES(?,?,?,?);");

		mAddPlayerStatsStatement = connection
				.prepareStatement("INSERT IGNORE INTO mh_Daily(ID, PLAYER_ID) VALUES(DATE_FORMAT(NOW(), '%Y%j'),?);");

		mLoadAchievementsStatement = connection
				.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM mh_Achievements WHERE PLAYER_ID = ?;");

		mGetPlayerUUID = connection
				.prepareStatement("SELECT UUID FROM mh_Players WHERE NAME=?;");
		mUpdatePlayerName = connection
				.prepareStatement("UPDATE mh_Players SET NAME=? WHERE UUID=?;");
	}
	
	@Override
	protected void setupStatement_1(Connection connection) throws SQLException {
		myAddPlayerStatement = connection
				.prepareStatement("INSERT IGNORE INTO mh_Players(UUID,NAME) VALUES(?,?);");
	}

	@Override
	public List<StatStore> loadStats(StatType type, TimePeriod period, int count)
			throws DataStoreException {
		String id;
		switch (period) {
		case Day:
			id = "DATE_FORMAT(NOW(), '%Y%j')"; //$NON-NLS-1$
			break;
		case Week:
			id = "DATE_FORMAT(NOW(), '%Y%U')"; //$NON-NLS-1$
			break;
		case Month:
			id = "DATE_FORMAT(NOW(), '%Y%c')"; //$NON-NLS-1$
			break;
		case Year:
			id = "DATE_FORMAT(NOW(), '%Y')"; //$NON-NLS-1$
			break;
		default:
			id = null;
			break;
		}
		Statement statement;
		try {
			// test if connection to MySql works properly
			statement = mConnection.createStatement();
			//n++;MobHunting.debug("MySQLDD: Number of connections=%s (251)(loadStats)",n);
			ResultSet rs = statement
					.executeQuery("SELECT PLAYER_ID from `mh_Players` LIMIT 0");
			rs.close();
			statement.close();
			//n--;MobHunting.debug("MySQLDD: Number of connections=%s (256)(loadStats)",n);
		} catch (SQLException e) {
			// The connection did not work, try to initialiaze again.
			mConnection = null;
			try {
				mConnection = setupConnection();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			statement = mConnection.createStatement();
			//n++;MobHunting.debug("MySQLDD: Number of connections=%s (268)(loadStats)",n);
			ResultSet results = statement.executeQuery("SELECT "
					+ type.getDBColumn() + ", mh_Players.UUID, mh_Players.NAME from mh_"
					+ period.getTable()
					+ " inner join mh_Players on mh_Players.PLAYER_ID=mh_"
					+ period.getTable() + ".PLAYER_ID"
					+ (id != null ? " where ID=" + id : " ") + " order by "
					+ type.getDBColumn() + " desc limit " + count);
			ArrayList<StatStore> list = new ArrayList<StatStore>();

			while (results.next())
				list.add(new StatStore(type, Bukkit.getOfflinePlayer(UUID
						.fromString(results.getString(2))), results.getInt(1)));
				//list.add(new StatStore(type, results.getString(3), results.getInt(1)));

			results.close();
			statement.close();
			//n--;MobHunting.debug("MySQLDD: Number of connections=%s (285)(loadStats)",n);
			return list;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	private void performUUIDMigrate(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		//n++;MobHunting.debug("MySQLDD: Number of connections=%s (294)(UUIDMigrate)",n);
		try {
			ResultSet rs = statement
					.executeQuery("SELECT UUID from `mh_Players` LIMIT 0");
			rs.close();
			statement.close();
			//n--;MobHunting.debug("MySQLDD: Number of connections=%s (300)(UUIDMigrate)",n);
			return; // UUIDs are in place

		} catch (SQLException e) {
		}

		System.out
				.println("[MobHunting]*** Migrating MobHunting Database to UUIDs ***");

		// Add missing columns
		//Statement statement = connection.createStatement();
		//n++;MobHunting.debug("MySQLDD: Number of connections=%s ",n);
		statement
				.executeUpdate("alter table `mh_Players` add column `UUID` CHAR(40) default '**UNSPEC**' NOT NULL first");

		// Get UUID and update table
		ResultSet rs = statement
				.executeQuery("select `NAME` from `mh_Players`");
		UUIDHelper.initialize();

		PreparedStatement insert = connection
				.prepareStatement("update `mh_Players` set `UUID`=? where `NAME`=?");
		//n++;MobHunting.debug("MySQLDD: Number of connections=%s (322)(UUIDMigrate)",n);
		StringBuilder failString = new StringBuilder();
		int failCount = 0;
		while (rs.next()) {
			String player = rs.getString(1);
			UUID id = UUIDHelper.getKnown(player);
			if (id != null) {
				insert.setString(1, id.toString());
				insert.setString(2, player);
				insert.addBatch();
			} else {
				if (failString.length() != 0)
					failString.append(", ");
				failString.append(player);
				++failCount;
			}
		}

		UUIDHelper.clearCache();

		rs.close();

		if (failCount > 0) {
			System.err.println("* " + failCount
					+ " accounts failed to convert:");
			System.err.println("** " + failString.toString());
		}

		insert.executeBatch();
		insert.close();
		//n--;MobHunting.debug("MySQLDD: Number of connections=%s (352)(UUIDMigrate)",n);
		
		
		int modified = statement
				.executeUpdate("delete from `mh_Players` where `UUID`='**UNSPEC**'");
		System.out.println("[MobHunting]" + modified
				+ " players were removed due to missing UUIDs");

		statement.executeUpdate("alter table `mh_Players` drop primary key");
		statement
				.executeUpdate("alter table `mh_Players` modify `UUID` CHAR(40) NOT NULL PRIMARY KEY first");

		System.out
				.println("[MobHunting]*** Player UUID migration complete ***");

		statement.close();
		//n--;MobHunting.debug("MySQLDD: Number of connections=%s (368)(UUIDMigrate)",n);
		connection.commit();
	}

	private void performAddNewMobs(Connection connection) throws SQLException {

		Statement statement = connection.createStatement();
		//n++;MobHunting.debug("MySQLDD: Number of connections=%s (375)(addNewMobs)",n);
		try {
			ResultSet rs = statement
					.executeQuery("SELECT PvpPlayer_kill from `mh_Daily` LIMIT 0");
			rs.close();
			statement.close();
			//n--;MobHunting.debug("MySQLDD: Number of connections=%s (381)(addNewMobs)",n);
			return; // PvpPlayer row exits

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
					.executeQuery("SELECT Giant_kill from `mh_Daily` LIMIT 0");
			rs.close();
			statement.close();
			//n--;MobHunting.debug("MySQLDD: Number of connections=%s (419)(addNewMobs)",n);
			return; // Giant_Kill row exits

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
					.executeUpdate("alter table `mh_eekly` add column `Guardian_kill`  INTEGER NOT NULL DEFAULT 0");
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

			statement.executeUpdate("DROP TRIGGER IF EXISTS `mh_DailyInsert`");
			statement.executeUpdate("DROP TRIGGER IF EXISTS `mh_DailyUpdate`");
			setupTrigger(connection);

			System.out.println("[MobHunting]*** Adding new Mobs complete ***");

		}

		statement.close();
		//n--;MobHunting.debug("MySQLDD: Number of connections=%s (520)(addNewMobs)",n);
		connection.commit();
	}
}
