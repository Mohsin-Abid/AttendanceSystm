package com.geo.attendancesystm.activities;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.attendancesystm.Interfaces.ClickListenerInterface;
import com.geo.attendancesystm.Interfaces.SimilarityClassifier;
import com.geo.attendancesystm.R;
import com.geo.attendancesystm.adapters.LectureAdapter;
import com.geo.attendancesystm.model.classes.pojo.Attendance;
import com.geo.attendancesystm.model.classes.pojo.Lectures;
import com.geo.attendancesystm.model.classes.pojo.StudentInfo;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAttendance extends AppCompatActivity implements ClickListenerInterface {
    private static final String TAG = "AddAttendance";
    float[][] embeddings;
    float distance= 1.0f;
    PrefManager prefManager;
    Button recognizeBtn;
    PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    ProcessCameraProvider cameraProvider;
    CameraSelector cameraSelector;
    int cam_face=CameraSelector.LENS_FACING_FRONT;
    FaceDetector detector;
    boolean start=true,flipX=false;
    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    float[][] outputEmbeddings;
    int OUTPUT_SIZE=192; //Output size of model
    Interpreter tfLite;
    String modelFile="mobile_face_net.tflite"; //model name
    TextView status;
    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>(); //saved Faces
    List<Lectures> list;
    LectureAdapter adp;
    RecyclerView lecturesRecycler;
    String lecture_id;
    int faceCount =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);
        init();
        loadModel();
        mlKitInit();
        fetchLectures();
        if (embeddings == null){
            jsonToArray(prefManager.getFace());
        }
        else {
          fetchFace();
        }
    }
    void init(){
        prefManager = new PrefManager(AddAttendance.this);
        recognizeBtn = findViewById(R.id.recognizeBtn);
        previewView=findViewById(R.id.previewView);
        status = findViewById(R.id.status);
        lecturesRecycler = findViewById(R.id.lecturesRecycler);
        lecturesRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
    void fetchLectures()
    {
        list = new ArrayList();
        Call<List<Lectures>> call = null;
        if (prefManager.getUserType().equals("student")){
            call = RetrofitClient.getInstance().getApi().get_lectures_students(prefManager.getUserId());
        }
        else if (prefManager.getUserType().equals("teacher")){
            call = RetrofitClient.getInstance().getApi().get_lectures_teachers(prefManager.getUserId());
        }
        call.enqueue(new Callback<List<Lectures>>() {
            @Override
            public void onResponse(Call<List<Lectures>> call, Response<List<Lectures>> response)
            {
                if(response.isSuccessful())
                {
                    list = response.body();
                    Log.d(TAG, "onResponse: "+new Gson().toJson(list));
                    if (!list.get(0).getResult().equals("0"))
                    {
                        adp = new LectureAdapter(AddAttendance.this,list,AddAttendance.this);
                        lecturesRecycler.setAdapter(adp);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Lectures>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
                Toast.makeText(AddAttendance.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void fetchFace(){
        Call<StudentInfo> call = null;
        if (prefManager.getUserType().equals("student")){
            call = RetrofitClient.getInstance().getApi().getStudent(prefManager.getUserId());
        }
        else if (prefManager.getUserType().equals("teacher")){
            call = RetrofitClient.getInstance().getApi().getTeacher(prefManager.getUserId());
        }
        call.enqueue(new Callback<StudentInfo>() {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful()){
                    if (response.body().result.equals("1")){
                        jsonToArray(response.body().getFace_state());
                        prefManager.setFace(response.body().getFace_state());
                    }
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t) {
                Toast.makeText(AddAttendance.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }
    void jsonToArray(String json)
    {
        // separate and replace
        json = json.replace("[[", "");//replacing all [ to ""
        json = json.replace("]]", "");//replacing all ] to ""
        //StringTokenizer tokens = new StringTokenizer(json,",");
        String[] separated = json.split(",");
        embeddings = new float[1][192];
        // converting to float
        try {
            for (int i = 0; i < embeddings.length; i++) {
                for (int j = 0; j < 192; j++) {
                    embeddings[i][j] = Float.parseFloat(separated[j]);
                }
            }
        } catch (Exception e){
            Log.d(TAG, "stringToJson: "+e);
        }
        SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                prefManager.getUserId(), prefManager.getUserName(), -1f);
        result.setExtra(embeddings);
        registered.put(prefManager.getUserName(),result);
    } // end
    private List<Pair<String, Float>> findNearest(float[] emb)
    {
        List<Pair<String, Float>> neighbour_list = new ArrayList<Pair<String, Float>>();
        Pair<String, Float> ret = null; //to get closest match
        Pair<String, Float> prev_ret = null; //to get second closest match
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
        {
            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                prev_ret=ret;
                ret = new Pair<>(name, distance);
            }
        }
        if(prev_ret==null) prev_ret=ret;
        neighbour_list.add(ret);
        neighbour_list.add(prev_ret);
        return neighbour_list;
    }
    private void cameraBind()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);


        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cam_face)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
                        .build();

        Executor executor = Executors.newSingleThreadExecutor();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                try {
                    Thread.sleep(500);  //Camera preview refreshed every 1000 millisec(adjust as required)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                InputImage image = null;

                // Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)

                @SuppressLint("UnsafeOptInUsageError")
                Image mediaImage = imageProxy.getImage();

                if (mediaImage != null) {
                    image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//
                }
                // Process acquired image to detect faces
                Task<List<Face>> result =
                        detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {

                                                if(faces.size()!=0) {

                                                    Face face = faces.get(0); //Get first face from detected faces
//                                                    System.out.println(face);

                                                    //mediaImage to Bitmap
                                                    Bitmap frame_bmp = toBitmap(mediaImage);

                                                    int rot = imageProxy.getImageInfo().getRotationDegrees();

                                                    //Adjust orientation of Face
                                                    Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false);

                                                    //Get bounding box of face
                                                    RectF boundingBox = new RectF(face.getBoundingBox());
                                                    //Crop out bounding box from whole Bitmap(image)
                                                    Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                                    if(flipX)
                                                        cropped_face = rotateBitmap(cropped_face, 0, flipX, false);
                                                    //Scale the acquired Face to 112*112 which is required input for model
                                                    Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                                                    if(start)
                                                        recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
                                                }
                                                else
                                                {
                                                    status.setText("No Face Detected");
                                                }

                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        })
                                .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Face>> task) {
                                        imageProxy.close(); //v.important to acquire next frame for analysis
                                    }
                                });
            }
        });
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    } // bind preview wnd
    public void recognizeImage(final Bitmap bitmap) {

        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};
        Map<Integer, Object> outputMap = new HashMap<>();

        outputEmbeddings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable

        outputMap.put(0, outputEmbeddings);
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model
        float distance_local = Float.MAX_VALUE;
        String id = "0";
        String label = "?";
        if (registered.size() > 0) {
            final List<Pair<String, Float>> nearest = findNearest(outputEmbeddings[0]);//Find 2 closest matching face

            if (nearest.get(0) != null) {
                final String name = nearest.get(0).first; //get name and distance of closest matching face
                // label = name;
                distance_local = nearest.get(0).second;

                if(distance_local<distance && name.equals(prefManager.getUserName())){
                    faceCount++;
                    if (faceCount>4){
                        addAttendance(prefManager.getUserId(),prefManager.getUserType(),lecture_id);
                    }

                } //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                else{
                    status.setText("Unknown");
                }
            }
        }
    }
    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);


        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }
    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    void loadModel(){
        try {
            tfLite=new Interpreter(loadModelFile(AddAttendance.this,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    void mlKitInit(){
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);
    }


    @Override
    public void oniItemClick(int position)
    {
        lecture_id = list.get(position).getId();
        if (list.get(position).getMarked()){
            Toast.makeText(this, "Already Marked", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (checkValidTime(list.get(position).getStarttime(),list.get(position).getEndtime()))
            {
                previewView.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
                lecturesRecycler.setVisibility(View.INVISIBLE);
                cameraBind();
            }
            else
            {
                Toast.makeText(this, "Time not matched", Toast.LENGTH_SHORT).show();
            }
        }
    }
    boolean checkValidTime(String startTime, String endTime)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String str = sdf.format(new Date());
        String[] sTime = startTime.split(":");
        String[] eTime = endTime.split(":");
        int nStartTimeHours = Integer.parseInt(sTime[0]); // start time hours
        int nStartTimeMin = Integer.parseInt(sTime[1]); //  start time mins
        int nEndTimeHours = Integer.parseInt(eTime[0]); // end time hours
        int nEndTimeMin= Integer.parseInt(eTime[1]); // end time mins
        String[] a = str.split(":");
        int hours = Integer.parseInt(a[0]); // current hours
        int minutes = Integer.parseInt(a[1]); // current min
        Calendar csTime = Calendar.getInstance();
        csTime.set(HOUR_OF_DAY,nStartTimeHours);
        csTime.set(MINUTE,nStartTimeMin);
        Calendar ceTime = Calendar.getInstance();
        ceTime.set(HOUR_OF_DAY,nEndTimeHours);
        ceTime.set(MINUTE,nEndTimeMin);
        Calendar cTime = Calendar.getInstance();
        cTime.set(HOUR_OF_DAY,hours);
        cTime.set(MINUTE,minutes);
        long sTimeMilli = csTime.getTimeInMillis();
        long eTimeMilli = ceTime.getTimeInMillis();
        long cTimeMilli = cTime.getTimeInMillis();
        if (cTimeMilli >= sTimeMilli && cTimeMilli <=eTimeMilli){
            return true;
        }
        return false;
    }
    void addAttendance(String user_id,String status,String lecture_id){
        Call<Attendance> call =RetrofitClient.getInstance().getApi().update_attendance(user_id,status,lecture_id);
        call.enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                if (response.isSuccessful()){
                    if (response.body().getResult().equals("success")){
                        Toast.makeText(AddAttendance.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
                Toast.makeText(AddAttendance.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}