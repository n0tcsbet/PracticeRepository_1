package com.example.myapplicationetc;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ContentActivity extends AppCompatActivity {

    private ListView llaudio;
    private MediaPlayer mediaPlayer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private VideoView videoView;

    int[] audioRes = {R.raw.highfleet_radar1, R.raw.highfleet_radar2};
    String[] audioTitles = {"Трек 1", "Трек 2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_media);

        llaudio = findViewById(R.id.lvAudio);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        videoView = findViewById(R.id.videoView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                audioTitles
        );
        llaudio.setAdapter(adapter);

        llaudio.setOnItemClickListener((parent, view, position, id) -> playAudio(audioRes[position]));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            stopAudio();
            initVideo();
            Toast.makeText(this, "Обновлено ;)))", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });

        initVideo();
    }

    private void playAudio(int resId) {
        stopAudio();
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.start();
        Toast.makeText(this, "Делаем дело...", Toast.LENGTH_SHORT).show();
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void initVideo() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video3);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> videoView.start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAudio();
    }
}