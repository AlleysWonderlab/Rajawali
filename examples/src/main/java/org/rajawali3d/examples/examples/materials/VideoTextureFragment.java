package org.rajawali3d.examples.examples.materials;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.HorizontalCamera;
import org.rajawali3d.debug.CoordinateTrident;
import org.rajawali3d.debug.DebugVisualizer;
import org.rajawali3d.debug.GridFloor;
import org.rajawali3d.examples.R;
import org.rajawali3d.examples.examples.AExampleFragment;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Sphere;

public class VideoTextureFragment extends AExampleFragment {

    @Override
    public AExampleRenderer createRenderer() {
        return new VideoTextureRenderer(getActivity(), this);
    }


    private final class VideoTextureRenderer extends AExampleRenderer implements View.OnTouchListener {
        private MediaPlayer mMediaPlayer;
        private StreamingTexture mVideoTexture;

        public VideoTextureRenderer(Context context, @Nullable AExampleFragment fragment) {
            super(context, fragment);
        }

        @Override
        protected void initScene() {
            PointLight pointLight = new PointLight();
            pointLight.setPower(1);
            pointLight.setPosition(-1, 1, 4);

            getCurrentScene().addLight(pointLight);
            getCurrentScene().setBackgroundColor(0xff040404);

            try {
                Object3D android = new Cube(2.0f);
                Material material = new Material();
                material.enableLighting(true);
                material.setDiffuseMethod(new DiffuseMethod.Lambert());
                material.setSpecularMethod(new SpecularMethod.Phong());
                android.setMaterial(material);
                android.setColor(0xff99C224);
                //getCurrentScene().addChild(android);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }

            mMediaPlayer = MediaPlayer.create(getContext(),
                    Uri.parse("https://v-2-alleys-co.s3.dualstack.ap-northeast-1.amazonaws.com/XQ/h_kFOVYe1OVSC6kR855w-navi.mp4"));
            mMediaPlayer.setLooping(true);

            mVideoTexture = new StreamingTexture("sintelTrailer", mMediaPlayer);
            Material material = new Material();
            material.setColorInfluence(1);
            try {
                material.addTexture(mVideoTexture);
            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }

            DebugVisualizer debugViz = new DebugVisualizer(this);
            debugViz.addChild(new GridFloor());
            debugViz.addChild(new CoordinateTrident());
            getCurrentScene().addChild(debugViz);

//			Plane screen = new Plane(3, 2, 2, 2, Vector3.Axis.Z);
            Sphere screen = new Sphere(4, 10, 10,
                    (float) ((float) 0 / 180 * Math.PI),
                    (float) ((float) 80 / 180 * Math.PI),
                    (float) ((float) 67.5 / 180 * Math.PI),
                    (float) ((float) 45 / 180 * Math.PI));

            screen.setColor(Color.BLACK);
            screen.setMaterial(material);
            getCurrentScene().addChild(screen);

            Material imgMaterial = new Material();
            try {
                Texture sphereMapTexture = new Texture("right", R.drawable.right);
                imgMaterial.addTexture(sphereMapTexture);

                Sphere screen2 = new Sphere(4, 10, 10,
                        (float) ((float) 90 / 180 * Math.PI),
                        (float) ((float) 80 / 180 * Math.PI),
                        (float) ((float) 67.5 / 180 * Math.PI),
                        (float) ((float) 45 / 180 * Math.PI));
                screen2.setColor(Color.BLACK);
                screen2.setMaterial(imgMaterial);
                getCurrentScene().addChild(screen2);


                imgMaterial = new Material();
                sphereMapTexture = new Texture("left", R.drawable.left);
                imgMaterial.addTexture(sphereMapTexture);
//
                Sphere screen3 = new Sphere(4, 10, 10,
                        (float) ((float) 270 / 180 * Math.PI),
                        (float) ((float) 80 / 180 * Math.PI),
                        (float) ((float) 67.5 / 180 * Math.PI),
                        (float) ((float) 45 / 180 * Math.PI));
                screen3.setColor(Color.BLACK);
                screen3.setMaterial(imgMaterial);
                getCurrentScene().addChild(screen3);

            } catch (ATexture.TextureException e) {
                e.printStackTrace();
            }


//            ChaseCamera chaseCamera = new ChaseCamera(new Vector3(0, 0, 0));
//            // -- tell the camera which object to chase
//            chaseCamera.setLinkedObject(screen);
//            chaseCamera.setCameraPitch(180);
//            // -- set the far plane to 1000 so that we actually see the sky sphere
//            chaseCamera.setFarPlane(1000);
//            getCurrentScene().replaceAndSwitchCamera(chaseCamera, 0);

//            final HorizontalCamera camera = new HorizontalCamera(this, );
//            getCurrentScene().replaceAndSwitchCamera(camera, 0);
////            camera.setRotation(Vector3.Y, 90 / 180 * Math.PI);
//            camera.setRotation(Vector3.Y, 225);

//            camera.setLookAt(360, 0, 0);
//            camera.setCameraYaw(30 / 180 * Math.PI);
//            camera.setCameraOrientation(new Quaternion(Vector3.Y, 60 / 180 * Math.PI));
//            camera.setCameraPitch(200 / 180 * Math.PI);
//            camera.setCameraRoll(200 / 180 * Math.PI);

//            getCurrentCamera().enableLookAt();
//            getCurrentCamera().setLookAt(0, 0, 0);
//
            HorizontalCamera arcball = new HorizontalCamera(mContext, ((Activity) mContext).findViewById(R.id.content_frame), screen);
            arcball.setPosition(0, 0, 0);
            arcball.setRotation(Vector3.Axis.Y, -130);
            getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), arcball);

//            // -- animate the spot light
//
//            TranslateAnimation3D lightAnim = new TranslateAnimation3D(
//                    new Vector3(-3, 3, 10), // from
//                    new Vector3(3, 1, 3)); // to
//            lightAnim.setDurationMilliseconds(5000);
//            lightAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
//            lightAnim.setTransformable3D(pointLight);
//            lightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
//            getCurrentScene().registerAnimation(lightAnim);
//            lightAnim.play();
//
            // -- animate the camera

//            EllipticalOrbitAnimation3D camAnim = new EllipticalOrbitAnimation3D(
//                    new Vector3(3, 2, 10), new Vector3(1, 0, 8), 0, 359);
//            camAnim.setDurationMilliseconds(20000);
//            camAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
//            camAnim.setTransformable3D(getCurrentCamera());
//            getCurrentScene().registerAnimation(camAnim);
//            camAnim.play();

            mMediaPlayer.start();
        }

        @Override
        protected void onRender(long ellapsedRealtime, double deltaTime) {
            super.onRender(ellapsedRealtime, deltaTime);
            mVideoTexture.update();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mMediaPlayer != null)
                mMediaPlayer.pause();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mMediaPlayer != null)
                mMediaPlayer.start();
        }

        @Override
        public void onRenderSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            super.onRenderSurfaceDestroyed(surfaceTexture);
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }

}
