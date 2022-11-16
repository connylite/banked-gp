package com.bankedgp;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bankedgp")
public interface BankedGPConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

	@ConfigItem(
		keyName = "grabFromInventory",
		name = "Include player inventory",
		description = "Toggles whether the items inside your inventory will be included in the calculations",
		position = 5
	)
	default boolean grabFromInventory()
	{
		return false;
	}

	@ConfigItem(
		keyName = "grabFromLootingBag",
		name = "Include looting bag",
		description = "Toggles whether the items stored inside your Looting Bag will be included in the calculations",
		position = 6
	)
	default boolean grabFromLootingBag()
	{
		return false;
	}
}
