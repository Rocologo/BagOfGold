package au.com.mineauz.MobHunting.storage.asynch;

import au.com.mineauz.MobHunting.storage.IDataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;

public interface DataStoreTask<T>
{
	public T run(IDataStore store) throws DataStoreException;
	
	public boolean readOnly();
}
