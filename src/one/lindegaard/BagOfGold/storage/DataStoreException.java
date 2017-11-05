package one.lindegaard.BagOfGold.storage;

public class DataStoreException extends Exception
{

	private static final long serialVersionUID = -5399664455224338175L;

	public DataStoreException()
	{
		
	}

	public DataStoreException(String message, Throwable cause) {
	    super(message, cause);
	}
	
	public DataStoreException(String message)
	{
		super(message);
	}
	
	public DataStoreException(Throwable inner)
	{
		super(inner);
	}
}
