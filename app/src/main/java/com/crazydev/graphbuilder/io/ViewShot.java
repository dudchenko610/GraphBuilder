package com.crazydev.graphbuilder.io;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLException;
import android.os.Environment;

import androidx.core.app.NotificationCompat;

import com.crazydev.graphbuilder.R;
import com.crazydev.graphbuilder.rendering.OpenGLRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

public class ViewShot {

    final static String DIR_SD = "FunctionAnalizer";

    private static String lastName;
    private static int c = 0;

    public static void makeGLSurfaceViewShot(final OpenGLRenderer openGLRenderer, final GL10 gl, final Context context) throws Exception {

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        String fileName = format.format(d);
        fileName = fileName.replace(".", "_");
        fileName += ".png";

        if (fileName == lastName) {
            return;
        }

        lastName = fileName;

        final String fN = fileName;
        final int width  = openGLRenderer.getWidth();
        final int height = openGLRenderer.getHeight();

        final int bitmapBuffer[] = new int[width * height];
        final int bitmapSource[] = new int[width * height];

        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);

        } catch (GLException e) {
            return;
        }

        new Thread() {

            @Override
            public void run() {
                try {
                    ViewShot.makeShot(bitmapBuffer, bitmapSource, width, height, fN, context);
                } catch (Exception e) {

                }
            }

        }.start();


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)

                        .setSmallIcon(R.drawable.photologo2)
                        .setContentTitle("GraphBuilder&Analyzer")
                        .setContentText(context.getString(R.string.nsscr) + " " + (c + 1) + " " + context.getString(R.string.stg));

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(c ++, notification);

    }

    private static void makeShot(int bitmapBuffer[], int bitmapSource[], int width, int height, String fileName, Context context) throws Exception {

        int offset1, offset2;
        for (int i = 0; i < height; i++) {
            offset1 = i * width;
            offset2 = (height- i - 1) * width;

            for (int j = 0; j < width; j++) {
                int texturePixel = bitmapBuffer[offset1 + j];
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;
                bitmapSource[offset2 + j] = pixel;
            }

        }

        Bitmap b =  Bitmap.createBitmap(bitmapSource, width, height, Bitmap.Config.ARGB_8888);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new Exception("SD-карта не доступна: " + Environment.getExternalStorageState());
        }

      /*  File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        sdPath.mkdirs();*/

        File sdPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Screenshots");

        File sdFile = new File(sdPath, fileName);
        sdFile.createNewFile();

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(sdFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);

            out.flush();
            out.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(sdFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

        } catch (IOException e) {
            throw new Exception("Error");

        } finally {
            try {
                out.close();
            } catch (IOException e2) {
                throw new Exception("Error");
            }
        }
    }

}
