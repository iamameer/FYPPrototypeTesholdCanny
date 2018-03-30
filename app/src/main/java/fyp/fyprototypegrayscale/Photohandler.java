package fyp.fyprototypegrayscale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Photohandler implements PictureCallback{
    private final Context context;
    public final static String DEBUG_TAG = "MakePhotoActivity";

    public Photohandler(Context context) {
        this.context = context;
    }


    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(MainActivity.DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" /*+ date*/ + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bytes);
            fos.close();
            Toast.makeText(context, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
            Log.d(DEBUG_TAG,"filename: "+filename);
        } catch (Exception error) {
            Log.d(MainActivity.DEBUG_TAG, "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }

        //GRAY PART
        try{
            //read first
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            //convert to gray
            Mat omat = new Mat(bmp.getWidth(),bmp.getHeight(), CvType.CV_8UC1);
            Mat gmat = new Mat(bmp.getWidth(),bmp.getHeight(), CvType.CV_8UC1);
            Utils.bitmapToMat(bmp,omat);

            Imgproc.cvtColor(omat,gmat,Imgproc.COLOR_RGB2GRAY);

            //save gray
            String path = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/";
            File file = new File(path, "Picture.jpg");
            Log.d(DEBUG_TAG,"saving: "+file.getAbsolutePath());

            Imgcodecs.imwrite(file.getAbsolutePath(),gmat);

            //THRESHOLD PART
            //read first
            String pathTres = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/";
            File fileTres = new File(pathTres, "Picture.jpg");
            Bitmap bmpTres = BitmapFactory.decodeFile(fileTres.getAbsolutePath());

            Mat tmat = new Mat(bmpTres.getWidth(),bmpTres.getHeight(),CvType.CV_8UC1);
            Mat nmat = new Mat(bmpTres.getWidth(),bmpTres.getHeight(),CvType.CV_8UC1);
            Utils.bitmapToMat(bmpTres,tmat);

            Imgproc.threshold(tmat,nmat,0,255,Imgproc.THRESH_BINARY);

            //save gray
            String path4 = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/";
            File file3 = new File(path4, "PictureTres.jpg");
            Log.d(DEBUG_TAG,"saving: "+file3.getAbsolutePath());

            Imgcodecs.imwrite(file3.getAbsolutePath(),nmat);

            //CANNY PARTTT
            String pathCanny = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/";
            File fileCanny = new File(pathCanny, "PictureTres.jpg");
            Bitmap bmpCanny = BitmapFactory.decodeFile(fileCanny.getAbsolutePath());

            Mat trmat = new Mat(bmpCanny.getWidth(),bmpCanny.getHeight(),CvType.CV_8UC1);
            Mat cmat = new Mat(bmpCanny.getWidth(),bmpCanny.getHeight(),CvType.CV_8UC1);
            Utils.bitmapToMat(bmpCanny,trmat);

            Imgproc.Canny(trmat,cmat,60,60*3);

            //save can
            String path5 = Environment.getExternalStorageDirectory()+"/Pictures/CameraAPIDemo/";
            File file4 = new File(path5, "PictureCan.jpg");
            Log.d(DEBUG_TAG,"saving: "+file4.getAbsolutePath());

            Imgcodecs.imwrite(file4.getAbsolutePath(),cmat);

        }catch (Exception e){
            Log.d(DEBUG_TAG,"err: "+e.toString());
        }
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }
}
