package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import one.lindegaard.BagOfGold.BagOfGold;

public class MySQLDataStore extends DatabaseDataStore {

	private BagOfGold plugin;

	public MySQLDataStore(BagOfGold plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Locale.setDefault(new Locale("us", "US"));
			Class.forName("com.mysql.jdbc.Driver");
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(BagOfGold.getConfigManager().databaseUsername);
			dataSource.setPassword(BagOfGold.getConfigManager().databasePassword);
			if (BagOfGold.getConfigManager().databaseHost.contains(":")) {
				dataSource.setServerName(BagOfGold.getConfigManager().databaseHost.split(":")[0]);
				dataSource.setPort(Integer.valueOf(BagOfGold.getConfigManager().databaseHost.split(":")[1]));
			} else {
				dataSource.setServerName(BagOfGold.getConfigManager().databaseHost);
			}
			dataSource.setDatabaseName(BagOfGold.getConfigManager().databaseName + "?autoReconnect=true");
			Connection c = dataSource.getConnection();
			Statement statement = c.createStatement();
			statement.executeUpdate("SET NAMES 'utf8'");
			statement.executeUpdate("SET CHARACTER SET 'utf8'");
			statement.close();
			c.setAutoCommit(false);
			return c;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("MySQL not present on the classpath", classNotFoundEx);
		} catch (SQLException sqlEx) {
			throw new DataStoreException("Error creating sql connection", sqlEx);
		}
	}

	@Override
	protected void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException {
		switch (preparedConnectionType) {
		case GET_PLAYER_DATA:
			mGetPlayerData = connection.prepareStatement("SELECT * FROM mh_Players WHERE UUID=?;");
			break;
		case GET_PLAYER_UUID:
			mGetPlayerUUID = connection.prepareStatement("SELECT UUID FROM mh_Players WHERE NAME=?;");
			break;
		case UPDATE_PLAYER_NAME:
			mUpdatePlayerName = connection.prepareStatement("UPDATE mh_Players SET NAME=? WHERE UUID=?;");
			break;
		case UPDATE_PLAYER_SETTINGS:
			mUpdatePlayerSettings = connection.prepareStatement(
					"UPDATE mh_Players SET LEARNING_MODE=?,MUTE_MODE=?,BALANCE=?,BALANCE_CHANGES=?,BANK_BALANCE=?, BANK_BALANCE_CHANGES=? WHERE UUID=?;");
			break;
		case INSERT_PLAYER_DATA:
			mInsertPlayerData = connection.prepareStatement(
					"INSERT INTO mh_Players (UUID,NAME,LEARNING_MODE,MUTE_MODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,?,?,?,?);");
			break;
		case GET_PLAYER_BY_PLAYER_ID:
			mGetPlayerByPlayerId = connection.prepareStatement("SELECT UUID FROM mh_Players WHERE PLAYER_ID=?;");
			break;
		}

	}

	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {

		// reference
		// http://stackoverflow.com/questions/6115612/how-to-convert-an-entire-mysql-database-characterset-and-collation-to-utf-8

		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.GREEN + "[BagOfGold] Converting BagOfGold Database to UTF8");

		Connection connection = setupConnection();

		try {
			Statement create = connection.createStatement();

			create.executeUpdate(
					"ALTER DATABASE " + database_name + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate(
					"ALTER TABLE mh_Achievements CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_AllTime CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate(
					"ALTER TABLE mh_Bounties CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Daily CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Mobs CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Monthly CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Players CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Weekly CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Yearly CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			console.sendMessage(ChatColor.GREEN + "[BagOfGold] Done.");

		} catch (SQLException e) {
			console.sendMessage(ChatColor.RED + "[BagOfGold] Something went wrong.");
			e.printStackTrace();
		}

	}

	// *******************************************************************************
	// V1 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV1Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = BagOfGold.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players "//
				+ "(UUID CHAR(40) ,"//
				+ " NAME VARCHAR(20),"//
				+ " PLAYER_ID INTEGER NOT NULL AUTO_INCREMENT,"//
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + ","//
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0,"//
				+ " BALANCE REAL DEFAULT 0,"//
				+ " BALANCE_CHANGES REAL DEFAULT 0,"//
				+ " BANK_BALANCE REAL DEFAULT 0,"//
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0,"//
				+ " PRIMARY KEY (PLAYER_ID))");

		create.close();
		connection.commit();

	}

}
