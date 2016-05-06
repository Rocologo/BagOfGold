package one.lindegaard.MobHunting.storage;

public class DataDeleteException extends Exception
{

	private static final long serialVersionUID = 1959347663962873942L;

	public DataDeleteException()
	{
		
	}
	
	public DataDeleteException(String message)
	{
		super(message);
	}
	
	public DataDeleteException(Throwable inner)
	{
		super(inner);
	}
}
