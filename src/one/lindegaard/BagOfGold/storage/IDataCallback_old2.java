package one.lindegaard.BagOfGold.storage;

public interface IDataCallback_old2<T>
{
	void onCompleted(T data);
	
	void onError(Throwable error);
}
