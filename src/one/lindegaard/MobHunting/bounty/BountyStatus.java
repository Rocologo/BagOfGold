package one.lindegaard.MobHunting.bounty;

public enum BountyStatus {
	open("open", 0), completed("completed", 1), expired("expired", 2), canceled("canceled", 3), deleted("deleted", 4);

	private String mName;
	private int mStatus;

	BountyStatus(String name, int status) {
		mName = name;
		mStatus = status;
	};

	public int getValue() {
		return mStatus;
	}

	public void setValue(int value) {
		mStatus = value;
	}

	public static BountyStatus valueOf(int value) {
		for (BountyStatus bs : values()) {
			if (bs.getValue() == value) {
				return bs;
			}
		}
		return null;
	}

	public String getName() {
		return mName;
	}

}
