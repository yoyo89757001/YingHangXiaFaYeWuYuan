package megvii.testfacepass.pa.ui;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;


import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hwit.HwitManager;
import com.lztek.toolkit.Lztek;
import com.sdsmdg.tastytoast.TastyToast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import mcv.facepass.FacePassException;
import mcv.facepass.FacePassHandler;
import mcv.facepass.types.FacePassAddFaceResult;
import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassFace;
import mcv.facepass.types.FacePassImage;
import mcv.facepass.types.FacePassImageType;
import mcv.facepass.types.FacePassMouthOccAttr;
import mcv.facepass.types.FacePassRecognitionResult;
import mcv.facepass.types.FacePassRecognitionResultType;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;

import megvii.testfacepass.pa.beans.HuiFuBean;
import megvii.testfacepass.pa.beans.IDCardBean;
import megvii.testfacepass.pa.beans.IDCardBean_;
import megvii.testfacepass.pa.beans.IDCardTakeBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.XGBean;
import megvii.testfacepass.pa.beans.ZhiLingBean;
import megvii.testfacepass.pa.camera.CameraManager;

import megvii.testfacepass.pa.camera.CameraPreview;

import megvii.testfacepass.pa.camera.CameraPreviewData;

import megvii.testfacepass.pa.dialog.MiMaDialog3;
import megvii.testfacepass.pa.dialog.MiMaDialog4;
import megvii.testfacepass.pa.tuisong_jg.MyServeInterface;

import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FacePassUtil;

import megvii.testfacepass.pa.utils.GsonUtil;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.view.GlideRoundTransform;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import top.zibin.luban.Luban;


public class MianBanJiActivity3 extends Activity implements CameraManager.CameraListener,MyServeInterface, SensorEventListener {

    @BindView(R.id.xiping)
    ImageView xiping;
    @BindView(R.id.tishiyu)
    TextView tishiyu;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.faceLinearLayout)
    LinearLayout faceLinearLayout;
    @BindView(R.id.faceName)
    TextView faceName;
    @BindView(R.id.faceImage)
    ImageView faceImage;



    private NetWorkStateReceiver netWorkStateReceiver = null;
    private SensorManager sm;
    private Box<Subject> subjectBox = null;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private Box<IDCardBean> idCardBeanBox = MyApplication.myApplication.getIdCardBeanBox();

   // private Bitmap msrBitmap = null;
    private RequestOptions myOptions = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
           // .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
    .transform(new GlideRoundTransform(MianBanJiActivity3.this,10));
//
//    private RequestOptions myOptions2 = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//            .transform(new GlideCircleTransform270(MyApplication.myApplication, 2, Color.parseColor("#ffffffff"), 270));
   // private String serialnumber = GetDeviceId.getDeviceId(MyApplication.myApplication);

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private final Timer timer = new Timer();
    private TimerTask task;
    private final Timer timer2 = new Timer();
   // private TimerTask task2;
    private LinkedBlockingQueue<ZhiLingBean.DataBean> linkedBlockingQueue;
    /* 相机实例 */
    private CameraManager manager;
  //  private CameraManager2 manager2;
    /* 显示人脸位置角度信息 */
    // private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
  //  private CameraPreview2 cameraView2;
    private static final int cameraWidth = 720;
    private static final int cameraHeight = 640;
  //  private boolean isOP = true;
    private int heightPixels;
    private int widthPixels;
    int screenState = 0;// 0 横 1 竖
    TanChuangThread tanChuangThread;
    private ReadThread3 mReadThread3;
    // private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private int dw, dh;
    private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private Box<BaoCunBean> baoCunBeanDao = null;
    private Box<HuiFuBean> huiFuBeanBox = null;
   // private Box<DaKaBean> daKaBeanBox = null;
    private BaoCunBean baoCunBean = null;
    private TimeChangeReceiver timeChangeReceiver;
    private Handler mHandler;
    //private static boolean isLink = true;
    private FacePassHandler paAccessControl=null;
 //   private Float mCompareThres;
  //  private static String faceId = "";
   // private long feature2 = -1;
   // private NV21ToBitmap nv21ToBitmap;
    private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
    private HashMap<Integer, Integer> musicId = new HashMap<>();
    private int pp = 0;
   // private ReadThread mReadThread;
  // private ReadThread mReadThread;
    private InputStream mInputStream;
  //  private int w, h, cameraH, cameraW;
   // private float s1 = 0, s2 = 0;
    private Timer mTimer;//距离感应
    private TimerTask mTimerTask;//距离感应
    private int pm = 0;
    private boolean onP1 = true, onP2 = true;
    private boolean isPM = true;
    private boolean isPM2 = true;
    private float juli = 0;
    private String JHM = null;
    TextView tvTitle_Ir;
    TextView tvName_Ir;//识别结果弹出信息的名字
    TextView tvTime_Ir;//识别结果弹出信息的时间
    TextView tvFaceTips_Ir;//识别信息提示
    LinearLayout layout_loadbg_Ir;//识别提示大框
    RelativeLayout layout_true_gif_Ir, layout_error_gif_Ir;//蓝色图片动画 红色图片动画
    ImageView iv_true_gif_in_Ir, iv_true_gif_out_Ir, iv_error_gif_in_Ir, iv_error_gif_out_Ir;//定义旋转的动画
    Animation gifClockwise, gifAntiClockwise;
    LinearInterpolator lir_gif;
    private Box<IDCardTakeBean> idCardTakeBeanBox=MyApplication.myApplication.getIdCardTakeBeanBox();
    private int jiqiType=-1;
    private boolean isGET = true;
    private int cishu=5;
    private int jidianqi=6000;
    private Lztek lztek=null;
  //  private Function mFuncs = null;
   // private int loc_readerHandle=-1;
    private boolean isCLOSDLED =false;
  //  private CameraPreviewData mCurrentImage;
    ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    private static final String group_name = "facepasstestx";
    private int STATE=1;
    //private static boolean isR=false;
    private String TIME=null;
    private int gpio=218;
    private boolean isOPen=false;
    private String xueliehao=null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        huiFuBeanBox = MyApplication.myApplication.getHuiFuBeanBox();
        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
      //  daKaBeanBox=MyApplication.myApplication.getDaKaBeanBox();
        baoCunBean = baoCunBeanDao.get(123456L);
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        gpio=baoCunBean.getPort();
        MyApplication.myApplication.addActivity(this);

        if (baoCunBean.getDangqianChengShi2()!=null){
            switch (baoCunBean.getDangqianChengShi2()){
                case "智连":
                    jiqiType=0;
                    break;
                case "亮钻":
                    jiqiType=1;
                    break;
                case "涂鸦":
                    jiqiType=2;
                    break;
            }
        }

        JHM = baoCunBean.getJihuoma();
        if (JHM == null)
            JHM = "";
        subjectBox = MyApplication.myApplication.getSubjectBox();
        xueliehao=baoCunBean.getTuisongDiZhi();

        //每分钟的广播
        // private TodayBean todayBean = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>(60);
        EventBus.getDefault().register(this);//订阅
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;
      //  nv21ToBitmap = new NV21ToBitmap(MianBanJiActivity3.this);
        /* 初始化界面 */
        //  Log.d("MianBanJiActivity3", "jh:" + baoCunBean);
        //初始化soundPool,设置可容纳12个音频流，音频流的质量为5，
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中
        musicId.put(1, soundPool.load(this, R.raw.yanzhengtongguo, 1));//验证通过
        musicId.put(2, soundPool.load(this, R.raw.moshengren, 1));//陌生人
        musicId.put(3, soundPool.load(this, R.raw.meiyouxinxi, 1));//没有查询到信息
        musicId.put(4, soundPool.load(this, R.raw.meiyouguoqishijian, 1));//没有过期时间
        musicId.put(5, soundPool.load(this, R.raw.xinxiguoqi, 1));//信息过期
        musicId.put(6, soundPool.load(this, R.raw.jilushibai, 1));//上传记录失败 请重试
        musicId.put(7, soundPool.load(this, R.raw.wangluobutong, 1));//网络不通 请检查网络
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        try {
            lztek=Lztek.create(MyApplication.myApplication);
            lztek.gpioEnable(gpio);
            lztek.setGpioOutputMode(gpio);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }

        initView();

