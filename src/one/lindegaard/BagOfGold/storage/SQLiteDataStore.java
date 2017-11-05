package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import one.lindegaard.BagOfGold.BagOfGold;

public class SQLiteDataStore extends DatabaseDataStore {

	private BagOfGold plugin;

	public SQLiteDataStore(BagOfGold plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager
					.getConnection("jdbc:sqlite:" + BagOfGold.getInstance().getDataFolder().getPath() + "/"
							+ BagOfGold.getConfigManager().databaseName + ".db");
			c.setAutoCommit(false);
			return c;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("SQLite not present on the classpath", classNotFoundEx);
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
		case INSERT_PLAYER_DATA:
			mInsertPlayerData = connection.prepareStatement(
					"INSERT INTO mh_Players (UUID,NAME,PLAYER_ID,LEARNING_MODE,MUTE_MODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,(SELECT IFNULL(MAX(PLAYER_ID),0)+1 FROM mh_Players),?,?,?,?,?,?);");
			break;
		case UPDATE_PLAYER_SETTINGS:
			mUpdatePlayerSettings = connection
					.prepareStatement("UPDATE mh_Players SET LEARNING_MODE=?,MUTE_MODE=?,BALANCE=?,BALANCE_CHANGES=?,BANK_BALANCE=?,BANK_BALANCE_CHANGES=? WHERE UUID=?;");
			break;
		case GET_PLAYER_BY_PLAYER_ID:
			mGetPlayerByPlayerId = connection.prepareStatement("SELECT UUID FROM mh_Players WHERE PLAYER_ID=?;");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[MobHunting] this command is only for MySQL");
	}

	// *******************************************************************************
	// V1 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV1Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = BagOfGold.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players" //
				+ "(UUID TEXT," //
				+ " NAME TEXT, " //
				+ " PLAYER_ID INTEGER NOT NULL DEFAULT 1," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + "," //
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0," //
				+ " BALANCE_CHANGES REAL DEFAULT 0," //
				+ " BANK_BALANCE REAL DEFAULT 0," //
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0," //
				+ " PRIMARY KEY(PLAYER_ID))");

		create.close();
		connection.commit();

	}

}
