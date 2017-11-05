package one.lindegaard.BagOfGold.storage;

public class UserNotFoundException extends DataStoreException {

	private static final long serialVersionUID = -7118924404417328362L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}
}
