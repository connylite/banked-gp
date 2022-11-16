package com.bankedgp;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BankedGPPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BankedGPPlugin.class);
		RuneLite.main(args);
	}
}