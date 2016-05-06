package one.lindegaard.MobHunting.storage.asynch;

import one.lindegaard.MobHunting.storage.DataDeleteException;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.IDataStore;

public interface DataDeleteTask<T>
{
	public T run(IDataStore store) throws DataStoreException, DataDeleteException;
	
	public boolean readOnly();
}
