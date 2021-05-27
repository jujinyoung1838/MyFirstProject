package com.example.firstproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CameraImage extends AppCompatActivity {
    ImageView CosmeticImage; //카메라로 찍은 화장품 사진
    File file;      //카메라 파일
    Uri fileUri;    //카메라 파일경로
    static TextView imageDetail;     //문자인식 후 값받기
    Bitmap rotatedBitmap;   //image 회전 후 bitmap
    static String message;  //이미지에서 추출한 message값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_image);
        //검색하기 버튼
        Button searchButton = findViewById(R.id.serachButton);
        //화장품 사진
        CosmeticImage = findViewById(R.id.CosmeticImage);

        //액티비티 이동되자마자 카메라 실행
        TakePicture();

        //region검색하기 버튼 클릭시 이벤트 발생
        Button serachButton = findViewById(R.id.serachButton);
        serachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //여기서 글자 분석 제품이름 추출하기
                callCloudVision(rotatedBitmap);
                searchButton.setText("검색 중");

                //검색 결과 인텐트로 이동
//                Intent intent = new Intent(getApplicationContext(), SearchResult.class);
//                startActivityForResult(intent,MainActivity.SEARCHRESULT_CODE);
            }
        });
        //endregion
    }

    //region 카메라 사용
    public void TakePicture() {
        try {
            if (file == null) {
                file = createFile();
            }else{
                file.delete();
            }

            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(this, "org.techtown.firstproject.fileprovider", file);
        }else{
            fileUri = Uri.fromFile(file);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //카메라 인텐트
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);   //사진 결과물을 fileUri에 저장
        //이 액티비티가 존재하면 실행
        if(intent.resolveActivity((getPackageManager())) != null){
            startActivityForResult(intent, MainActivity.CAMERA_CODE);        //실행
        }
    }

    //파일생성
    public File createFile(){
        String filename = "capture.jpg";

        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir,filename);

        return outFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case MainActivity.CAMERA_CODE:
                if(resultCode==RESULT_OK) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //비율 자르기
                    options.inSampleSize = 1;
                    //파일을 읽어들여서 bitmap객체로 만든다.
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    if (bitmap == null) {
                        Toast.makeText(this, "할당 안됨", Toast.LENGTH_LONG).show();

                    } else {
                        rotatedBitmap = rotateImage(bitmap);
                        scaleBitmapDown(rotatedBitmap,MainActivity.MAX_DIMENSION);
                        CosmeticImage.setImageBitmap(rotatedBitmap);
                    }
                }
                break;
        }
    }
    //이미지 회전
    public static Bitmap rotateImage(Bitmap source){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }

    //endregion

    //region 글자분석
    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(MainActivity.CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(MainActivity.ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(MainActivity.ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // 여기 수정해서 글자, 그림 등 인식
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(MainActivity.MAX_LABEL_RESULTS);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(MainActivity.TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<CameraImage> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(CameraImage activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(MainActivity.TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(MainActivity.TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "failed to make API request because of other IOException " + e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            CameraImage activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                //값을 받을 text값 설정
                imageDetail = activity.findViewById(R.id.image_details);
                imageDetail.setText(result);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, "failed to make API request because of other IOException " + e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        //message = new StringBuilder();

        //getTextAnnotation으로 변경
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            message = labels.get(0).getDescription();
        } else {
            message = "nothing";
        }

        return message;
    }

    //문자 두줄만 추출(제목)
    public String SplitText()
    {
        String[] splitmessage = message.split("\n");

        return splitmessage[0] + splitmessage[1];
    }

    //endregion
}