package au.com.mineauz.MobHunting.storage;

public class UserNotFoundException extends DataStoreException
{
	private static final long	serialVersionUID	= 7350275975771597231L;

	public UserNotFoundException()
	{
		super();
	}
	
	public UserNotFoundException(String message)
	{
		super(message);
	}
}
