package com.example.gravitypaint;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ImageSaver {

    public static String generateImageFileName(String root, Bitmap.CompressFormat format){
        return root + "/image-" + new Date()+ "." +
                ((format == Bitmap.CompressFormat.PNG)/*Есди уловие верно то*/ ? "png" : /* Иначе*/ "jpeg");

    }
    public static void saveImage(Bitmap image, Bitmap.CompressFormat format, int quality,
                                 String outputFileName) throws IOException {
        File f = new File(outputFileName);
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);

        image.compress(format, quality, fos);

        fos.flush();
        fos.close();
    }
}