//       List<Subject> subjects= subjectBox.getAll();
//        for (Subject subject : subjects) {
//            Log.d("MianBanJiActivity3", subject.getSid()+subject.getName());
//        }

//        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
//        serverManager.setMyServeInterface(MianBanJiActivity3.this);
//        serverManager.startServer();

        if (baoCunBean != null) {
            try {
                if (baoCunBean.getJidianqi()!=0){
                    jidianqi=baoCunBean.getJidianqi();
                }
                if (baoCunBean.getMoshengrenPanDing()!=0){
                    cishu=baoCunBean.getMoshengrenPanDing();
                }

                FacePassHandler.initSDK(getApplicationContext());
                FacePassUtil util = new FacePassUtil();
                util.init(MianBanJiActivity3.this, getApplicationContext(), SettingVar.faceRotation, baoCunBean);

            } catch (Exception e) {
               TastyToast.makeText(MianBanJiActivity3.this,"初始化失败"+e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR).show();

            }
        }

        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();
        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();

        link_get_zhiling();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(10000);
             //   w = cameraView.getMeasuredWidth();
             //   h = cameraView.getMeasuredHeight();
              //  cameraH = manager.getCameraheight();
              //  cameraW = manager.getCameraWidth();
             //   s1 = (float) w / (float) cameraH;
             //   s2 = (float) h / (float) cameraW;

                if (lztek!=null && !isOPen)
                lztek.hardReboot();

                if (TIME==null)
                    return;
               List<Subject> subjects= subjectBox.getAll();
               for (Subject subject:subjects){
                   try {
                   long times1=DateUtils.date2TimeStamp(subject.getBirthday());
                   long times2=DateUtils.date2TimeStamp(TIME);
                   if (times1<times2){
                       if (paAccessControl!=null){
                           Log.d("MianBanJiActivity3", "删除过期时间的人员");
                           paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                           subjectBox.remove(subject);
                       }
                     }
                   } catch (Exception e) {
                       e.printStackTrace();
                }
               }

            }
        }).start();

//        try {
//            SerialPort mSerialPort = MyApplication.myApplication.getSerialPort();
//            //mOutputStream = mSerialPort.getOutputStream();
//            mInputStream = mSerialPort.getInputStream();
//
//        } catch (Exception e) {
//            Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
//        }

       // mReadThread = new ReadThread();
      //  mReadThread.start();




        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                switch (msg.what) {
//                    case 111: {
//                        Subject subject = (Subject) msg.obj;
//                        //Log.d("MianBanJiActivity3", "subject:" + subject);
//                        if (subject.getTeZhengMa() != null) {
//                            //  Log.d("MianBanJiActivity3", "ddd3");
//                         //   faceView.setTC(BitmapFactory.decodeFile(MyApplication.SDPATH3 + File.separator + subject.getTeZhengMa() + ".png")
//                            //        , subject.getName(), subject.getDepartmentName());
//
//                         //   DengUT.getInstance(baoCunBean).openDool();
//                         //   DaKaBean daKaBean=new DaKaBean();
//                         //   daKaBean.setId2(subject.getSid());
//                         //   daKaBeanBox.put(daKaBean);
//
//                            //启动定时器或重置定时器
//                            if (task != null) {
//                                task.cancel();
//                                //timer.cancel();
//                                task = new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        Message message = new Message();
//                                        message.what = 222;
//                                        mHandler.sendMessage(message);
//                                    }
//                                };
//                                timer.schedule(task, jidianqi);
//                            } else {
//                                task = new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        Message message = new Message();
//                                        message.what = 222;
//                                        mHandler.sendMessage(message);
//                                    }
//                                };
//                                timer.schedule(task, jidianqi);
//                            }
//                        } else {
//                            //  Log.d("MianBanJiActivity3", "ddd4");
//                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//                            //faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), subject.getDepartmentName());
//                        }
//
//                        break;
//                    }
                    case 222: {//关门
                        DengUT.getInstance(baoCunBean).closeDool();
                        break;
                    }
                    case 333:
                       // if (isCLOSDLED)
                        DengUT.getInstance(baoCunBean).openLOED();
                        break;
                    case 444:
                       // if (isCLOSDLED)
                        DengUT.getInstance(baoCunBean).closeLOED();
                        break;
//                    case 999:
//                        String card= (String) msg.obj;
//                        if (card!=null){
//                           List<Subject> subjectList= subjectBox.query().equal(Subject_.workNumber,card).build().find();
//                           if (subjectList.size()>1){
//                               StringBuilder builder=new StringBuilder();
//                               for (Subject subject:subjectList){
//                                   builder.append(subject.getName());
//                                   builder.append(",");
//                               }
//                               Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, builder.toString()+"ID卡号重复,打卡失败,请重新设置", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//                               tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                               tastyToast.show();
//
//                           }else if (subjectList.size()==1){
//                               faceLinearLayout.setVisibility(View.VISIBLE);
//                               faceName.setText(subjectList.get(0).getName());
//                               Glide.with(MyApplication.myApplication)
//                                       .load(R.drawable.erroy_bg)
//                                       .apply(myOptions)
//                                       .into(faceImage);
//                               new Thread(new Runnable() {
//                                   @Override
//                                   public void run() {
//                                       SystemClock.sleep(2200);
//                                       runOnUiThread(new Runnable() {
//                                           @Override
//                                           public void run() {
//                                               faceLinearLayout.setVisibility(View.GONE);
//                                           }
//                                       });
//                                   }
//                               }).start();
//                           }else {
//                               Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "此ID卡不存在", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
//                               tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                               tastyToast.show();
//                           }
//                        }
//
//                        break;

                }
                return false;
            }
        });


        //init_NFC();

        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1

