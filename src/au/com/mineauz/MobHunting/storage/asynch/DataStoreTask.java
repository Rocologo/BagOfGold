package au.com.mineauz.MobHunting.storage.asynch;

import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;

public interface DataStoreTask<T>
{
	public T run(DataStore store) throws DataStoreException;
	
	public boolean readOnly();
}
