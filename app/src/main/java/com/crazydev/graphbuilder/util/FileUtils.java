package com.crazydev.graphbuilder.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readTextFileFromResource (Context context, int resId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream iS = context.getResources().openRawResource(resId);
            InputStreamReader iSR = new InputStreamReader(iS);
            BufferedReader bR = new BufferedReader(iSR);
            String nextLine;

            while ((nextLine = bR.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return body.toString();
    }
}
