package megvii.testfacepass.pa.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.megvii.AuthApi.AuthApi;
import com.megvii.AuthApi.AuthCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.io.IOException;
import java.util.List;


import io.objectbox.Box;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;

import megvii.testfacepass.pa.utils.AcquireTokenAPI;
import megvii.testfacepass.pa.utils.GetDeviceId;
import megvii.testfacepass.pa.utils.ToastUtils;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;



public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


   // static final String tokenApiUrl = "https://biap-dev-auth-test.pingan.com:10565/dev-auth-web/biap/demo/acticatecode/acquiretoken";
    String tokenApiUrl = "https://biap-dev-auth.pingan.com/dev-auth-web/biap/demo/acticatecode/acquiretoken";
    static final String device = "stest-dev";
    private ProgressDialog mProgressDialog;
    private BaoCunBean baoCunBean;
    private Box<BaoCunBean> baoCunBeanBox=MyApplication.myApplication.getBaoCunBeanBox();
    private static boolean isL=true;
    private SharedPreferences mSharedPreferences;

    static {
        try {
            //Robin pace_face_detect.so需要在授权之前加载
            System.loadLibrary("pace_face_detect");
        } catch (UnsatisfiedLinkError var1) {
            Log.e("Robin", "detection" + var1.toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        baoCunBean=baoCunBeanBox.get(123456L);
        obj = new AuthApi();
        mSharedPreferences = getSharedPreferences("SP", Context.MODE_PRIVATE);
        methodRequiresTwoPermission();



    }

    private final int RC_CAMERA_AND_LOCATION=10000;

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA,
                Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_COARSE_LOCATION
                ,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.INTERNET};

        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已经得到许可，就去做吧 //第一次授权成功也会走这个方法
            Log.d("BaseActivity", "成功获得权限");
               start();

//            try {
//                singleCertification();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要授予app权限,请点击确定",
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        Log.d("BaseActivity", "list.size():" + list.size());

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        Log.d("BaseActivity", "list.size():" + list.size());
        Toast.makeText(BaseActivity.this,"权限被拒绝无法正常使用app",Toast.LENGTH_LONG).show();

    }

    private AuthApi obj;
    private static final int GET_AUTH_OK = 0;

    private  void singleCertification() throws IOException {
        String active = "r009b9e58d8f9a8d6dd0e3024a4bbac3f000006";
        String cert="\"{\"\"serial\"\":\"\"m00420764abde7861a9499f135641501301d9\"\",\"\"key\"\":\"\"1847df22d4a3ae83eb7a6781a17118c3d0dd21671b86455030196e6dfc01066d674266063ea6c43914b090531fa377d77602fc883f5cc8acdeedfd1e578bf539a35dca7f606b147d052ad9827e6d61c7510700964c150a5ae7b5783e371f745017fef5a068d914ed224460e3c77808e729f3e94735b1c7d1bd857fc33d0d8ba9d51fec0db3d37fd1f5f7d58f179c79f16b7e8a9eaff8a392b1aecd55598011c0c23ade589861e63f7c9daf62b803607690b7882ce3032c8f9563b04870483c88\"\"}\"\n";
        if (TextUtils.isEmpty(cert)|| TextUtils.isEmpty(active)){
            EventBus.getDefault().post("授权文件为空");
            return;
        }

        Log.d("FaceInit", "cert:"+cert);
        Log.d("FaceInit","active:"+active );
        obj.authDevice(cert,active, new AuthCallback() {
            @Override
            public void onAuthResponse(final int i, final String s) {
                if(i == GET_AUTH_OK){
                    EventBus.getDefault().post("激活成功");
                    if (mSharedPreferences != null) {
                        mSharedPreferences.edit().putBoolean("token", true).apply();
                        start();
                    }
                }else {
                    Log.d("FaceInit", s);
                    Log.d("FaceInit", "i:" + i);
                    EventBus.getDefault().post("激活失败"+s+":"+i);
                }
            }
        });
    }


    private void showDialog(final boolean value) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(BaseActivity.this);
                }
                if (value) {
                    mProgressDialog.setCancelable(false);
                    if (!BaseActivity.this.isFinishing())
                        mProgressDialog.show();
                } else {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    private void start(){
        //初始化
        File file = new File(MyApplication.SDPATH);
        if (!file.exists()) {
            Log.d("ggg", "file.mkdirs():" + file.mkdirs());
        }
        File file2 = new File(MyApplication.SDPATH2);
        if (!file2.exists()) {
            Log.d("ggg", "file.mkdirs():" + file2.mkdirs());
        }
        File file3 = new File(MyApplication.SDPATH3);
        if (!file3.exists()) {
            Log.d("ggg", "file.mkdirs():" + file3.mkdirs());
        }

        if (baoCunBean.getTuisongDiZhi()==null || baoCunBean.getTuisongDiZhi().equals("")) {
            String deviceId = GetDeviceId.getDeviceId(BaseActivity.this);
            if (deviceId == null) {
                ToastUtils.show2(BaseActivity.this, "获取设备唯一标识失败");

            } else {
                Log.d("BaseActivity", deviceId + "设备唯一标识");
                baoCunBean.setTuisongDiZhi(deviceId);
                baoCunBeanBox.put(baoCunBean);
            }
        }else {
            Log.d("BaseActivity", baoCunBean.getTuisongDiZhi());
        }
        boolean token;
        token = mSharedPreferences.getBoolean("token", false);
        Log.d("BaseActivity", "token:" + token);
        if (token) {
            startActivity(new Intent(BaseActivity.this,MianBanJiActivity3.class));
            finish();
        } else {
            startActivity(new Intent(BaseActivity.this,SheZhiActivity2.class));
            finish();
        }
    }


    private void requestToken() {
        if (baoCunBean.getAppurl()==null || baoCunBean.getAppid()==null|| baoCunBean.getAppkey()==null){
            showDialog(false);
            ToastUtils.show2(BaseActivity.this, "初始化失败,激活码为空");
            startActivity(new Intent(BaseActivity.this,SheZhiActivity2.class));
            finish();
            return;
        }

        showDialog(true);
        new AcquireTokenAPI().requestToken(tokenApiUrl, baoCunBean.getAppid(), device, new AcquireTokenAPI.AcquireTokenListener() {
            @Override
            public void onSuccess(String response) {
                String token;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    Log.e("ssss", "get token response json: " + response);
                    if ("0".equals(code) && jsonObject.has("data")) {
                        String dataJson = jsonObject.getString("data");
                        JSONObject jsonObject1 = new JSONObject(dataJson);
                        token = jsonObject1.getString("token");
                        if (mSharedPreferences != null) {
                            mSharedPreferences.edit().putString("token", token).apply();
                        }
                      //  startAuthAndInitSDK(token);
                    } else {
                        showDialog(false);
                        Toast.makeText(getApplicationContext(), "获取token网络请求失败", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BaseActivity.this,MianBanJiActivity3.class).putExtra("dddd",-1));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showDialog(false);
                    Toast.makeText(getApplicationContext(), "获取token,Json解析异常", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BaseActivity.this,MianBanJiActivity3.class).putExtra("dddd",-1));
                    finish();
                }
            }

            @Override
            public void onFail() {
                showDialog(false);
                Toast.makeText(getApplicationContext(), "获取token网络请求失败", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BaseActivity.this,MianBanJiActivity3.class).putExtra("dddd",-1));
                finish();
            }
        });
    }


    public static class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Intent i = new Intent(context, BaseActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
