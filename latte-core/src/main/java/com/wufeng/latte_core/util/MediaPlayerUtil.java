package com.wufeng.latte_core.util;

import android.app.Activity;
import android.media.MediaPlayer;

import com.wufeng.latte_core.R;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayerUtil {
    List<MediaPlayer> mediaPlayerList = new ArrayList<>();
    MediaPlayer mediaPlayerDi;
    Activity activity;

    public MediaPlayerUtil(Activity activity){
        this.activity = activity;
    }

    /**
     * init media player
     *
     * @param fileId
     * @return
     */
    public MediaPlayer initSound(int fileId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, fileId);
        mediaPlayerList.add(mediaPlayer);
        return mediaPlayer;
    }

    public void startMediaPlayerDi() {
        try {
            if (mediaPlayerDi == null) {
                mediaPlayerDi = initSound(R.raw.di);
            }
            mediaPlayerDi.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * release all media player
     */
    public void releaseMediaPlayerAll() {
        try {
            try {
                if (mediaPlayerDi != null) {
                    mediaPlayerDi.release();
                    mediaPlayerDi = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (MediaPlayer mediaPlayer : mediaPlayerList) {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mediaPlayerList.clear();
            mediaPlayerList = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
