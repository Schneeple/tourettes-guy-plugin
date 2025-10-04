package com.github.schneeple.player;

import com.github.schneeple.sound.Sound;
import com.github.schneeple.sound.SoundEngine;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

public class CEngineerChatTrolls {

    @Inject
    private Client client;

    @Inject
    private SoundEngine soundEngine;

    @Inject
    private ScheduledExecutorService executor;

    public void runTriggers(ChatMessage chatMessageFromC) {
        Optional<String> standardisedNameOpt = LocalPlayer.getStandardisedName(client);
        if (standardisedNameOpt.isEmpty()) return;

        String standardisedName = standardisedNameOpt.get();
        if ("skill specs".equals(standardisedName))
            skillSpecsChatTrolls(chatMessageFromC);
    }

    private void skillSpecsChatTrolls(ChatMessage chatMessageFromC) {
        switch (chatMessageFromC.getMessage().toLowerCase()) {
            case "hello skill specs":
                soundEngine.playClip(Sound.CHAT_TROLL_SKILL_SPECS_HELLO, executor);
                break;
            case "are you ok":
                soundEngine.playClip(Sound.CHAT_TROLL_SKILL_SPECS_OK, executor);
                break;
            case "oops":
                soundEngine.playClip(Sound.CHAT_TROLL_SKILL_SPECS_OOPS, executor);
                break;
            default: break;
        }
    }
}
