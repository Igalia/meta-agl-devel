# 4A recipes

Collection of recipes for first integration of 4A (Advanced AGL Audio Architecture).

This layer should disappear and recipes merged into meta-agl.


## Pulseaudio

Currently pulseaudio is disabled for 4A.

If you want to start pulseaudio manually:

```bash
systemctl --user start pulseaudio
```

If you want to add pulseaudio to alsa:

```bash
mv /usr/share/alsa/alsa.conf.d.disable/* /usr/share/alsa/alsa.conf.d/
```

