package one.lindegaard.MobHunting;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class HttpTools {

	public static boolean isHomePageReachable(URL url) {
		try {
			// open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

			// trying to retrieve data from the source. If there
			// is no connection, this line will fail
			urlConnect.setConnectTimeout(5000);
			@SuppressWarnings("unused")
			Object objData = urlConnect.getContent();

		} catch (UnknownHostException e) {
			// e.printStackTrace();
			return false;
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
		return true;
	}

}