//        if (baoCunBean.isHuoTi()) {
//            if (SettingVar.cameraId == 1) {
//                manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            } else {
//                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
//            }
//        }

//        try {
//            mFuncs = new Function(this, mHandler);
//            loc_readerHandle = mFuncs.lc_init_ex(1, "/dev/ttyS1".toCharArray(), Integer.parseInt("9600"));
//            if(loc_readerHandle == -1)
//            {
//                Toast.makeText(getApplicationContext(), "连接读卡器失败", Toast.LENGTH_SHORT).show();
//            }
//            else
//            {
//                int aa= mFuncs.lc_beep(loc_readerHandle, 2);
//                Log.d("MianBanJiActivity3", loc_readerHandle+"   "+ aa);
//                new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    int st = 0;
//                    byte[] rData = new byte[128];
//                    int[] rlen = new int[2];
//                    if (loc_readerHandle!=-1){
//                      //  Log.d("MianBanJiActivity3", "ffffffff:"+loc_readerHandle);
//                        st = mFuncs.lc_getAutoReturnedData(loc_readerHandle, rData, rlen);
//                      //  Log.d("MianBanJiActivity3", "hh哈哈:"+st);
//                        if (st == 0)
//                        {
//                            StringBuilder showStr= new StringBuilder();
//                            int len=rlen[0];
//                            for(int i= 0; i<len; i++)
//                                showStr.append(byteToHexString(rData[i]));
//                            Log.d("MianBanJiActivity3", showStr.toString());
//                            Message message = new Message();
//                            message.what = 999;
//                            message.obj=showStr.toString();
//                            mHandler.sendMessage(message);
//
//
//                        }
//                    }
//                }
//
//            }
//        }).start();
//
//
//            }
//        }catch (NoClassDefFoundError error){
//            Log.d("MianBanJiActivity3", error.getMessage()+"");
//        }


        guanPing();//关屏
        DengUT.getInstance(baoCunBean).openLOED();



    }


    private String  byteToHexString(byte mByte)
    {
        String hexStr;

        hexStr = Integer.toHexString(mByte & 0xff);
        if(hexStr.length() == 1)
            hexStr = '0'+ hexStr;

        return hexStr;
    }

    @OnClick(R.id.root_layout)
    public void onViewClicked() {
        if (baoCunBean.isShowShiPingLiu()){
            MiMaDialog3 miMaDialog=new MiMaDialog3(MianBanJiActivity3.this,baoCunBean.getMima2());
            WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
            params.width=dw;
            params.height=dh+60;
            miMaDialog.getWindow().setGravity(Gravity.CENTER);
            miMaDialog.getWindow().setAttributes(params);
            miMaDialog.show();
        }
    }



//    private class ReadThread extends Thread {
//
//        @Override
//        public void run() {
//            super.run();
//            while (!isInterrupted()) {
//                int size;
//                try {
//                    final byte[] buffer = new byte[64];
//                    if (mInputStream == null) return;
//                    size = mInputStream.read(buffer);
//                    if (size > 0) {
//                        // Log.d("ReadThread", "buffer.length:" + byteToString(buffer));
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                readdd(buffer);
//                            }
//                        });
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//        }
//    }


