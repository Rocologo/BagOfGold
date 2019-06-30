package one.lindegaard.BagOfGold;

import one.lindegaard.Core.Messages.MessageType;

public enum MessageType_old {
	Chat("Chat"), ActionBar("ActionBar"), BossBar("BossBar"), Title("Title"), Subtitle("Subtitle");

	private final String name;

	private MessageType_old(String name) {
		this.name = name;
	}

	public boolean equalsName(String otherName) {
		return (otherName != null) && name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public MessageType valueOf(int id) {
		return MessageType.values()[id];
	}

	public String getName() {
		return name;
	}

}
