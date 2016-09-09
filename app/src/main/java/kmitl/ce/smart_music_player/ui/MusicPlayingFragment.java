package kmitl.ce.smart_music_player.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kmitl.ce.smart_music_player.R;
import kmitl.ce.smart_music_player.model.MusicInformation;

/**
 * Created by Jo on 8/16/2016.
 */
public class MusicPlayingFragment extends DialogFragment {
    public static final String KEY_MESSAGE = "music_inform";

    private MusicInformation musicInformation;
    private Integer playButtonState = null;
    private SeekBar seekBarprocess;
    private TextView songCurrentDuration;
    private TextView songTotalDuration;
    private android.os.Handler handler = new android.os.Handler();
    private MediaPlayer mediaPlayer;
    private TextView songNameView;
    private TextView artistNameView;
    private ImageView imageView;

    public static MusicPlayingFragment newInstance() {
        MusicPlayingFragment fragment = new MusicPlayingFragment();
        return fragment;
    }

    public void setMusicInformation(MusicInformation musicInformation) {
        this.musicInformation = musicInformation;
    }


    public MusicPlayingFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.music_playing_fragment, container, false);

        mediaPlayer = ((PlaylistActivity) getActivity()).getMediaPlayer();
        songNameView = (TextView) rootView.findViewById(R.id.song_name);
        artistNameView = (TextView) rootView.findViewById(R.id.song_artist);
        seekBarprocess = (SeekBar) rootView.findViewById(R.id.seekBar);
        songCurrentDuration = (TextView) rootView.findViewById(R.id.currentDurationLabel);
        songTotalDuration = (TextView) rootView.findViewById(R.id.totalDurationLabel);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next);
        ImageButton previousButton = (ImageButton) rootView.findViewById(R.id.previous);
        ImageButton repeatButton = (ImageButton) rootView.findViewById(R.id.repeat);
        ImageButton shuffleButton = (ImageButton) rootView.findViewById(R.id.shuffle);

        ImageButton backBtn = (ImageButton) rootView.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        final ImageButton playButton = (ImageButton) rootView.findViewById(R.id.play);
        Picasso.with(getActivity().getApplicationContext())
                .load(((PlaylistActivity) getActivity()).getPlayStateImage())
                .into(playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlaylistActivity) getActivity()).playStateClick(playButton);
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //next
                ((PlaylistActivity) getActivity()).nextSong();
                setUpMusicPlayerView(view);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previous
                ((PlaylistActivity) getActivity()).previousSong();
                setUpMusicPlayerView(view);
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    seekBarprocess.setProgress(mCurrentPosition / 1000);
                    songCurrentDuration.setText(getTimeString(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        seekBarprocess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo(i * 1000);
                    songCurrentDuration.setText(getTimeString(i * 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setUpMusicPlayerView(rootView);

        return rootView;
    }

    private void setImageView(View rootView) {
        //ImageView
        byte[] thumbnail = musicInformation.getThumbnail();
        if (thumbnail != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
            imageView.setImageBitmap(bitmap);
        } else {
            Picasso.with(getActivity()).load(R.drawable.musical_note).into(imageView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((PlaylistActivity) getActivity()).updatePlayButton(((PlaylistActivity) getActivity()).getMusicPlayingButton());
    }

    private void setUpMusicPlayerView(View view){
        musicInformation = ((PlaylistActivity) getActivity()).getMusicInformation();
        songNameView.setText(musicInformation.getTitle());
        artistNameView.setText(musicInformation.getArtist());
        setImageView(view);
        seekBarprocess.setMax(musicInformation.getDuration() / 1000);
        songTotalDuration.setText(getTimeString(musicInformation.getDuration()));
    }

    private String getTimeString(Integer millisec) {
        int min = (millisec / 1000) / 60;
        int sec = (millisec / 1000) % 60;
        return String.format("%02d:%02d", min, sec);
    }


}
