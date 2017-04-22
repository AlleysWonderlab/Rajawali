package org.rajawali3d.examples.examples.materials;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.cameras.HorizontalCamera;
import org.rajawali3d.debug.CoordinateTrident;
import org.rajawali3d.debug.DebugVisualizer;
import org.rajawali3d.debug.GridFloor;
import org.rajawali3d.examples.DemoApplication;
import org.rajawali3d.examples.R;
import org.rajawali3d.examples.examples.AExampleFragment;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.ExoTexture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;

import java.util.ArrayList;

public class ExoTextureFragment extends AExampleFragment implements View.OnTouchListener {
    DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((View) mRenderSurface).setOnTouchListener(this);
        return mLayout;
    }

    @Override
    public AExampleRenderer createRenderer() {
        ArrayList<SimpleExoPlayer> exoPlayers = new ArrayList<>();
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        for (int i = 0; i < 3; i++) {
            SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),
                    trackSelector,
                    new DefaultLoadControl(),
                    null, 1);
            Uri uri = null;
            int time = 0;
            switch (i) {
                case 0: {
                    uri = Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                    exoPlayer.setPlayWhenReady(true);
                    break;
                }
            }
            MediaSource mediaSource = new ExtractorMediaSource(uri,
                    buildDataSourceFactory(false),
                    new DefaultExtractorsFactory(),
                    new Handler(),
                    null);


            exoPlayer.prepare(mediaSource);
            exoPlayer.seekTo(time);

            exoPlayers.add(exoPlayer);

        }


        return new ExoTextureRenderer(getActivity(),
                exoPlayers,
                this);
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((DemoApplication) getContext().getApplicationContext())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ((ExoTextureRenderer) mRenderer).getObjectAt(event.getX(), event.getY());

        }

        return getActivity().onTouchEvent(event);
    }

    private final class ExoTextureRenderer extends AExampleRenderer implements OnObjectPickedListener {

        private ArrayList<SimpleExoPlayer> exoPlayers;
        private ArrayList<ExoTexture> exoTextures = new ArrayList<>();
        private ObjectColorPicker mPicker;

        public ExoTextureRenderer(Context context,
                                  ArrayList<SimpleExoPlayer> exoPlayers,
                                  @Nullable AExampleFragment fragment) {
            super(context, fragment);
            this.exoPlayers = exoPlayers;
        }

        @Override
        protected void initScene() {
            mPicker = new ObjectColorPicker(this);
            mPicker.setOnObjectPickedListener(this);
            PointLight pointLight = new PointLight();
            pointLight.setPower(1);
            pointLight.setPosition(-1, 1, 4);

            getCurrentScene().addLight(pointLight);
            getCurrentScene().setBackgroundColor(0xff040404);

            for (int i = 0; i < exoPlayers.size(); i++) {
                SimpleExoPlayer exoPlayer = exoPlayers.get(i);
                exoTextures.add(new ExoTexture("exo_" + i, exoPlayer));
                Material material = new Material();
                material.setColorInfluence(0);
                try {
                    material.addTexture(exoTextures.get(exoTextures.size() - 1));
                } catch (ATexture.TextureException e) {
                    e.printStackTrace();
                }
                Sphere screen = null;
                switch (i) {
                    case 0: {
                        screen = new Sphere(5f, 10, 10,
                                (float) ((float) 50 / 180 * Math.PI),
                                (float) ((float) 80 / 180 * Math.PI),
                                (float) ((float) 67.5 / 180 * Math.PI),
                                (float) ((float) 45 / 180 * Math.PI));
                        break;
                    }
                }
                if (screen != null) {
                    screen.setMaterial(material);
                    getCurrentScene().addChild(screen);
                    mPicker.registerObject(screen);
                }
            }
            
            DebugVisualizer debugViz = new DebugVisualizer(this);
            debugViz.addChild(new GridFloor(20, 0x555555, 1, 20));
            debugViz.addChild(new CoordinateTrident());
            getCurrentScene().addChild(debugViz);

            HorizontalCamera camera = new HorizontalCamera(mContext, ((Activity) mContext).findViewById(R.id.content_frame));
            camera.setPosition(0.0, 0.0, -2.4);
//            camera.setRotation(Vector3.Axis.Y, -130.0);
            getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), camera);
        }

        public void animCamera(int rotate) {
            // -- animate the camera
            RotateOnAxisAnimation anim = new RotateOnAxisAnimation(Vector3.Axis.Y, rotate);
            anim.setTransformable3D(getCurrentCamera());
            getCurrentScene().registerAnimation(anim);
            anim.play();
        }

        public void getObjectAt(float x, float y) {
            mPicker.getObjectAt(x, y);
        }

        @Override
        protected void onRender(long ellapsedRealtime, double deltaTime) {
            super.onRender(ellapsedRealtime, deltaTime);
            for (ExoTexture exoTexture : exoTextures) {
                exoTexture.update();
            }
        }

        public void onObjectPicked(@NonNull Object3D object) {
            for (ATexture aTexture : object.getMaterial().getTextureList()) {
//                switch (aTexture.getTextureName()) {
//                    case "exo_0": {
//                        System.out.println("exo_0");
//                        animCamera(-90);
//                        playOrPause(exoPlayers.get(0));
//                        playOrPause(exoPlayers.get(1));
//                        break;
//                    }
//                    case "exo_1": {
//                        System.out.println("exo_1");
//                        animCamera(180);
//                        playOrPause(exoPlayers.get(1));
//                        playOrPause(exoPlayers.get(2));
//                        break;
//                    }
//                    case "exo_2": {
//                        System.out.println("exo_2");
//                        animCamera(-90);
//                        playOrPause(exoPlayers.get(2));
//                        playOrPause(exoPlayers.get(0));
//                        break;
//                    }
//                }
            }

//            System.out.println(object.getMaterial().getTextureList().get(0).getTextureId());
//            object.setZ(object.getZ() == 0 ? -2 : 0);
        }

        private void playOrPause(SimpleExoPlayer exoPlayer) {
            if (exoPlayer.getPlaybackState() == 3) {
                if (exoPlayer.getPlayWhenReady())
                    exoPlayer.setPlayWhenReady(false);
                else
                    exoPlayer.setPlayWhenReady(true);
            }
        }

        @Override
        public void onNoObjectPicked() {
            RajLog.w("No object picked!");
        }
    }
}
