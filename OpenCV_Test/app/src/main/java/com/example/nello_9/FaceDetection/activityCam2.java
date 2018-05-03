package com.example.nello_9.FaceDetection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.example.nello_9.FaceDetection.R;

import org.opencv.android.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class activityCam2 extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    public String mCameraId="1";

    private static final String TAG = "vedo2";
    int a,b,c,d;
    boolean contr=false, k=false;
    CascadeClassifier cascadeClassifier;
    CascadeClassifier cascadeClassifierDESTRO;
    CascadeClassifier cascadeClassifierSINISTRO;
    private int absoluteFaceSize, sizeSin,contatore=-2;

    private CameraBridgeViewBase   mOpenCvCameraView;
    Mat image_output, occdx, occsx, perioculare;
    Rect rectCrop;
    Mat mRgba;
    Mat grey;
    Rect[] destroArray, sinistroArray,facesArray;
    MatOfRect faces ,occhioSinistro, occhioDestro;
    private BaseLoaderCallback mLoaderCallbackFront = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                {


                    perioculare= new Mat();
                    rectCrop= new Rect();
                    image_output=new Mat();
                    occdx= new Mat();
                    occsx= new Mat();

                    faces = new MatOfRect();
                    occhioSinistro= new MatOfRect();
                    occhioDestro= new MatOfRect();
                    Log.i(TAG, "OpenCV loaded successfullyTTT");
                    initializeOpenCVDependenciesFront();


                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.frontcam);
        Log.i(TAG,"primamcamera: "+mCameraId);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.riquadro);

        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        Log.i(TAG,"abilito frontale");
        mOpenCvCameraView.enableView();

        Log.i(TAG,"camera id= " +mCameraId);







    }



    private void initializeOpenCVDependenciesFront() {

        try {
            //----   RICONOSCIMENTO VISO   ----
            Log.i(TAG,"Riconoscimento viso front");
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();
            //----   RICONOSCIMENTO OCCHIO SINISTRO----
            Log.i(TAG,"Riconoscimento occsx front");
            InputStream issin = getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
            File cascadeDirsin = getDir("cascadesin", Context.MODE_PRIVATE);
            File mCascadeFilesin = new File(cascadeDirsin, "haarcascade_lefteye_2splits.xml");
            FileOutputStream ossin = new FileOutputStream(mCascadeFilesin);
            byte[] buffersin = new byte[4096];
            int bytesReadsin;
            while ((bytesReadsin = issin.read(buffersin)) != -1) {
                ossin.write(buffersin, 0, bytesReadsin);
            }
            issin.close();
            ossin.close();
            //----   RICONOSCIMENTO OCCHIO DESTRO----
            Log.i(TAG,"Riconoscimento occdx front");
            InputStream isdes = getResources().openRawResource(R.raw.haarcascade_righteye_2splits);
            File cascadeDirdes = getDir("cascadedes", Context.MODE_PRIVATE);
            File mCascadeFiledes = new File(cascadeDirdes, "haarcascade_righteye_2splits.xml");
            FileOutputStream osdes = new FileOutputStream(mCascadeFiledes);
            byte[] bufferdes = new byte[4096];
            int bytesReaddes;
            while ((bytesReaddes= isdes.read(bufferdes)) != -1) {
                osdes.write(bufferdes, 0, bytesReaddes);
            }
            isdes.close();
            osdes.close();
            // carico cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            Log.i(TAG,"primo cascade");
            cascadeClassifierSINISTRO = new CascadeClassifier(mCascadeFilesin.getAbsolutePath());
            Log.i(TAG,"secondo cascade");
            cascadeClassifierDESTRO = new CascadeClassifier(mCascadeFiledes.getAbsolutePath());
            Log.i(TAG,"terzo cascade");


        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

    }
    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        contatore=-2;

        if (!OpenCVLoader.initDebug()) {
            Log.i(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallbackFront);
        } else {
            Log.i(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallbackFront.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();

    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.i(TAG, "Camera attivata");
        grey = new Mat();

    }

    @Override
    public void onCameraViewStopped() {

    }
    public void stop(){
        if (contr){
            Log.i(TAG, "Sono nel contatore e vale= " +contatore);
            ArrayList <String> fileSalvati= new ArrayList<>();
            fileSalvati=SaveImage();
            String ok=fileSalvati.get(0);
            String sx=fileSalvati.get(1);
            String dx=fileSalvati.get(2);
            String ottimo=fileSalvati.get(3);
            Intent i = new Intent(this,MainActivity.class);
            i.putExtra("fotografia", ok);
            i.putExtra("sin",sx);
            i.putExtra("des",dx);
            i.putExtra("perioculare",ottimo);
            startActivity(i);}
        else{
            Log.i(TAG,"Occhi non trovati");
            Intent i= new Intent (this,Errore.class);
            startActivity(i);
        }
    }
  @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
      Log.i(TAG,"sono nel frame frontale");



      if (contatore>0 && !k) {
          k=true;
          Log.i(TAG, "k: "+k);
          stop();
          return null;

      }
      else{

          mRgba = inputFrame.rgba();
          Core.flip(mRgba, mRgba, 1);
          absoluteFaceSize = (int) (mRgba.rows()* 0.5);
          sizeSin=(int) (absoluteFaceSize*0.2);

          if (cascadeClassifier != null) {
              cascadeClassifier.detectMultiScale(mRgba, faces, 1.2, 1, 2,
                      new Size(absoluteFaceSize, absoluteFaceSize), new Size());

          }
          facesArray = faces.toArray();

          for (int i = 0; i <facesArray.length; i++) {
              Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 5);
              contatore++;
              image_output=mRgba.submat(facesArray[i]);
              perioculare=image_output.submat(image_output.rows()/4,image_output.rows()/2,0,image_output.cols());

          }
          grey= perioculare.clone();
          if (cascadeClassifierSINISTRO != null) {
              cascadeClassifierSINISTRO.detectMultiScale(grey, occhioSinistro, 1.15, 1,
                      Objdetect.CASCADE_FIND_BIGGEST_OBJECT| Objdetect.CASCADE_SCALE_IMAGE,new Size(sizeSin, sizeSin), new Size());


          }

          sinistroArray = occhioSinistro.toArray();
          if (cascadeClassifierDESTRO != null) {


              cascadeClassifierDESTRO.detectMultiScale(grey, occhioDestro, 1.15, 1,
                      Objdetect.CASCADE_FIND_BIGGEST_OBJECT| Objdetect.CASCADE_SCALE_IMAGE,new Size(sizeSin, sizeSin), new Size());
          }
          destroArray = occhioDestro.toArray();


          Log.i(TAG, "Contatore è: "+contatore);

          for (int i = 0; i <sinistroArray.length; i++) {
              Imgproc.rectangle(grey, sinistroArray[i].tl(), sinistroArray[i].br(), new Scalar(0, 0, 0, 0), 2);
              occsx=grey.submat(sinistroArray[i]);
              a=sinistroArray[i].y;
              b=a+sinistroArray[i].height;
              contr=true;

          }
          Log.i(TAG,"lunghezza facciaarray: "+facesArray.length);

          for (int i = 0; i <destroArray.length; i++) {
              Imgproc.rectangle(grey, destroArray[i].tl(), destroArray[i].br(), new Scalar(0, 0, 0, 0), 2);
              occdx=grey.submat(destroArray[i]);
              c= destroArray[i].x;
              d= c+destroArray[i].width;
              contr=true;
          }

          return mRgba;
      }


    }

    public ArrayList<String> SaveImage () {
        //------- PROVIAMO PORTRAIT -----
        if(!contr){

            Log.i(TAG,"STO PROVANDO il portrait");
            Core.flip(image_output.t(), image_output, 1);
            perioculare=image_output.submat(image_output.rows()/4,image_output.rows()/2,0,image_output.cols());
            grey= perioculare.clone();
            if (cascadeClassifierSINISTRO != null) {
                cascadeClassifierSINISTRO.detectMultiScale(grey, occhioSinistro, 1.15, 1,
                        Objdetect.CASCADE_FIND_BIGGEST_OBJECT| Objdetect.CASCADE_SCALE_IMAGE,new Size(30, 30), new Size());


            }

            sinistroArray = occhioSinistro.toArray();
            if (cascadeClassifierDESTRO != null) {


                cascadeClassifierDESTRO.detectMultiScale(grey, occhioDestro, 1.15, 1,
                        Objdetect.CASCADE_FIND_BIGGEST_OBJECT| Objdetect.CASCADE_SCALE_IMAGE,new Size(30, 30), new Size());
            }
            destroArray = occhioDestro.toArray();
            for (int i = 0; i <sinistroArray.length; i++) {

                Imgproc.rectangle(grey, sinistroArray[i].tl(), sinistroArray[i].br(), new Scalar(0, 0, 0, 0), 2);
                occsx=grey.submat(sinistroArray[i]);
                a=sinistroArray[i].y;
                b=a+sinistroArray[i].height;
                contr=true;
            }
            Log.i(TAG,"lunghezza facciaarray: "+facesArray.length);

            for (int i = 0; i <destroArray.length; i++) {
                Imgproc.rectangle(grey, destroArray[i].tl(), destroArray[i].br(), new Scalar(0, 0, 0, 0), 2);
                occdx=grey.submat(destroArray[i]);
                c= destroArray[i].x;
                d= c+destroArray[i].width;
                contr=true;
            }



        }


        //---- FINE PROVA ---------


        ArrayList <String> fileSalvati= new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.i(TAG,"Richiedo permesso");
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    1);
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/FotoViso");
        if(!path.exists()){
            path.mkdir();
        }
        File patthesimo= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/FotoViso/tmp");
        if(!patthesimo.exists()){
            patthesimo.mkdir();
        }

        String filename = "tmp.bmp";
        String occsinistro="tmpsinistro.bmp";
        String occdestro="tmpdestro.bmp";
        String peric= "tmpperioculare.bmp";
        File file = new File(patthesimo, filename);
        File filedx = new File(patthesimo, occdestro);
        File filesx = new File(patthesimo, occsinistro);
        File per= new File(patthesimo, peric);
        Boolean bool,bool1,bool2,bool3;
        filename = file.toString();
        occsinistro=filesx.toString();
        occdestro=filedx.toString();
        peric=per.toString();
        bool = Imgcodecs.imwrite(filename, image_output);
        bool1= Imgcodecs.imwrite(occdestro, occdx);
        bool2= Imgcodecs.imwrite(occsinistro, occsx);
        bool3=Imgcodecs.imwrite(peric,perioculare);
        fileSalvati.add(0,filename);
        fileSalvati.add(1,occsinistro);
        fileSalvati.add(2,occdestro);
        fileSalvati.add(3,peric);
        if (bool == true)
            Log.i(TAG, "SUCCESS: "+bool);
        else
            Log.i(TAG, "Fail: "+bool);
        if (bool1 == true)
            Log.i(TAG, "SUCCESS occhiodx: "+bool1);
        else
            Log.i(TAG, "Fail occhiodx: "+bool1);
        if (bool2 == true)
            Log.i(TAG, "SUCCESS occhiosx: "+bool2);
        else
            Log.i(TAG, "Fail occhiosx: "+bool2);


        return fileSalvati;
    }



}