//    private void readdd(byte[] idid) {
//        String sdfds = byteToString(idid);
//        if (sdfds != null) {
//            sdfds = sdfds.substring(6, 14);
//        } else {
//            return;
//        }
//        sdfds = sdfds.toUpperCase();
//        Log.d("MianBanJiActivity3", sdfds);
//        List<IDCardBean> idCardBeanList = idCardBeanBox.query().equal(IDCardBean_.idCard, sdfds).build().find();
//        if (idCardBeanList.size() > 0) {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证成功!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//            soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
//            DengUT.getInstance(baoCunBean).openDool();
//            IDCardBean cardBean=idCardBeanList.get(0);
//            link_shuaka(sdfds,cardBean.getName());
//            //启动定时器或重置定时器
//            if (task != null) {
//                task.cancel();
//                //timer.cancel();
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        Message message = new Message();
//                        message.what = 222;
//                        mHandler.sendMessage(message);
//                    }
//                };
//                timer.schedule(task, 6000);
//            } else {
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        Message message = new Message();
//                        message.what = 222;
//                        mHandler.sendMessage(message);
//                    }
//                };
//                timer.schedule(task, 6000);
//            }
//
//            IDCardTakeBean takeBean=new IDCardTakeBean();
//            takeBean.setIdCard(sdfds);
//            takeBean.setName(cardBean.getName());
//            takeBean.setTime(System.currentTimeMillis());
//            idCardTakeBeanBox.put(takeBean);
//
//        } else {
//            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证失败!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
//            tastyToast.setGravity(Gravity.CENTER, 0, 0);
//            tastyToast.show();
//            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
//        }
//    }


    @Override
    protected void onResume() {
        Log.d("MianBanJiActivity3", "重新开始");
        super.onResume();

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }


        if (jiqiType==2) {//涂鸦
            Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (juli > 0 && juli<9000) {
                            isPM2 = true;
                            //有人
                            if (isPM) {
                                isPM = false;
                                onP1 = true;
                                onP2 = true;
                                pm = 0;
                                Message message = new Message();
                                message.what = 333;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            isPM = true;
                            if (isPM2) {
                                pm++;
                                if (pm == 8) {
                                    Message message = new Message();
                                    message.what = 444;
                                    mHandler.sendMessage(message);
                                    isPM2 = false;
                                    onP1 = false;
                                    onP2 = false;
                                    pm = 0;
                                    if (DengUT.isOPENRed) {
                                        DengUT.isOPENRed = false;
                                        DengUT.getInstance(baoCunBean).closeRed();
                                    }
                                    if (DengUT.isOPENGreen) {
                                        DengUT.isOPENGreen = false;
                                        DengUT.getInstance(baoCunBean).closeGreen();
                                    }
                                    if (DengUT.isOPEN) {
                                        DengUT.isOPEN = false;
                                        DengUT.getInstance(baoCunBean).closeWrite();
                                    }
                                }
                            }
                        }
                    }
                };
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }

        if (lztek!=null){
            mReadThread3 = new ReadThread3();
            mReadThread3.start();
        }
    }

    private static boolean isRS=false;
    private class ReadThread3 extends Thread {
        boolean isIterrupt;
        @Override
        public void run() {
            super.run();
            while (!isIterrupt) {
                if (lztek!=null){
                    SystemClock.sleep(160);
                    final int value = lztek.getGpioValue(gpio);
                    // Log.d("MianBanJiActivity3", "value:" + value);
                    if (value==1){//有人
                        isCLOSDLED=true;
                        // Log.d("MianBanJiActivity3", "value:" + value);
                        isRS=true;
                        Message message = new Message();
                        message.what = 333;
                        mHandler.sendMessage(message);

                        if (task != null)
                            task.cancel();
                        SystemClock.sleep(2000);

                    }else {//没人
                        if (isRS){
                            isRS=false;
                            //  Log.d("MianBanJiActivity3", "valuettttt:" + value);
                            //启动定时器或重置定时器
                            if (task != null) {
                                task.cancel();
                                //timer.cancel();
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (isCLOSDLED){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                try {
                                    timer.schedule(task, 10000);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } else {
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (isCLOSDLED){
                                            Message message = new Message();
                                            message.what = 444;
                                            mHandler.sendMessage(message);
                                        }
                                    }
                                };
                                try {
                                    timer.schedule(task, 10000);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }


            }
        }
        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e("距离", "" + event.values[0]);
        juli = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onNewIntent(Intent intent) {
        //  super.onNewIntent(intent);
        // Log.d("SheZhiActivity2", "intent:" + intent);
        processIntent(intent);
    }






    @Override
    public void onStarted(String ip) {
        Log.d("MianBanJiActivity3", "小服务器启动" + ip);
    }


    @Override
    public void onStopped() {
        Log.d("MianBanJiActivity3", "小服务器停止");
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ZLoadingDialog zLoadingDialog = new ZLoadingDialog(MianBanJiActivity3.this);
//                zLoadingDialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
//                        .setLoadingColor(Color.parseColor("#0d2cf9"))//颜色
//                        .setHintText("服务器停止,请重启...")
//                        .setHintTextSize(20) // 设置字体大小 dp
//                        .setHintTextColor(Color.WHITE)  // 设置字体颜色
//                        .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
//                        .setDialogBackgroundColor(Color.parseColor("#CC111111")) // 设置背景色，默认白色
//                        .show();
//            }
//        });
    }

    @Override
    public void onException(Exception e) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ZLoadingDialog zLoadingDialog = new ZLoadingDialog(MianBanJiActivity3.this);
//                zLoadingDialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
//                        .setLoadingColor(Color.parseColor("#0d2cf9"))//颜色
//                        .setHintText("服务器异常,请重启...")
//                        .setHintTextSize(20) // 设置字体大小 dp
//                        .setHintTextColor(Color.WHITE)  // 设置字体颜色
//                        .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
//                        .setDialogBackgroundColor(Color.parseColor("#CC111111")) // 设置背景色，默认白色
//                        .show();
//            }
//        });
        Log.d("MianBanJiActivity3", "小服务器异常" + e);
    }



    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        isOPen=true;
        if (paAccessControl == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, SettingVar.faceRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);
    }

    private class FeedFrameThread extends Thread {
        boolean isIterrupt;

        @Override
        public void run() {
            while (!isIterrupt) {
                try {
                    FacePassImage image = mFeedFrameQueue.take();
                    /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                    FacePassDetectionResult detectionResult = null;
                    detectionResult = paAccessControl.feedFrame(image);
                   // Log.d("FeedFrameThread", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                   // Log.d("FeedFrameThread", "detectionResult:" + detectionResult);
                    if (detectionResult == null || detectionResult.faceList.length <= 0) {
                      //  Log.d("FeedFrameThread", "没人");
                        if (DengUT.isOPEN || DengUT.isOPENRed || DengUT.isOPENGreen) {
                            DengUT.isOPEN = false;
                            DengUT.isOPENGreen = false;
                            DengUT.isOPENRed = false;
                            DengUT.isOpenDOR = false;
                            DengUT.getInstance(baoCunBean).closeWrite();
                            showUIResult(1,"","");
                        }

                    }else {

                        final FacePassFace[] bufferFaceList = detectionResult.faceList;
                        FacePassMouthOccAttr attr=bufferFaceList[0].mouthOccAttr;
                        if (attr.is_valid){//有效
                            String kouzhao="";
                            switch (attr.mouth_occ_status){
                                case 0:
                                    kouzhao="未佩戴口罩";
                                    break;
                                case 1:
                                    kouzhao="面具遮挡";
                                    break;
                                case 2:
                                    kouzhao="已佩戴口罩";
                                    break;
                                case 3:
                                    kouzhao="其他遮挡";
                                    break;
                                default:
                                    kouzhao="";
                                    break;
                            }
                            String finalKouzhao = kouzhao;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvFaceTips_Ir.setText(finalKouzhao);
                                }
                            });
                        }
                    }

                    /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                    if (detectionResult != null && detectionResult.message.length != 0) {
                      //  Log.d("FeedFrameThread", "插入");
                        if (!DengUT.isOPEN) {
                            DengUT.isOPEN = true;
                            DengUT.getInstance(baoCunBean).openWrite();
                            showUIResult(2,"","");
                        }
                        mDetectResultQueue.offer(detectionResult);
                        //   Log.d("ggggg", "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                    }
                    //     }
                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }


    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {
                    //有动画 ，延迟到一秒一次
                    ZhiLingBean.DataBean commandsBean = linkedBlockingQueue.take();//若队列为空，发生阻塞，等待有元素
                    if (paAccessControl == null) {
                        continue;
                    }
                    if (commandsBean.getOperationStatus()==1){//新增
                        Subject subject = subjectBox.query().equal(Subject_.sid, commandsBean.getEmpId()).build().findUnique();
                        Log.d("TanChuangThread", "新增前查询subject:" + subject);
                        if (subject!=null){
                            continue;
                        }
                    }else if (commandsBean.getOperationStatus()==0){//删除
                        Subject subject = subjectBox.query().equal(Subject_.sid, commandsBean.getEmpId()).build().findUnique();
                        Log.d("TanChuangThread", "删除前查询subject:" + subject);
                        if (subject!=null){
                            try {
                                paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                subjectBox.remove(subject);
                                continue;
                            }catch (Exception e){
                                e.printStackTrace();
                                continue;
                            }
                        }else {
                            continue;
                        }
                    }
                    Bitmap bitmap = null;
                    try {
                        bitmap = Glide.with(MianBanJiActivity3.this).asBitmap()
                                .load(commandsBean.getPhotoURL())
                                // .sizeMultiplier(0.5f)
                                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    FacePassAddFaceResult detectResult = null;
                    if (bitmap != null) {
                        BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3,  "aaabbb.jpg");
                        // File filef = new File(MyApplication.SDPATH3 + File.separator +  "aaabbb.png");
                        // Log.d("TanChuangThread",  "未压缩前:filef.length():" + filef.length());
                        File file = Luban.with(MianBanJiActivity3.this)
                                .load(MyApplication.SDPATH3 + File.separator +  "aaabbb.jpg")
                                .ignoreBy(600)
                                .setTargetDir(MyApplication.SDPATH3 + File.separator)
                                .get(MyApplication.SDPATH3 + File.separator +  "aaabbb.jpg");
                        if (file==null){
                            continue;
                        }

                        detectResult = paAccessControl.addFace(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        //  Log.d("TanChuangThread", "detectResult:" + detectResult);
                        if (detectResult != null && detectResult.result == 0) {
                            // BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, commandsBean.getId() + ".png");
                            //先查询有没有
                            try {
                                byte[] faceToken = detectResult.faceToken;
                                Subject subject2 = subjectBox.query().equal(Subject_.sid, commandsBean.getEmpId()).build().findUnique();
                                if (subject2 != null) {
                                    paAccessControl.deleteFace(subject2.getTeZhengMa().getBytes());
                                    subjectBox.remove(subject2);
                                }
                                Subject subject = new Subject();
                                subject.setTeZhengMa(new String(faceToken));
                                subject.setSid(commandsBean.getEmpId());
                                subject.setBirthday(commandsBean.getFaceVerifyDate());
                                subject.setId(System.currentTimeMillis());
                                subject.setName(commandsBean.getName());
                                paAccessControl.bindGroup(group_name, faceToken);
                                subjectBox.put(subject);
                                Log.d("MyReceiver", "单个员工入库成功" + subject.toString());
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                        "", "1", "成功", commandsBean.getEmpId(), JHM);
                                huiFuBeanBox.put(huiFuBean);

                            } catch (Exception e) {
                                e.printStackTrace();
                                HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                        "", "0", e.getMessage()+"", commandsBean.getEmpId(), JHM);
                                huiFuBeanBox.put(huiFuBean);
                            }
                        } else {
                              HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                                    "", "0", "图片质量不合格", commandsBean.getEmpId(), JHM);
                              huiFuBeanBox.put(huiFuBean);
                        }
                        Log.d("TanChuangThread", "file.delete():" + file.delete());
                    } else {
                         HuiFuBean huiFuBean = new HuiFuBean(System.currentTimeMillis(), commandsBean.getId(),
                              "", "0", "图片下载失败", commandsBean.getEmpId(), JHM);
                         huiFuBeanBox.put(huiFuBean);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }

        @Override
        public void interrupt() {
            isRing = true;
            // Log.d("RecognizeThread", "中断了弹窗线程");
            super.interrupt();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MianBanJiActivity3", "暂停");
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (sm != null)
            sm.unregisterListener(this);

    }


    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.cameraId = preferences.getInt("cameraId", SettingVar.cameraId);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        SettingVar.cameraPreviewRotation2 = preferences.getInt("cameraPreviewRotation2", SettingVar.cameraPreviewRotation2);
        SettingVar.faceRotation2 = preferences.getInt("faceRotation2", SettingVar.faceRotation2);
        SettingVar.msrBitmapRotation = preferences.getInt("msrBitmapRotation", SettingVar.msrBitmapRotation);

        setContentView(R.layout.activity_mianbanji3);

        ButterKnife.bind(this);

        ImageView shezhi = findViewById(R.id.shezhi);
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiMaDialog4 miMaDialog = new MiMaDialog4(MianBanJiActivity3.this, baoCunBean.getMima());
                WindowManager.LayoutParams params = miMaDialog.getWindow().getAttributes();
                params.width = dw;
                params.height = dh;
//                miMaDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                miMaDialog.getWindow().setAttributes(params);
                miMaDialog.show();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        /* 初始化界面 */
        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

      //  manager2 = new CameraManager2();
      //  cameraView2 = findViewById(R.id.preview2);
      //  manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
      //  manager2.setListener(this);

        tvName_Ir = findViewById(R.id.tvName_Ir);//名字
        tvTime_Ir = findViewById(R.id.tvTime_Ir);//时间
        tvFaceTips_Ir = findViewById(R.id.tvFaceTips_Ir);//识别信息提示
        layout_loadbg_Ir = findViewById(R.id.layout_loadbg_Ir);//头像区域的显示的底图背景

        layout_true_gif_Ir = findViewById(R.id.layout_true_gif_Ir);
        layout_error_gif_Ir = findViewById(R.id.layout_error_gif_Ir);
        iv_true_gif_in_Ir = findViewById(R.id.iv_true_gif_in_Ir);
        iv_true_gif_out_Ir = findViewById(R.id.iv_true_gif_out_Ir);
        iv_error_gif_in_Ir = findViewById(R.id.iv_error_gif_in_Ir);
        iv_error_gif_out_Ir = findViewById(R.id.iv_error_gif_out_Ir);
        tvTitle_Ir = findViewById(R.id.tvTitle_Ir);

        //region 动画
        gifClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_clockwise);
        gifAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_anti_clockwise);
        lir_gif = new LinearInterpolator();//设置为匀速旋转
        gifClockwise.setInterpolator(lir_gif);
        gifAntiClockwise.setInterpolator(lir_gif);

        iv_true_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_error_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_true_gif_out_Ir.startAnimation(gifClockwise);
        iv_error_gif_out_Ir.startAnimation(gifClockwise);
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
        tvTitle_Ir.setTypeface(tf);
        if (baoCunBean.getWenzi1() == null) {
            tvTitle_Ir.setText("请设置公司名称");
        } else {
            tvTitle_Ir.setText(baoCunBean.getWenzi1());
        }
        if (baoCunBean.getLogo()!=null){
            try {
                logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        showUIResult(1,"","");

    }



    private class RecognizeThread extends Thread {

        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {
                    FacePassDetectionResult detectionResult = mDetectResultQueue.take();
                    // byte[] detectionResult = mDetectResultQueue.take();
                    FacePassRecognitionResult[] recognizeResult = paAccessControl.recognize(group_name, detectionResult.message);
                  //   Log.d("RecognizeThread", "识别线程");
                    if (recognizeResult != null && recognizeResult.length > 0) {
                        // Log.d("RecognizeThread", "recognizeResult.length:" + recognizeResult.length);
                        for (FacePassRecognitionResult result : recognizeResult) {
                          //  Log.d("RecognizeThread", "result.trackId:" + result.trackId);
                            //String faceToken = new String(result.faceToken);
                         //   Log.d("RecognizeThread", "paAccessControl.getConfig().searchThreshold:" + paAccessControl.getConfig().searchThreshold);
                            if (FacePassRecognitionResultType.RECOG_OK == result.facePassRecognitionResultType) {
                                //识别的
                                //  getFaceImageByFaceToken(result.trackId, faceToken);
                              //  Log.d("RecognizeThread", "result.detail.searchScore:" + result.detail.searchScore);
                               // Log.d("RecognizeThread", "识别了");
                                //  Log.d("RecognizeThread", subjectBox.getAll().get(0).toString());
                                Subject subject = subjectBox.query().equal(Subject_.teZhengMa, new String(result.faceToken)).build().findUnique();
                                // Log.d("RecognizeThread", "subject:" + subject);

                                if (subject != null) {
                                    if (subject.getBirthday()!=null && TIME!=null){
                                        try {
                                            long times1=DateUtils.date2TimeStamp(subject.getBirthday());
                                            long times2=DateUtils.date2TimeStamp(TIME);
                                            if (times1<times2){//过期不通过
                                                paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                                subjectBox.remove(subject);
                                                soundPool.play(musicId.get(5), 1, 1, 0, 0, 1);
                                                continue;
                                            }
                                        }catch (Exception e){e.printStackTrace();}
                                    }else {//没有日期，不执行
                                        soundPool.play(musicId.get(4), 1, 1, 0, 0, 1);
                                        continue;
                                    }

                                    //subjectOnly = subject;
                                    // linkedBlockingQueue.offer(subject);
//                                    Message message2 = Message.obtain();
//                                    message2.what = 111;
//                                    message2.obj = subject;
//                                    mHandler.sendMessage(message2);

                                    if (!DengUT.isOPENGreen) {
                                        DengUT.isOPENGreen = true;
                                        DengUT.getInstance(baoCunBean).openGreen();
                                    }


                                  //  long time=System.currentTimeMillis();
//                                    for (int i = 0; i < detectionResult.faceList.length; i++) {
//                                        FacePassImage images = detectionResult.images[i];
//                                        if (images.trackId == result.trackId) {
//                                       //     Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.is_valid:" + detectionResult.faceList[i].mouthOccAttr.is_valid);
//                                         //   Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.mouth_occ_status:" + detectionResult.faceList[i].mouthOccAttr.mouth_occ_status);
////                                            if (detectionResult.faceList[i].mouthOccAttr.is_valid){
////                                                String kouzhao="";
////                                                switch (detectionResult.faceList[i].mouthOccAttr.mouth_occ_status){
////                                                    case 0:
////                                                        kouzhao="未佩戴口罩";
////                                                        break;
////                                                    case 1:
////                                                        kouzhao="面具遮挡";
////                                                        break;
////                                                    case 2:
////                                                        kouzhao="已佩戴口罩";
////                                                        break;
////                                                    case 3:
////                                                        kouzhao="其他遮挡";
////                                                        break;
////                                                    default:
////                                                         kouzhao="";
////                                                        break;
////                                                }
////
////                                            }
//                                            showUIResult(4,subject.getName(),subject.getDepartmentName());
//                                           // final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
//                                          //  String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ruitongzipmbj";
//                                          //  boolean tt = nv21ToBitmap.saveBitmap(fileBitmap, paths, time + ".png");
//
//                                          //  if (tt) {
//                                           //     subject.setZpPath(paths + File.separator + time + ".png");
//                                           //     Log.d("RecognizeThread", "subjectBox.put(subject):" + subjectBox.put(subject));
//                                         //   }
//                                            break;
//                                        }
//                                    }
                                    link_shangchuanshualian(subject.getSid(), null, "1");
                                    showUIResult(4,subject.getName(),"验证通过!");
                                 //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
                                    DengUT.isOPEN = true;

                                } else {
                                    soundPool.play(musicId.get(3), 1, 1, 0, 0, 1);
                                    EventBus.getDefault().post("没有查询到人员信息");
                                }
                            } else {
                              //  Log.d("RecognizeThread", "未识别");
                                //未识别的
                                // 防止concurrentHashMap 数据过多 ,超过一定数据 删除没用的
                                if (concurrentHashMap.size() > 10) {
                                    concurrentHashMap.clear();
                                }
                                if (concurrentHashMap.get(result.trackId) == null) {
                                    //找不到新增
                                    concurrentHashMap.put(result.trackId, 1);
                                } else {
                                    //找到了 把value 加1
                                    concurrentHashMap.put(result.trackId, (concurrentHashMap.get(result.trackId)) + 1);
                                }
                                //判断次数超过3次
                                if (concurrentHashMap.get(result.trackId) == cishu) {
                                   // tID = result.trackId;
                                    //isLink = true;
                                  //  msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
//                                    if (!baoCunBean.isMsrPanDing()){
//                                        return;
//                                    }
                                  //  Subject subject1 = new Subject();
                                    // subject1.setW(bitmap.getWidth());
                                    // subject1.setH(bitmap.getHeight());
                                    //图片在bitmabToBytes方法里面做了循转
                                    // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
//                                    subject1.setId(System.currentTimeMillis());
//                                    subject1.setName("陌生人");
//                                    subject1.setTeZhengMa(null);
//                                    subject1.setPeopleType("3");
//                                    subject1.setDepartmentName("暂无进入权限!");
//                                    // linkedBlockingQueue.offer(subject1);
//                                    Message message2 = Message.obtain();
//                                    message2.what = 111;
//                                    message2.obj = subject1;
//                                    mHandler.sendMessage(message2);

                                    soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);

                                    if (!DengUT.isOPENRed) {
                                        DengUT.isOPENRed = true;
                                        DengUT.getInstance(baoCunBean).openRed();
                                    }


                                  //  long time=System.currentTimeMillis();
//                                    for (int i = 0; i < detectionResult.faceList.length; i++) {
//                                        FacePassImage images = detectionResult.images[i];
//                                        if (images.trackId == result.trackId) {
//                                        //    Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.is_valid:" + detectionResult.faceList[i].mouthOccAttr.is_valid);
//                                         //   Log.d("RecognizeThread", "detectionResult.faceList[i].mouthOccAttr.mouth_occ_status:" + detectionResult.faceList[i].mouthOccAttr.mouth_occ_status);
//                                            if (detectionResult.faceList[i].mouthOccAttr.is_valid){
//                                                String kouzhao="";
//                                                switch (detectionResult.faceList[i].mouthOccAttr.mouth_occ_status){
//                                                    case 0:
//                                                        kouzhao="未佩戴口罩";
//                                                        break;
//                                                    case 1:
//                                                        kouzhao="面具遮挡";
//                                                        break;
//                                                    case 2:
//                                                        kouzhao="已佩戴口罩";
//                                                        break;
//                                                    case 3:
//                                                        kouzhao="其他遮挡";
//                                                        break;
//                                                    default:
//                                                        kouzhao="";
//                                                        break;
//                                                }
//                                                showUIResult(3,"陌生人","",kouzhao);
//                                            }
//                                         //   final Bitmap fileBitmap = nv21ToBitmap.nv21ToBitmap(images.image, images.width, images.height);
//                                            //  String paths = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ruitongzipmbj";
//                                            //  boolean tt = nv21ToBitmap.saveBitmap(fileBitmap, paths, time + ".png");
//                                         //   link_shangchuanshualian(subject.getSid(), fileBitmap, subject.getPeopleType() + "");
//                                            //  if (tt) {
//                                            //     subject.setZpPath(paths + File.separator + time + ".png");
//                                            //     Log.d("RecognizeThread", "subjectBox.put(subject):" + subjectBox.put(subject));
//                                            //   }
//                                            break;
//                                        }
//                                    }
                                    showUIResult(3,"陌生人","");
                                    DengUT.isOPEN = true;


                                    //   msrBitmap = nv21ToBitmap.nv21ToBitmap(result.feedback.rgbImage.image, result.feedback.rgbImage.width, result.feedback.rgbImage.height);
                                    //   Log.d("RecognizeThread", "入库"+tID);
                                }
                            }

                        }
                    }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }

    }



    @Override
    protected void onStop() {
        Log.d("MianBanJiActivity3", "停止");
        if (manager != null) {
            manager.release();
        }
//        if (manager2 != null) {
//            manager2.release();
//        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "onRestart");
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MianBanJiActivity3", "onStart");
    }


    @Override
    protected void onDestroy() {
        Log.d("MianBanJiActivity3", "onDestroy");
//
//        if (mReadThread != null) {
//            mReadThread.interrupt();
//        }
        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
        }
        if (mFeedFrameThread != null) {
            mFeedFrameThread.isIterrupt = true;
            mFeedFrameThread.interrupt();
        }

        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }
        if (mReadThread3 != null) {
            mReadThread3.isIterrupt=true;
            mReadThread3.interrupt();
        }

        if (mRecognizeThread != null) {
            mRecognizeThread.isInterrupt = true;
            mRecognizeThread.interrupt();
        }

        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(netWorkStateReceiver);
        EventBus.getDefault().unregister(this);//解除订阅
        if (manager != null) {
            manager.release();
        }


        if (task != null)
            task.cancel();
        timer.cancel();


       // if (task2 != null)
        //    task2.cancel();
        timer2.cancel();


        DengUT.getInstance(baoCunBean).closeWrite();
        DengUT.getInstance(baoCunBean).closeGreen();
        DengUT.getInstance(baoCunBean).closeRed();

        super.onDestroy();

        MyApplication.myApplication.removeActivity(this);
    }

    private static final int REQUEST_CODE_CHOOSE_PICK = 1;


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_MENU) {
//              // startActivity(new Intent(MianBanJiActivity3.this, SheZhiActivity2.class));
//              //  finish();
//            }
//
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {

        if (event.equals("ditu123")) {
            // if (baoCunBean.getTouxiangzhuji() != null)
            //    daBg.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getTouxiangzhuji()));
            baoCunBean = baoCunBeanDao.get(123456L);

            //   Log.d("MainActivity101", "dfgdsgfdgfdgfdg");
            return;
        }

        if (event.equals("kaimen")) {
            menjing1();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(jidianqi);
                    menjing2();
                }
            }).start();
            return;
        }
        if (event.equals("guanbimain")) {
            finish();
            return;
        }
        if (event.equals("configs")){
            //更新配置
            baoCunBean = baoCunBeanDao.get(123456L);
            if (baoCunBean.getWenzi1() == null) {
                tvTitle_Ir.setText("请设置公司名称");
            } else {
                tvTitle_Ir.setText(baoCunBean.getWenzi1());
            }
            if (baoCunBean.getJidianqi()!=0){
                jidianqi=baoCunBean.getJidianqi();
            }
            if (baoCunBean.getMoshengrenPanDing()!=0){
                cishu=baoCunBean.getMoshengrenPanDing();
            }
            if (baoCunBean.getLogo()!=null){
                try {
                    logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        if (event.equals("mFacePassHandler")) {
            paAccessControl = MyApplication.myApplication.getFacePassHandler();
            return;
        }

        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //mianBanJiView.setTime(DateUtils.time(System.currentTimeMillis()+""));
                    // String riqi11 = DateUtils.getWeek(System.currentTimeMillis()) + "   " + DateUtils.timesTwo(System.currentTimeMillis() + "");
                    //  riqi.setTypeface(tf);
                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("03") && xiaoshiss.split(":")[1].equals("40")) {
                     Log.d("TimeChangeReceiver", "ssss");
                        List<Subject> subjects= subjectBox.getAll();
                        for (Subject subject:subjects){
                            try {
                                if (TIME==null)
                                    return;
                                long times1=DateUtils.date2TimeStamp(subject.getBirthday());
                                long times2=DateUtils.date2TimeStamp(TIME);
                                if (times1<times2){
                                    if (paAccessControl!=null){
                                        paAccessControl.deleteFace(subject.getTeZhengMa().getBytes());
                                        subjectBox.remove(subject);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //1分钟一次指令获取
                    Log.d("TimeChangeReceiver", "isGET:" + isGET);
                    if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("")) {
                        if (isGET){
                            isGET=false;
                            link_get_zhiling();
                        }
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(20000);
                            if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("")) {
                                link_infoSync();
                            }
                        }
                    }).start();

                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    //获取指令
    private void link_get_zhiling() {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            return;
        }
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .get()
                .url(baoCunBean.getHoutaiDiZhi()+"/faceinfos-model/list");
               // .url(baoCunBean.getHoutaiDiZhi() + "/app/getCommands?" + "serialnumber=" + JHM);
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                    isGET=true;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "获取指令:" + ss);
                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                    Gson gson = new Gson();
                    ZhiLingBean commandsBean = gson.fromJson(jsonObject, ZhiLingBean.class);
                    if (commandsBean != null && commandsBean.getCode() == 200) {
                        TIME=commandsBean.getSystemTime();
                        for (ZhiLingBean.DataBean resultBean : commandsBean.getData()) {
                            if (xueliehao!=null && xueliehao.equals(resultBean.getEquipmentID())){
                                linkedBlockingQueue.put(resultBean);//对于put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
                            }
                        }
//                        if (linkedBlockingQueue.size()==0){

//                        }
                      //  Log.d("MianBanJiActivity3", "linkedBlockingQueue.size():" + linkedBlockingQueue.size());
                        isGET=true;
                    }else {
                        isGET=true;
                    }
                } catch (Exception e) {
                    isGET=true;
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                }
            }
        });
    }


    //上传记录
    private void link_shangchuanshualian(String empId, Bitmap bitmap, String verifyStatus) {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            return;
        }
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put("empId", empId);
            object.put("verifyStatus", verifyStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(object);

        Log.d("MianBanJiActivity3", "上传记录：" + array.toString());
        RequestBody body = RequestBody.create(array.toString(), JSON);
      //  Log.d("AllConnects", baoCunBean.getHoutaiDiZhi());
      //  Bitmap bb = BitmapUtil.rotateBitmap(bitmap, SettingVar.msrBitmapRotation);
//        RequestBody body = null;
//        body = new FormBody.Builder()
//                .add("id", id + "")
//                .add("pepopleType", pepopleType)
//                .add("serialnumber", JHM)
//                .add("iamge", Bitmap2StrByBase64(bb))
//                .build();
   //     Log.d("MianBanJiActivity3", "id:"+id);
    //    Log.d("MianBanJiActivity3", "pepopleType:"+pepopleType);
    //    Log.d("MianBanJiActivity3", "Bitmap2StrByBase64:"+Bitmap2StrByBase64(bb));
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/faceinfos-model/checkoutResult");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());

                soundPool.play(musicId.get(7), 1, 1, 0, 0, 1);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "上传识别记录" + ss);
                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    if (jsonObject.get("code").getAsInt()==200){
                        soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                soundPool.play(musicId.get(6), 1, 1, 0, 0, 1);
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            soundPool.play(musicId.get(6), 1, 1, 0, 0, 1);
                        }
                    });
                }
            }
        });
    }

