package com.absabefi.coran.audio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;

import org.apache.cordova.CordovaPlugin;

/**
 * Quand l'app passe en arriere-plan (ecran verrouille) pendant qu'un son joue,
 * on demarre un service de premier plan pour qu'Android ne gele pas l'app.
 */
public class CoranAudioPlugin extends CordovaPlugin {

    @Override
    protected void pluginInitialize() {
        if (Build.VERSION.SDK_INT >= 33
                && !cordova.hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            cordova.requestPermission(this, 0, Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        Context ctx = cordova.getActivity().getApplicationContext();
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        if (am != null && am.isMusicActive()) {
            Intent i = new Intent(ctx, CoranAudioService.class);
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    ctx.startForegroundService(i);
                } else {
                    ctx.startService(i);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        stopService();
    }

    @Override
    public void onDestroy() {
        stopService();
    }

    private void stopService() {
        Context ctx = cordova.getActivity().getApplicationContext();
        ctx.stopService(new Intent(ctx, CoranAudioService.class));
    }
}
