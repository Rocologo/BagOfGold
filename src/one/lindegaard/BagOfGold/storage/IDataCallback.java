package one.lindegaard.BagOfGold.storage;

public interface IDataCallback<T>
{
	void onCompleted(T data);
	
	void onError(Throwable error);
}
