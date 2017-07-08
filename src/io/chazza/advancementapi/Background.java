package io.chazza.advancementapi;

public enum Background {
	ADVENTURE("minecraft:textures/gui/advancements/backgrounds/adventure.png"), END(
			"minecraft:textures/gui/advancements/backgrounds/end.png"), HUSBANDRY(
					"minecraft:textures/gui/advancements/backgrounds/husbandry.png"), NETHER(
							"minecraft:textures/gui/advancements/backgrounds/nether.png"), STONE(
									"minecraft:textures/gui/advancements/backgrounds/stone.png"), fromNamespace(null);

	private String str;

	Background(String str) {
		this.str = str;
	}

	public String toString() {
        return str;
    }
	
}