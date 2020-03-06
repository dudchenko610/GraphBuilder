package com.crazydev.graphbuilder.io;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class IOManager {

    private AssetManager assetManager;

    public IOManager (Context context) {
        this.assetManager = context.getAssets();
    }

    public InputStream readAsset(String assetFileName) throws IOException {
        return this.assetManager.open(assetFileName);
    }
}
