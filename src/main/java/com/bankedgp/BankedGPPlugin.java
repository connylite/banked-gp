package com.bankedgp;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(name = "Banked GP")
public class BankedGPPlugin extends Plugin
{
	private static final int LOOTING_BAG_ID = 516;

	private final Map<Integer, Integer> containerHashMap = new HashMap<>();

	private final Map<Integer, Map<Integer, Integer>> containerMap = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private BankedGPConfig config;

	@Provides
	BankedGPConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankedGPConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged ev)
	{
		if (ev.getContainerId() == InventoryID.BANK.getId() || (ev.getContainerId() == InventoryID.INVENTORY.getId() && config.grabFromInventory()) || (ev.getContainerId() == LOOTING_BAG_ID && config.grabFromLootingBag()))
		{
			updateItemsFromItemContainer(ev.getContainerId(), ev.getItemContainer());
		}
	}

	private void updateItemsFromItemContainer(final int inventoryId, final ItemContainer c)
	{
		// Check if the contents have changed.
		if (c == null)
		{
			return;
		}

		final Map<Integer, Integer> m = new HashMap<>();
		for (Item item : c.getItems())
		{
			if (item.getId() == -1)
			{
				continue;
			}

			// Account for noted items, ignore placeholders.
			int itemID = item.getId();
			final ItemComposition itemComposition = itemManager.getItemComposition(itemID);
			if (itemComposition.getPlaceholderTemplateId() != -1)
			{
				continue;
			}

			if (itemComposition.getNote() != -1)
			{
				itemID = itemComposition.getLinkedNoteId();
			}

			final int qty = m.getOrDefault(itemID, 0) + item.getQuantity();
			m.put(itemID, qty);
		}

		updateInventoryMap(inventoryId, m);
	}

	private void updateInventoryMap(final int inventoryId, final Map<Integer, Integer> m)
	{
		final int curHash = m.hashCode();
		if (curHash != containerHashMap.getOrDefault(inventoryId, -1))
		{
			containerHashMap.put(inventoryId, curHash);
			containerMap.put(inventoryId, m);
			log.debug("{} container map: {}", inventoryId, m);
			//SwingUtilities.invokeLater(() -> panel.setInventoryMap(inventoryId, m));
		}
	}
}
