# Tourettes Guy: Completed [![Plugin Installs](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/tourettes-guy-completed)](https://runelite.net/plugin-hub/schneeple) [![Plugin Rank](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/rank/plugin/tourettes-guy-completed)](https://runelite.net/plugin-hub)

##### A plugin for [RuneLite](https://runelite.net/)

Tourettes Guy announces when you complete an achievement!

Huge thanks to [Tourettes Guy](https://www.youtube.com/channel/UCUNoAjAgVHEHc6jrUr4XuWQ) for providing custom recorded audio for this plugin!
___
## General Troubleshooting
BEFORE TRYING ANYTHING ELSE, ENABLE THIS IN THE **RUNESCAPE** SETTINGS

![image](https://user-images.githubusercontent.com/62370532/208992085-e2c07494-d8bb-489e-b7f3-ed538175acbc.png)
___

## Customising your sounds

### 1. Locate your `.runelite` folder

On windows this is likely to be here: `C:\Users\<your username>\.runelite`

If you aren't sure, it's the same place that stores your `settings.properties`

Within this `.runelite` folder, there should be a `tourettes-guy-sounds` folder, which is where the sound files are downloaded to

### 2. Prepare your sound files

Make sure your files are all `.wav` format (just changing the extension won't work, actually convert them)

Make sure the file name __exactly__ matches the name of the existing file (in `tourettes-guy-sounds` folder) you want to replace

### 3. Understand how the files are handled

If you replace an existing file in `tourettes-guy-sounds` using exactly the same file name, your sound will be loaded instead

If you place a new file with an unexpected file name in `tourettes-guy-sounds`, it will be deleted

If you place a new folder inside `tourettes-guy-sounds` that is unexpected, this should be left as is, so can be used to store multiple sounds that you may want to swap in at a future date

If you want to revert to a default sound file, simply delete the relevant file in `tourettes-guy-sounds` and the default file will be re-downloaded when the plugin next starts

### 4. If it fails to play your sound

Remove your sound and make sure it plays the default sound for that event - if not, there is something misconfigured in your plugin _or in-game_ settings. For example, the collection log event can only be captured if your _in-game_ notifications for collection log slots are turned on

Check that your file is actually a valid `.wav` and not just a renamed `.mp3` or similar

Check that the file is still there in the `tourettes-guy-sounds` folder, if you accidentally used an incorrect file name, it won't have been loaded, and will have been deleted
___

## Other information

### Announcement options include

- level ups
- quests
- collection log slot (requires game chat notification messages)
- achievement diaries (per tier, not per task)
- combat achievements (per task, not per tier)
- dying "on my hcim" (plays for any account type)

and 'public' chat messages for each of the above (that only you can see)

Sounds are downloaded to the local file system instead of being 'baked in' to the plugin build, allowing for further
expansion in the future while also 'supporting' user-swapped sounds for pre-existing events

### Planned / Work In Progress expansions

- none

### Potential future expansions

- clue scroll completion

### Known Issues

- PulseAudio on linux can just refuse to accept the audio formats used despite claiming to accept them :man_shrugging:
- ~~PipeWire on linux can cut off sounds early - this might be fixed given a more recent version of the jdk and more recent version of pipewire, but currently cannot confirm :man_shrugging:~~
  - PipeWire was cutting off sounds early, if they were between ~1.5 seconds and 2 seconds long (#26). This has been worked around by artificially extending sounds with silence to bring them up to 2 seconds long, where they are no longer cut off.
  I believe I've updated all such files, but please report any still getting cut off for you via an issue on this repo (once the fix is actually released, see #26 for progress).