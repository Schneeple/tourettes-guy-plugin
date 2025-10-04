package com.github.schneeple;

import com.github.schneeple.announce.AnnouncementTriggers;
import com.github.schneeple.eastereggs.EasterEggTriggers;
import com.github.schneeple.player.CEngineerPlayer;
import com.github.schneeple.player.LoggedInState;
import com.github.schneeple.qol.QualityOfLifeTriggers;
import com.github.schneeple.sound.SoundFileManager;
import com.github.schneeple.trolls.TrollTriggers;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@PluginDescriptor(
        name = "C Engineer: Completed",
        description = "C Engineer announces when you complete an achievement",
        tags = {"c engineer", "stats", "levels", "quests", "diary", "announce"}
)

public class CEngineerCompletedPlugin extends Plugin {
    @Inject
    private Client client;

    @Getter(AccessLevel.PACKAGE)
    @Inject
    private ClientThread clientThread;

    @Inject
    private CEngineerCompletedConfig config;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private EventBus eventBus;

    @Inject
    private CEngineerPlayer cEngineer;

    @Inject
    private AnnouncementTriggers announcementTriggers;

    @Inject
    private EasterEggTriggers easterEggTriggers;

    @Inject
    private TrollTriggers trollTriggers;

    @Inject
    private QualityOfLifeTriggers qolTriggers;

    @Inject
    private LoggedInState loggedInState;

    @Override
    protected void startUp() throws Exception {
        eventBus.register(cEngineer);
        eventBus.register(announcementTriggers);
        eventBus.register(easterEggTriggers);
        eventBus.register(trollTriggers);
        eventBus.register(qolTriggers);
        eventBus.register(loggedInState);
        loggedInState.setForCurrentGameState(client.getGameState());
        announcementTriggers.initialise();

        executor.submit(() -> SoundFileManager.prepareSoundFiles(okHttpClient, config.downloadStreamerTrolls()));
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(cEngineer);
        eventBus.unregister(announcementTriggers);
        eventBus.unregister(easterEggTriggers);
        eventBus.unregister(trollTriggers);
        eventBus.unregister(qolTriggers);
        eventBus.unregister(loggedInState);

        announcementTriggers.shutDown();
    }

    @Provides
    CEngineerCompletedConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CEngineerCompletedConfig.class);
    }
}
