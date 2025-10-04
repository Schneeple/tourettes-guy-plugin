package com.github.schneeple;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TourettesGuyCompletedPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TourettesGuyCompletedPlugin.class);
		RuneLite.main(args);
	}
}
