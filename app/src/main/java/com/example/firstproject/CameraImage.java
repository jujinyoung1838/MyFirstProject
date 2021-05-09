package com.example.firstproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CameraImage extends AppCompatActivity {
    ImageView CosmeticImage; //카메라로 찍은 화장품 사진
    File file;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_image);

        //화장품 사진
        CosmeticImage = findViewById(R.id.CosmeticImage);

        //intent 이동되자마자 카메라 실행
        takePicture();

        //region검색하기 버튼 클릭시 이벤트 발생
        Button serachButton = findViewById(R.id.serachButton);
        serachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //여기서 이미지 분석 시작 하기.


                //검색 결과 인텐트로 이동
                Intent intent = new Intent(getApplicationContext(), SearchResult.class);
                //이전에 있던 액티비티 재호출시 그 위로는 다날림
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,MainActivity.SEARCHRESULT_CODE);
            }
        });
        //endregion
    }

    //region 카메라 사용
    public void takePicture() {
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
                        Bitmap rotatedBitmap;
                        rotatedBitmap = rotateImage(bitmap);
                        CosmeticImage.setImageBitmap(rotatedBitmap);
                    }
                }
                break;
        }
//        else {
//            Toast.makeText(this,"사진 없음",Toast.LENGTH_LONG).show();
//        }
    }
    //이미지 회전
    public static Bitmap rotateImage(Bitmap source){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }

    //endregion
}