package com.github.schneeple;

import com.github.schneeple.announce.AnnouncementTriggers;
import com.github.schneeple.eastereggs.EasterEggTriggers;
import com.github.schneeple.player.TourettesGuyPlayer;
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
        name = "Tourettes Guy: Completed",
        description = "Tourettes Guy announces when you complete an achievement",
        tags = {"Tourettes Guy", "stats", "levels", "quests", "diary", "announce"}
)

public class TourettesGuyCompletedPlugin extends Plugin {
    @Inject
    private Client client;

    @Getter(AccessLevel.PACKAGE)
    @Inject
    private ClientThread clientThread;

    @Inject
    private TourettesGuyCompletedConfig config;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private EventBus eventBus;

    @Inject
    private TourettesGuyPlayer tourettesGuy;

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
        eventBus.register(tourettesGuy);
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
        eventBus.unregister(tourettesGuy);
        eventBus.unregister(announcementTriggers);
        eventBus.unregister(easterEggTriggers);
        eventBus.unregister(trollTriggers);
        eventBus.unregister(qolTriggers);
        eventBus.unregister(loggedInState);

        announcementTriggers.shutDown();
    }

    @Provides
    TourettesGuyCompletedConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TourettesGuyCompletedConfig.class);
    }
}