//    //拿时间
//    private void link_time(String id,String name) {
//        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
//            return;
//        }
//        RequestBody body = null;
//        body = new FormBody.Builder()
//                .add("id", id)
//                .add("name", name)
//                .add("serialnumber", JHM)
//                .build();
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .post(body)
//                .url(baoCunBean.getHoutaiDiZhi() + "/app/updateIDcardTake");
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
//            }
//
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//
//                    Log.d("AllConnects", "上传识别记录" + ss);
//
//                } catch (Exception e) {
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//
//                }
//            }
//        });
//    }

    //数据同步
    private void link_infoSync() {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONArray array = new JSONArray();
        List<HuiFuBean> huiFuBeanList = huiFuBeanBox.getAll();
        if (huiFuBeanList != null) {
            if (huiFuBeanList.size()==0){
                return;
            }
            for (HuiFuBean bean : huiFuBeanList) {
                JSONObject object = new JSONObject();
                try {
                    object.put("id", bean.getPepopleId());
                    object.put("status", bean.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(object);
            }
        }else {
            return;
        }


        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
        RequestBody body = RequestBody.create(array.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi()+"/faceinfos-model/checkPhotoStorage");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "数据同步:" + ss);
                    JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                    if (jsonObject.get("code").getAsInt()==200){
                        for (HuiFuBean bean : huiFuBeanList) {
                            huiFuBeanBox.remove(bean);
                        }
                    }
                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
                }
            }
        });
    }

    //信鸽信息处理
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(XGBean xgBean) {


    }


    private void guanPing() {
        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 0);
        this.sendBroadcast(intent);
        sendBroadcast(new Intent("com.android.internal.policy.impl.hideNavigationBar"));
        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusclose"));
        try {
         Lztek lztek=Lztek.create(MyApplication.myApplication);
         lztek.navigationBarSlideShow(false);
         lztek.hideNavigationBar();
        }catch (NoClassDefFoundError e){
            e.printStackTrace();
        }
        try {
            HwitManager.HwitSetHideSystemBar(MianBanJiActivity3.this);
            HwitManager.HwitSetDisableSlideShowSysBar(1);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }
    }

    private void menjing1() {
        // TPS980PosUtil.setJiaJiPower(1);
        DengUT.getInstance(baoCunBean).openDool();
      //  TPS980PosUtil.setRelayPower(1);
        Log.d("MianBanJiActivity3", "打开");
    }

    private void menjing2() {
        //  TPS980PosUtil.setJiaJiPower(0);
        DengUT.getInstance(baoCunBean).closeDool();
       // TPS980PosUtil.setRelayPower(0);
        Log.d("MianBanJiActivity3", "关闭");
    }



    private void init_NFC() {

        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();
        if (mNfcAdapter == null) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        } else if (!mNfcAdapter.isEnabled()) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先去设置里面打开NFC开关", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {

        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
                processIntent(this.getIntent());
            }
        }

    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void processIntent(Intent intent) {
        //  String data = null;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // String[] techList = tag.getTechList();
        // Log.d("Mian", "tag.describeContents():" + tag.describeContents());
        byte[] ID;
        //  data = tag.toString();
        if (tag == null)
            return;
        ID = tag.getId();
//        data += "\n\nUID:\n" + byteToString(ID);
//        data += "\nData format:";
//        for (String tech : techList) {
//            data += "\n" + tech;
//        }
//         Log.d("MianBanJiActivity3", byteToString(ID));
        String sdfds = byteToString(ID);
        if (sdfds != null) {
            Log.d("MianBanJiActivity3", sdfds);
            sdfds = sdfds.toUpperCase();
            List<IDCardBean> idCardBeanList = idCardBeanBox.query().equal(IDCardBean_.idCard, sdfds).build().find();
            if (idCardBeanList.size() > 0) {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证成功!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                DengUT.getInstance(baoCunBean).openDool();
                IDCardBean cardBean=idCardBeanList.get(0);
              //  link_shuaka(sdfds,cardBean.getName());
                //启动定时器或重置定时器
                if (task != null) {
                    task.cancel();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 222;
                            mHandler.sendMessage(message);
                        }
                    };
                    timer.schedule(task, 6000);
                } else {
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 222;
                            mHandler.sendMessage(message);
                        }
                    };
                    timer.schedule(task, 6000);
                }

                IDCardTakeBean takeBean=new IDCardTakeBean();
                takeBean.setIdCard(sdfds);
                takeBean.setName(cardBean.getName());
                takeBean.setTime(System.currentTimeMillis());
                idCardTakeBeanBox.put(takeBean);

            } else {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证失败!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
            }
        }


    }

    /**
     * 将byte数组转化为字符串
     *
     * @param src
     * @return
     */
    public static String byteToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            // System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

