//package megvii.testfacepass.pa.tuisong_jg;
//
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.text.TextUtils;
//import android.util.Log;
//import android.util.Patterns;
//
//import com.alibaba.fastjson.JSON;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.lztek.toolkit.Lztek;
//import com.pingan.ai.access.common.PaAccessControlMessage;
//import com.pingan.ai.access.entiry.PaAccessFaceInfo;
//import com.pingan.ai.access.manager.PaAccessControl;
//import com.pingan.ai.access.result.PaAccessDetectFaceResult;
//import com.yanzhenjie.andserver.annotation.GetMapping;
//import com.yanzhenjie.andserver.annotation.PostMapping;
//import com.yanzhenjie.andserver.annotation.QueryParam;
//import com.yanzhenjie.andserver.annotation.RequestBody;
//import com.yanzhenjie.andserver.annotation.RequestMapping;
//import com.yanzhenjie.andserver.annotation.RequestParam;
//import com.yanzhenjie.andserver.annotation.RestController;
//import com.yanzhenjie.andserver.framework.body.FileBody;
//import com.yanzhenjie.andserver.framework.body.StringBody;
//import com.yanzhenjie.andserver.http.HttpResponse;
//import com.yanzhenjie.andserver.http.multipart.MultipartFile;
//
//import net.lingala.zip4j.core.ZipFile;
//import net.lingala.zip4j.exception.ZipException;
//import net.lingala.zip4j.model.FileHeader;
//import net.lingala.zip4j.model.ZipParameters;
//import net.lingala.zip4j.util.Zip4jConstants;
//
//import org.greenrobot.eventbus.EventBus;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import io.objectbox.Box;
//import io.objectbox.query.LazyList;
//import jxl.Workbook;
//import jxl.WorkbookSettings;
//import jxl.format.Colour;
//import jxl.write.Label;
//import jxl.write.WritableCell;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableFont;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;
//import megvii.testfacepass.pa.MyApplication;
//import megvii.testfacepass.pa.beans.BaoCunBean;
//import megvii.testfacepass.pa.beans.BitahFaceBean;
//import megvii.testfacepass.pa.beans.ConﬁgsBean;
//import megvii.testfacepass.pa.beans.DaKaBean;
//import megvii.testfacepass.pa.beans.DaKaBean_;
//import megvii.testfacepass.pa.beans.IDBean;
//import megvii.testfacepass.pa.beans.IDCardBean;
//import megvii.testfacepass.pa.beans.IDCardBean_;
//import megvii.testfacepass.pa.beans.IDCardTakeBean;
//import megvii.testfacepass.pa.beans.IDCardTakeBean_;
//import megvii.testfacepass.pa.beans.IdsBean;
//import megvii.testfacepass.pa.beans.PersonsBean;
//import megvii.testfacepass.pa.beans.ResBean;
//import megvii.testfacepass.pa.beans.Subject;
//import megvii.testfacepass.pa.beans.Subject_;
//import megvii.testfacepass.pa.beans.WiﬁMsgBean;
//import megvii.testfacepass.pa.utils.BitmapUtil;
//import megvii.testfacepass.pa.utils.DateUtils;
//import megvii.testfacepass.pa.utils.FileUtil;
//import megvii.testfacepass.pa.utils.GsonUtil;
//
//
//@RestController
//public class MyService2 {
//
//    private static final String TAG = "MyService";
//    private static WritableFont arial14font = null;
//    private static WritableCellFormat arial14format = null;
//    private static WritableFont arial10font = null;
//    private static WritableCellFormat arial10format = null;
//    private static WritableFont arial12font = null;
//    private static WritableCellFormat arial12format = null;
//    private final static String UTF8_ENCODING = "UTF-8";
//
//    private Lztek lztek= Lztek.create(MyApplication.myApplication);
//
//    private Box<Subject> subjectBox  = MyApplication.myApplication.getSubjectBox();
//    private Box<DaKaBean> daKaBeanBox  = MyApplication.myApplication.getDaKaBeanBox();
//    private Box<IDCardBean> idCardBeanBox  = MyApplication.myApplication.getIdCardBeanBox();
//    private Box<IDCardTakeBean> idCardTakeBeanBox  = MyApplication.myApplication.getIdCardTakeBeanBox();
//    private PaAccessControl paAccessControl=PaAccessControl.getInstance();
//    private Box<BaoCunBean> baoCunBeanBox=MyApplication.myApplication.getBaoCunBeanBox();
//    private  String serialnumber= MyApplication.myApplication.getBaoCunBeanBox().get(123456).getJihuoma();
//    private BaoCunBean baoCunBean=MyApplication.myApplication.getBaoCunBeanBox().get(123456);
//    private  String pass= MyApplication.myApplication.getBaoCunBeanBox().get(123456).getJiaoyanmima();
//
//
//
//    @PostMapping("/setPassWord")
//    String setPassWord(@RequestParam(name = "oldPass") String oldPass ,
//                       @RequestParam(name = "newPass") String newPass){
//                if (oldPass==null || newPass==null || oldPass.trim().equals("") || newPass.trim().equals("")){
//                    return requsBean(400,true,"","参数验证失败");
//                }else {
//                    if (pass==null){
//                        //第一次设置
//                        if (newPass.equals(oldPass)){
//                            baoCunBean.setJiaoyanmima(newPass.trim());
//                            baoCunBeanBox.put(baoCunBean);
//                            pass=newPass.trim();
//                            return requsBean(1,true,oldPass);
//                        }else {
//                            return requsBean(400,true,"","密码不一致");
//                        }
//                    }else {
//                        //修改密码
//                        if (pass.equals(oldPass)){
//                            baoCunBean.setJiaoyanmima(newPass.trim());
//                            baoCunBeanBox.put(baoCunBean);
//                            pass=newPass.trim();
//                            return requsBean(1,true,newPass);
//                        }else {
//                            return requsBean(400,true,"","旧密码错误");
//                        }
//                    }
//                }
//
//    }
//
//
//    //    2.设备序列号获取
////    请求地址：   http://设备IP:8090/getDeviceKey
////    请求方法： POST
////    请求说明：
//    @GetMapping("/getDeviceKey")
//     String getDeviceKey(){
//       String ss= MyApplication.myApplication.getBaoCunBeanBox().get(123456).getJihuoma();
//       if (ss==null|| ss.equals("")){
//             return requsBean(-1,true,"","获取序列号失败");
//       }else {
//           return requsBean(1,true,ss,"获取序列号成功");
//       }
//
//    }
//
////3.设备配置
////    请求地址：  http://设备IP:8090/setConfig
////    请求方法： POST
//    @PostMapping("/setConfig")
//    String setConfig(@RequestParam(name = "pass") String pass ,
//                       @RequestParam(name = "conﬁg") String conﬁg){
//        if (pass!=null && pass.equals(this.pass)){
//            try {
//                if (conﬁg==null || conﬁg.equals("")){//为空
//                    return requsBean(400,true,"","参数验证失败");
//                }else {
//                    JsonObject jsonObject= GsonUtil.parse(conﬁg).getAsJsonObject();
//                    Gson gson=new Gson();
//                    ConﬁgsBean conﬁgsBean=gson.fromJson(jsonObject,ConﬁgsBean.class);
//                    if (conﬁgsBean.getRecRank()==1){
//                        baoCunBean.setHuoTi(false);
//                    }
//                    if (conﬁgsBean.getRecRank()==2){
//                        baoCunBean.setHuoTi(true);
//                    }
//                    if (conﬁgsBean.getSaveIdentifyTime()!=0){
//                     baoCunBean.setMoshengrenPanDing(conﬁgsBean.getSaveIdentifyTime());
//                    }
//                    if (conﬁgsBean.getIsStranger()==1){
//                        baoCunBean.setMsrPanDing(true);
//                    }
//                    if (conﬁgsBean.getIsStranger()==2){
//                        baoCunBean.setMsrPanDing(false);
//                    }
//                   if (conﬁgsBean.getIdentifyScores()!=0){
//                       baoCunBean.setShibieFaZhi(conﬁgsBean.getIdentifyScores());
//                   }
//                   if (null==conﬁgsBean.getCompanyName()){
//                       Log.d(TAG, "kong");
//                   }else {
//                       baoCunBean.setWenzi1(conﬁgsBean.getCompanyName());
//                   }
//                    if (conﬁgsBean.getIsOpenDoor()==1){
//                        baoCunBean.setShowShiPingLiu(true);
//                    }
//                    if (conﬁgsBean.getIsOpenDoor()==2){
//                        baoCunBean.setShowShiPingLiu(false);
//                    }
//                    if (conﬁgsBean.getRelayInterval()!=0){
//                        baoCunBean.setJidianqi(conﬁgsBean.getRelayInterval());
//                    }
//                    if (conﬁgsBean.getConfigModel()!=0){
//                        baoCunBean.setConfigModel(conﬁgsBean.getConfigModel());
//                    }
//
//                    baoCunBeanBox.put(baoCunBean);
//                    //发送广播更新配置  还没实现
//                    EventBus.getDefault().post("configs");
//                    return requsBean(1,true,"","设置成功");
//                }
//
//            }catch (Exception e){
//                return requsBean(400,true,e.getMessage()+"","参数异常");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////4.修改Logo
////    请求地址：  http://设备IP:8090/changeLogo
////    请求方法： POST
//    @PostMapping("/changeLogo")
//    String changeLogo(@RequestParam(name = "pass") String pass ,
//                     @RequestParam(name = "imgBase64") String imgBase64){
//        if (pass!=null && pass.equals(this.pass)){
//            if (imgBase64!=null && !imgBase64.equals("")){
//                baoCunBean.setLogo(imgBase64);
//                baoCunBeanBox.put(baoCunBean);
//                EventBus.getDefault().post("configs");
//                return requsBean(1,true,"","设置成功");
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////    5.设置设备时间
////    请求地址：  http://设备IP:8090/setTime
////    请求方法： POST
//    @PostMapping("/setTime")
//        String setTime(@RequestParam(name = "pass") String pass ,
//                          @RequestParam(name = "timestamp") String timestamp){
//            if (pass!=null && pass.equals(this.pass)){
//                if (timestamp!=null && !timestamp.equals("")){
//                    try {
//                        lztek.setSystemTime(Long.parseLong(timestamp));
//                        return requsBean(1,true,"","设置成功");
//                    }catch (Exception e){
//                        return requsBean(-1,true,e.getMessage()+"","设置失败");
//                    }
//                }else {
//                    return requsBean(400,true,"","参数验证失败");
//                }
//            }else {
//                return requsBean(401,true,"","签名校验失败");
//            }
//        }
//
////    有线网络配置
////    请求地址：  http://设备IP:8090/setNetInfo
////    请求方法： POST
//
//    @PostMapping("/setNetInfo")
//    String setNetInfo(@RequestParam(name = "pass") String pass ,
//                   @RequestParam(name = "isDHCPMod") String isDHCPMod,
//                      @RequestParam(name = "ip",required = false) String ip,
//                      @RequestParam(name = "gateway",required = false) String gateway,
//                      @RequestParam(name = "subnetMask",required = false) String subnetMask,
//                      @RequestParam(name = "DNS",required = false) String DNS){
//        if (pass!=null && pass.equals(this.pass)){
//            if (isDHCPMod!=null && !isDHCPMod.equals("0")){
//                try {
//                    lztek.setEthEnable(true);
//                    if (isDHCPMod.equals("1")){ //自动获取ip
//                        lztek.setEthDhcpMode();
//                        return requsBean(1,true,"","设置成功");
//                    }else if (isDHCPMod.equals("2")){
//                        if (ip!=null && gateway!=null && subnetMask!=null && DNS!=null){
//                            lztek.setEthIpAddress(ip,subnetMask,gateway,DNS);
//                            return requsBean(1,true,"","设置成功");
//                        }else {
//                            return requsBean(400,true,"","参数验证失败");
//                        }
//                    }else {
//                        return requsBean(400,true,"","参数验证失败");
//                    }
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","设置失败");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
//
////6.Wi-Fi配置
////    请求地址：  http://设备IP:8090/setWifi
////    请求方法： POST
//    @PostMapping("/setWifi")
//    String setWifi(@RequestParam(name = "pass") String pass ,
//                   @RequestParam(name = "wiﬁMsg") String wiﬁMsg){
//        if (pass!=null && pass.equals(this.pass)){
//            if (wiﬁMsg!=null && !wiﬁMsg.equals("")){
//                try {
//                    JsonObject jsonObject= GsonUtil.parse(wiﬁMsg).getAsJsonObject();
//                    Gson gson=new Gson();
//                    WiﬁMsgBean wiﬁMsgBean=gson.fromJson(jsonObject,WiﬁMsgBean.class);
//
//                    return requsBean(1,true,"","设置成功");
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","设置失败");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
//
////5.设备重启
////    请求地址：   http://设备IP:8090/restartDevice
////    请求方法： POST
////    请求数据：
//    @PostMapping("/restartDevice")
//    String restartDevice(@RequestParam(name = "pass") String pass ){
//        if (pass!=null && pass.equals(this.pass)){
//          ;
//            lztek.hardReboot();
//            return requsBean(1,true,"","设置成功");
//
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
//
//    //6.设备重置
//    //    请求地址：   http://设备IP:8090/device/reset
//    //    请求方法： POST
//    //    请求数据：
//    @PostMapping("/device/reset")
//    String reset(@RequestParam(name = "pass") String pass ){
//        if (pass!=null && pass.equals(this.pass)){
//            ;
//            paAccessControl.stopFrameDetect();
//            paAccessControl.deleteAllFaces();
//            subjectBox.removeAll();
//            paAccessControl.startFrameDetect();
//            return requsBean(1,true,"","设置成功");
//
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////    7.设备开门
////    请求地址：   http://设备IP:8090/device/openDoorControl
////    请求方法： POST
//    @PostMapping("/openDoorControl")
//    String openDoorControl(@RequestParam(name = "pass") String pass ){
//        if (pass!=null && pass.equals(this.pass)){
//            EventBus.getDefault().post("kaimen");
//            return requsBean(1,true,"","设置成功");
//
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////    8.识别回调配置
////    请求地址：   http://设备IP:8090/setIdentifyCallBack
////    请求方法： POST
//    @PostMapping("/setIdentifyCallBack")
//    String setIdentifyCallBack(@RequestParam(name = "pass") String pass,
//                               @RequestParam(name = "url") String url){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (url!=null && !url.equals("")){
//                if (isValidUrl(url)){//是url
//                    baoCunBean.setHoutaiDiZhi(url);
//                    baoCunBeanBox.put(baoCunBean);
//                    EventBus.getDefault().post("kaimen");
//                    return requsBean(1,true,"","设置成功");
//                }else {
//                    return requsBean(400,true,"","参数验证失败");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//    private boolean isValidUrl(String url){
//        return !TextUtils.isEmpty(url) && url.matches(Patterns.WEB_URL.pattern());
//    }
//
////9.心跳地址配置
////    请求地址：   http://设备IP:8090/setDeviceHeartBeat
////    请求方法： POST
//    @PostMapping("/setDeviceHeartBeat")
//    String setDeviceHeartBeat(@RequestParam(name = "pass") String pass,
//                               @RequestParam(name = "url") String url){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (url!=null && !url.equals("")){
//                if (isValidUrl(url)){//是url
//                    baoCunBean.setXintiaoDIZhi(url);
//                    baoCunBeanBox.put(baoCunBean);
//                    EventBus.getDefault().post("kaimen");
//                    return requsBean(1,true,"","设置成功");
//                }else {
//                    return requsBean(400,true,"","参数验证失败");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////    10.人员创建
////    请求地址：   http://设备IP:8090/person/create
////    请求方法： POST
//    @PostMapping("/person/create")
//    String create(@RequestParam(name = "pass") String pass,
//                              @RequestParam(name = "person") String person){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (person!=null && !person.equals("")){
//                try {
//                    JsonObject jsonObject= GsonUtil.parse(person).getAsJsonObject();
//                    Gson gson=new Gson();
//                    PersonsBean personsBean=gson.fromJson(jsonObject,PersonsBean.class);
//                    if (null==personsBean.getId() || null==personsBean.getName())
//                        return requsBean(400,true,"id为空","参数验证失败");
//                    Subject subject=new Subject();
//                    subject.setTeZhengMa(personsBean.getId());
//                    subject.setName(personsBean.getName());
//                    subject.setIdcardNum(personsBean.getIdcardNum());
//                    subject.setEntryTime(personsBean.getExpireTime());
//                    subjectBox.put(subject);
//                    return requsBean(1,true,personsBean.getId(),"设置成功");
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","参数异常");
//                }
//
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////11.人员更新
////    请求地址：   http://设备IP:8090/person/update
////    请求方法： POST
//    @PostMapping("/person/update")
//    String update(@RequestParam(name = "pass") String pass,
//                  @RequestParam(name = "person") String person){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (person!=null && !person.equals("")){
//                try {
//                    JsonObject jsonObject= GsonUtil.parse(person).getAsJsonObject();
//                    Gson gson=new Gson();
//                    PersonsBean personsBean=gson.fromJson(jsonObject,PersonsBean.class);
//                    if (null==personsBean.getId() || null==personsBean.getName())
//                        return requsBean(400,true,"id为空","参数验证失败");
//                    Subject subject=new Subject();
//                    subject.setId(System.currentTimeMillis());
//                    subject.setTeZhengMa(personsBean.getId());
//                    subject.setName(personsBean.getName());
//                    subject.setIdcardNum(personsBean.getIdcardNum());
//                    subjectBox.put(subject);
//                    return requsBean(1,true,personsBean.getId(),"设置成功");
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","参数异常");
//                }
//
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////12.人员删除（批量）
////    请求地址：   http://设备IP:8090/person/delete
////    请求方法： POST
//    @PostMapping("/person/delete")
//    String delete(@RequestParam(name = "pass") String pass,
//                  @RequestParam(name = "id") String id){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (id!=null && !id.equals("")){
//                try {
//                    StringBuilder stringBuffer1=new StringBuilder();
//                    StringBuilder stringBuffer2=new StringBuilder();
//
//                    if (id.equals("-1")){
//                       LazyList<Subject> subjectLazyList= subjectBox.query().build().findLazy();
//                       for (Subject subject:subjectLazyList){
//                           stringBuffer1.append(subject.getTeZhengMa());
//                           stringBuffer1.append(",");
//                       }
//                       subjectBox.removeAll();
//                       paAccessControl.stopFrameDetect();
//                       paAccessControl.deleteAllFaces();
//                       paAccessControl.startFrameDetect();
//                       return requsBean(1, true, new IdsBean(stringBuffer1.toString(),stringBuffer2.toString()), "删除成功");
//                    }else {
//                        String[] strings=id.split(",");
//                        paAccessControl.stopFrameDetect();
//                        for (String string : strings) {
//                            stringBuffer1.append(string);
//                            stringBuffer1.append(",");
//                            List<Subject> sus = subjectBox.query().equal(Subject_.teZhengMa, string).build().find();
//                            for (Subject ss : sus) {
//                                try {
//                                    subjectBox.remove(ss);
//                                    paAccessControl.deleteFaceById(ss.getTeZhengMa());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    stringBuffer2.append(ss);
//                                    paAccessControl.startFrameDetect();
//                                }
//                            }
//                        }
//                        paAccessControl.startFrameDetect();
//                        return requsBean(1, true, new IdsBean(stringBuffer1.toString(),stringBuffer2.toString()), "删除成功");
//                    }
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","参数异常");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
//
////    13.人员分页查询
////    请求地址：   http://设备IP:8090/person/findByPage
////    请求方法： post
//    @PostMapping("/person/findByPage")
//    String findByPage(@RequestParam(name = "pass") String pass,
//                  @RequestParam(name = "length") String length,
//                      @RequestParam(name = "index") String index){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (length!=null){
//                try {
//                    int ind=Integer.parseInt(index);
//                    int len=Integer.parseInt(length);
//                    JsonArray jsonArray=new JsonArray();
//                   List<Subject> subjectList= subjectBox.query().build().find(ind,len);
//                    for (Subject subject:subjectList){
//                        PersonsBean personsBean=new PersonsBean();
//                        personsBean.setId(subject.getTeZhengMa());
//                        personsBean.setName(subject.getName());
//                        personsBean.setIdcardNum(subject.getIdcardNum());
//                        personsBean.setExpireTime(subject.getEntryTime());
//                        jsonArray.add(JSON.toJSONString(personsBean));
//                    }
//                    return requsBean(1,true,jsonArray,"获取成功");
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","参数异常");
//                }
//
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////    18.人员信息查询
////    请求地址：  http://设备IP:8090/person/find
////    请求方法： POST
//@PostMapping("/person/find")
//String find(@RequestParam(name = "pass") String pass,
//                  @RequestParam(name = "id") String id){
//    if (pass!=null && pass.equals(this.pass)){
//        ;if (id!=null){
//            try {
//                List<Subject> subjectList= subjectBox.query().equal(Subject_.teZhengMa,id).build().find();
//                PersonsBean personsBean=new PersonsBean();
//                personsBean.setId(subjectList.get(0).getTeZhengMa());
//                personsBean.setName(subjectList.get(0).getName());
//                personsBean.setIdcardNum(subjectList.get(0).getIdcardNum());
//                personsBean.setExpireTime(subjectList.get(0).getEntryTime());
//                return requsBean(1,true,personsBean,"获取成功");
//            }catch (Exception e){
//                return requsBean(-1,true,e.getMessage()+"","参数异常");
//            }
//
//        }else {
//            return requsBean(400,true,"","参数验证失败");
//        }
//    }else {
//        return requsBean(401,true,"","签名校验失败");
//    }
//}
//
//
////19.人员有效期设置
////    请求地址：   http://设备IP:8090/person/permissionsCreate
////    请求方法： POST
//    @PostMapping("/person/permissionsCreate")
//    String permissionsCreate(@RequestParam(name = "pass") String pass,
//                            @RequestParam(name = "personId") String personId,
//                             @RequestParam(name = "time") String time){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (personId!=null && !personId.equals("") && time!=null && !time.equals("")){
//                try {
//                    long ti = Long.parseLong(time);
//                    List<Subject> subjectList= subjectBox.query().equal(Subject_.teZhengMa,personId).build().find();
//                    for (Subject subject:subjectList){
//                        subject.setEntryTime(ti);
//                    }
//                    subjectBox.put(subjectList);
//
//                    return requsBean(1,true,"","设置成功");
//                }catch (Exception e){
//                    return requsBean(-1,true,e.getMessage()+"","参数异常");
//                }
//
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
////    20.照片注册（base64）
////    请求地址：  http://设备IP:8090/face/create
////    请求方法： POST
//    @PostMapping("/face/create")
//    String facecreate(@RequestParam(name = "pass") String pass,
//                             @RequestParam(name = "personId") String personId,
//                             @RequestParam(name = "faceId") String faceId,
//                            @RequestParam(name = "imgBase64") String imgBase64){
//        if (pass!=null && pass.equals(this.pass)){
//            ;if (personId!=null && !personId.equals("") && imgBase64!=null && !imgBase64.equals("") && faceId!=null && !faceId.equals("")){
//                try {
//                    Subject subject= subjectBox.query().equal(Subject_.teZhengMa,personId).build().findUnique();
//                    if (subject==null)
//                        return requsBean(-1, true, "", "未找到该人员");
//                    paAccessControl.stopFrameDetect();
//                   Bitmap bitmap=BitmapUtil.base64ToBitmap(imgBase64);
//                    PaAccessDetectFaceResult detectResult = paAccessControl.
//                            detectFaceByBitmap(bitmap,PaAccessControl.getInstance().getPaAccessDetectConfig());
//                        Log.d(TAG, "detectResult:" + detectResult);
//                    if (detectResult!=null && detectResult.message== PaAccessControlMessage.RESULT_OK) {
//                        BitmapUtil.saveBitmapToSD(bitmap, MyApplication.SDPATH3, faceId + ".png");
//                        //先查询有没有
//                        try {
//                            Subject subject1= subjectBox.query().equal(Subject_.faceIds1,faceId).build().findUnique();
//                            Subject subject2= subjectBox.query().equal(Subject_.faceIds2,faceId).build().findUnique();
//                            Subject subject3= subjectBox.query().equal(Subject_.faceIds3,faceId).build().findUnique();
//                            if (subject1==null){
//                                subject.setFaceIds1(faceId);
//                            }else if (subject2==null){
//                                subject.setFaceIds2(faceId);
//                            }else if (subject3==null){
//                                subject.setFaceIds3(faceId);
//                            }else {//都不为空
//                                return requsBean(-1, true, "", "注册失败,该人员超过三张注册照片,请先删除其中一张");
//                            }
//                            PaAccessFaceInfo face = paAccessControl.queryFaceById(faceId);
//                            if (face != null) {
//                                paAccessControl.deleteFaceById(face.faceId);
//                            }
//                            paAccessControl.addFace(faceId, detectResult.feature, MyApplication.GROUP_IMAGE);
//                            subjectBox.put(subject);
//                            paAccessControl.startFrameDetect();
//                            return requsBean(1, true, "", "注册成功");
//                        } catch (Exception e) {
//                            paAccessControl.startFrameDetect();
//                            return requsBean(-1, true, e.getMessage() + "", "参数异常");
//                        }
//                    }else {
//                        return requsBean(-1, true, "", "照片不符合入库标准");
//                    }
//                } catch (Exception e) {
//                    paAccessControl.startFrameDetect();
//                    return requsBean(-1, true, e.getMessage() + "", "参数异常");
//                }
//            }else {
//                return requsBean(400,true,"","参数验证失败");
//            }
//        }else {
//            return requsBean(401,true,"","签名校验失败");
//        }
//    }
//
//
//    private String requsBean(int result,boolean success,Object data){
//        return JSON.toJSONString(new ResBean(result,success,data));
//    }
//    private String requsBean(int result,boolean success,Object data,String msg){
//        return JSON.toJSONString(new ResBean(result,success,data,msg));
//    }
//}
