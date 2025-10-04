package com.github.schneeple.trolls;

import com.github.schneeple.TourettesGuyCompletedConfig;
import com.github.schneeple.animation.AnimationTriggers;
import com.github.schneeple.player.TourettesGuyPlayer;
import com.github.schneeple.projectile.ProjectileID;
import com.github.schneeple.sound.Sound;
import com.github.schneeple.sound.SoundEngine;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;

import static com.github.schneeple.projectile.ProjectileSoundID.SNOWBALL_SOUND_IDS;

public class TrollTriggers {
    private static final Pattern STAT_SPY_REGEX = Pattern.compile(Text.standardize(TourettesGuyPlayer.RSN + " is reading your skill stats!"));
    private static final Pattern ESCAPE_CRYSTAL_REGEX = Pattern.compile(Text.standardize(TourettesGuyPlayer.RSN + " activated your crystal\\."));
    private static final Pattern TOB_GREEN_BALL_BOUNCE_REGEX = Pattern.compile(Text.standardize("<col=0a721f>A powerful projectile bounces into your direction\\.\\.\\.</col>"));

    private static final int SNOWBALL_COOLDOWN_TICKS = 50;
    private static final Duration SNOWBALL_DELAY_GAUNTLET_SOUND = Duration.ofMinutes(9);
    private static final Duration SNOWBALL_DELAY_SOUNDS = Duration.ofSeconds(20);

    private static final WorldArea GAUNTLET_LOBBY = new WorldArea(new WorldPoint(3026, 6117, 1), 14, 14);

    @Inject
    private Client client;

    @Inject
    private TourettesGuyCompletedConfig config;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private SoundEngine soundEngine;

    @Inject
    private TourettesGuyPlayer tourettesGuy;

    @Inject
    private AnimationTriggers animationTriggers;

    private int lastSnowballTriggerTick = -1;

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE && chatMessage.getType() != ChatMessageType.SPAM)
            return;

        if (!config.easterEggs())
            return;

        String standardisedMessage = Text.standardize(chatMessage.getMessage());
        if (STAT_SPY_REGEX.matcher(standardisedMessage).matches()) {
            soundEngine.playClip(Sound.STAT_SPY_SOUP, executor);

        } else if (ESCAPE_CRYSTAL_REGEX.matcher(standardisedMessage).matches()) {
            soundEngine.playClip(Sound.ESCAPE_CRYSTAL, executor);

        } else if (tourettesGuy.isFollowingMe() && TOB_GREEN_BALL_BOUNCE_REGEX.matcher(standardisedMessage).matches()) {
            soundEngine.playClip(Sound.TOB_GREEN_BALL, executor);
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged graphicChanged) {
        if (!config.easterEggs())
            return;

        if (!tourettesGuy.isWearingAttackTrollRequirements())
            return;

        iceBarrageTroll(graphicChanged.getActor());
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged) {
        if (!config.easterEggs())
            return;

        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == animationChanged.getActor() && localPlayer.getAnimation() != -1) {
            animationTriggers.runTriggersForLocalPlayerAnimation(localPlayer.getAnimation());
            return;
        }

        if (tourettesGuy.isOutOfRenderDistance())
            return;

        Actor actor = animationChanged.getActor();
        if (!tourettesGuy.actorEquals(actor))
            return;

        int actorAnimationId = actor.getAnimation();
        if (actorAnimationId == -1)
            return;

        animationTriggers.runTriggersForTourettesGuyAnimation(actorAnimationId);
    }

    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed) {
        if (shouldMuteSnowballs() && SNOWBALL_SOUND_IDS.contains(soundEffectPlayed.getSoundId())) {
            soundEffectPlayed.consume();
        }
    }

    @Subscribe
    public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed areaSoundEffectPlayed) {
        if (shouldMuteSnowballs() && SNOWBALL_SOUND_IDS.contains(areaSoundEffectPlayed.getSoundId())) {
            areaSoundEffectPlayed.consume();
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved projectileMoved) {
        if (tourettesGuy.isOutOfRenderDistance())
            return;

        if (!config.easterEggs())
            return;

        Projectile projectile = projectileMoved.getProjectile();
        if (projectile.getId() != ProjectileID.SNOWBALL)
            return;

        int currentTick = client.getTickCount();
        if (currentTick - lastSnowballTriggerTick < SNOWBALL_COOLDOWN_TICKS)
            return;

        Actor myself = client.getLocalPlayer();
        if (myself == null)
            return;

        Actor projectileInteracting = projectile.getInteracting();
        if (!myself.equals(projectileInteracting))
            return;

        if (tourettesGuy.couldHaveThrownProjectileFrom(projectile)) {
            lastSnowballTriggerTick = currentTick;
            playSoundFromSnowball();
        }
    }

    private void playSoundFromSnowball() {
        WorldPoint currentLocation = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
        if (GAUNTLET_LOBBY.contains(currentLocation)) {
            soundEngine.playClip(Sound.SNOWBALL_GAUNTLET_LOBBY, executor, SNOWBALL_DELAY_GAUNTLET_SOUND);
            return;
        }

        Sound sound = pickSnowballSoundBasedOnEquipment();
        if (tourettesGuy.isWearing(ItemID.TRAIL_MAGE_AMULET)) {
            soundEngine.playClip(sound, executor);
        } else {
            soundEngine.playClip(sound, executor, SNOWBALL_DELAY_SOUNDS);
        }
    }

    private Sound pickSnowballSoundBasedOnEquipment() {
        if (tourettesGuy.isWearing(ItemID.BUCKET_HELM_GOLD))
            return Sound.SNOWBALL_EQUIPPING_BUCKET_HELM_G_OR_FUNNY_FEEL;

        if (tourettesGuy.isWearing(ItemID.GIANT_BOOT))
            return Sound.SNOWBALL_EQUIPPING_GIANT_BOOT;

        if (tourettesGuy.isWearing(ItemID.WISE_SPECTACLES))
            return Sound.SNOWBALL_EQUIPPING_SAGACIOUS_SPECTACLES;

        if (tourettesGuy.isWearing(ItemID.TOA_AMASCUT_MASK))
            return Sound.SNOWBALL_EQUIPPING_MASK_OF_REBIRTH;

        return Sound.randomSnowballSoundNotFromEquippedItem();
    }

    private boolean shouldMuteSnowballs() {
        if (tourettesGuy.isOutOfRenderDistance())
            return false;

        return config.muteSnowballsIfTourettesGuyIsNear();
    }

    private void iceBarrageTroll(Actor actorFromGraphicChanged) {
        if (!(actorFromGraphicChanged instanceof NPC))
            return;

        NPC npc = (NPC) actorFromGraphicChanged;
        if (!npc.hasSpotAnim(SpotanimID.ICE_BARRAGE_IMPACT))
            return;
        if (!tourettesGuy.isInteracting(npc))
            return;
        if (tourettesGuy.tilesFrom(client.getLocalPlayer()) > 10)
            return;

        soundEngine.playClip(Sound.ATTACK_TROLL_IB, executor);
    }
}
