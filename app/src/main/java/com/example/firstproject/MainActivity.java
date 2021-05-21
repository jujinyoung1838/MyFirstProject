package com.example.firstproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    //region Intent간 이동하는 requestCode값
    public static final int MAINAVTIVITY_CODE = 0;  //홈화면으로 이동하는 requestcode값
    public static final int SURVEY_CODE = 1;        //피부 타입 등록으로 이동하는 requestcode값
    public static final int CAMERA_CODE = 2;        //카메라 사용 requestcode값
    public static final int CAMERAIMAGE_CODE = 3;   //사진으로 검색 화면 이동 requestcode값
    public static final int SEARCHRESULT_CODE = 4;  //검색결과 화면 이동 requestCode값
    public static final int MAINCONTENT_CODE = 5;   //상세정보창 화면 이동 requestCode값
    //endregion

    //region 구글 API 비전 사용
    static final String CLOUD_VISION_API_KEY = "AIzaSyDK3FWEVo89Z7RbgbwKn_aaE5vqe9H6gvA";
    static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    static final int MAX_LABEL_RESULTS = 10;
    static final int MAX_DIMENSION = 1200;
    static final String TAG = MainActivity.class.getSimpleName();
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //필요한 위험권환 자동 부여
        AutoPermissions.Companion.loadAllPermissions(this,CAMERA_CODE);

        //region 내 피부 타입 등록하기 버튼 클릭시 발생 이벤트
        ImageButton skinButton = findViewById(R.id.skinType);
        skinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Survey.class);
                startActivityForResult(intent,SURVEY_CODE);         //실행
            }
        });
        //endregion

        //region 사진으로 검색 버튼 클릭시 발생 이벤트
        ImageView cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraImage.class);
                startActivityForResult(intent,CAMERAIMAGE_CODE);
            }
        });
        //endregion
    }


    //region 위험권환 부여
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this,requestCode,permissions,this);
    }

    @Override
    public void onDenied(int i, String[] permissions) {
        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int i, String[] permissions) {
        Toast.makeText(this,"permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }
    //endregion
}