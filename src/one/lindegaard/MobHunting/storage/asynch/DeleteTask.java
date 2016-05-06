package one.lindegaard.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Set;

import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.storage.DataDeleteException;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.IDataStore;

public class DeleteTask implements DataDeleteTask<Void>
{
	private HashSet<Bounty> mWaitingBounties = new HashSet<Bounty>();
	
	
	public DeleteTask(Set<Object> waiting)
	{
		synchronized(waiting)
		{
			mWaitingBounties.clear();
			
			for(Object obj : waiting)
			{
				if(obj instanceof Bounty)
					mWaitingBounties.add((Bounty)obj);
			}
			
			waiting.clear();
		}
	}
	@Override
	public Void run( IDataStore store ) throws DataDeleteException, DataStoreException
	{

		if(!mWaitingBounties.isEmpty()){
			store.deleteBounty(mWaitingBounties);
		}
		
		return null;
	}

	@Override
	public boolean readOnly()
	{
		return false;
	}
}