//            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
//
//            //获得ConnectivityManager对象
//            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            //获取所有网络连接的信息
//            if (connMgr!=null){
//                Network[] networks = connMgr.getAllNetworks();
//                //用于存放网络连接信息
//                StringBuilder sb = new StringBuilder();
//                //通过循环将网络信息逐个取出来
//                Log.d("MianBanJiActivity3", "networks.length:" + networks.length);
//                if (networks.length == 0) {
//                    //没网
//                    Log.d("MianBanJiActivity3", "没网2");
////                    if (serverManager != null) {
////                        serverManager.stopServer();
////                        serverManager = null;
////                    }
//                }
//                for (Network network : networks) {
//                    //获取ConnectivityManager对象对应的NetworkInfo对象
//                    NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
//                    if (networkInfo!=null && networkInfo.isConnected()) {
//                        //连接上
//                        Log.d("MianBanJiActivity3", "有网2");
////                        if (serverManager != null) {
////                            serverManager.stopServer();
////                            serverManager = null;
////                        }
////                        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
////                        serverManager.setMyServeInterface(MianBanJiActivity3.this);
////                        serverManager.startServer();
//                        break;
//                    }
//                }
//            }
        }
    }


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }



    /**
     * 显示结果UI
     *
     * @param state 状态 1 初始状态  2 识别中,出现提示语  3 识别失败  4 识别成功
     */
    protected void showUIResult(final int state, final String name, final String detectFaceTime) {
        if (state==STATE){
            return;
        }else {
            STATE=state;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //  Log.d("MianBanJiActivity3", "state:" + state);
                switch (state) {
                    case 1: {//初始状态
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.INVISIBLE);//识别结果大框
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.GONE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("");
                        break;
                    }
                    case 2: {//识别中,出现提示语
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("识别中,请稍后...");
                        break;
                    }
                    case 3: {//识别失败
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.VISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.VISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.VISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.error_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("无权限通过,请重试");
                        //   tvFaceTips_Ir.setText(kouzhao);
                        break;
                    }
                    case 4: {//识别成功
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.VISIBLE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText(name);
                        tvTime_Ir.setText(detectFaceTime);
                        //   tvFaceTips_Ir.setText(kouzhao);
                        break;
                    }
                }
            }
        });
    }


}
