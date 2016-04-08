package au.com.mineauz.MobHunting.storage;

public interface IDataCallback<T>
{
	public void onCompleted(T data);
	
	public void onError(Throwable error);
}
