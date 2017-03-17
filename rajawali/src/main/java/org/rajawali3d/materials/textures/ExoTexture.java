package org.rajawali3d.materials.textures;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.view.Surface;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class ExoTexture extends ATexture {

    public interface ISurfaceListener {
        void setSurface(Surface surface);
    }

    private final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    private SimpleExoPlayer exoPlayer;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    public ExoTexture(ExoTexture other) {
        super(other);
    }


    public ExoTexture(String textureName, SimpleExoPlayer exoPlayer) {
        super(TextureType.VIDEO_TEXTURE, textureName);
        this.exoPlayer = exoPlayer;
        setGLTextureType(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
    }

    public ExoTexture clone() {
        return new ExoTexture(this);
    }


    void add() throws TextureException {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        setTextureId(textureId);
        mSurfaceTexture = new SurfaceTexture(textureId);
        if (exoPlayer != null) {
            mSurface = new Surface(mSurfaceTexture);
            exoPlayer.setVideoSurface(mSurface);
        }
    }

    void remove() throws TextureException {
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
        mSurfaceTexture.release();
    }

    void replace() throws TextureException {
        return;
    }

    void reset() throws TextureException {
        mSurfaceTexture.release();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void update() {
        if (mSurfaceTexture != null)
            mSurfaceTexture.updateTexImage();
    }

    public void updateSimpleExoPlayer(SimpleExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
        exoPlayer.setVideoSurface(mSurface);
    }
}