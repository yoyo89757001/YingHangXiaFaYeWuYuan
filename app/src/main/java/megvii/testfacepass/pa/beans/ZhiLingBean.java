package megvii.testfacepass.pa.beans;

import java.util.List;

public class ZhiLingBean {


    /**
     * code : 200
     * count : 3
     * data : [{"id":13,"finOrgId":34,"empId":179,"name":"张汝棉","photoURL":"http://pandebug.xiaomy.net/307836d5269f4a14a388a75f486196bd.jfif","faceVerifyDate":"2020-04-03","status":1,"createTime":"2020-04-02 15:53:02","updateTime":"2020-04-02 15:53:02"},{"id":14,"finOrgId":34,"empId":177,"name":"张仲","photoURL":"http://pandebug.xiaomy.net/ff60c03a0a9d491d851b50afce777479.jfif","faceVerifyDate":"2020-04-02","status":1,"createTime":"2020-04-01 16:00:01","updateTime":"2020-04-01 16:00:01"},{"id":15,"finOrgId":34,"empId":182,"name":"简学勇","photoURL":"http://pandebug.xiaomy.net/f0dfa40a10b4462bb913c54f3d8653d3.jfif","faceVerifyDate":"2020-04-02","status":1,"createTime":"2020-04-01 16:00:01","updateTime":"2020-04-01 16:00:01"}]
     * msg : 操作成功
     */

    private int code;
    private int count;
    private String msg;
    private String systemTime;
    private List<DataBean> data;

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 13
         * finOrgId : 34
         * empId : 179
         * name : 张汝棉
         * photoURL : http://pandebug.xiaomy.net/307836d5269f4a14a388a75f486196bd.jfif
         * faceVerifyDate : 2020-04-03
         * status : 1
         * createTime : 2020-04-02 15:53:02
         * updateTime : 2020-04-02 15:53:02
         */

        private String id;
        private String finOrgId;
        private String empId;
        private String name;
        private String photoURL;
        private String faceVerifyDate;
        private int status;
        private String createTime;
        private String updateTime;
        private int operationStatus;//operationStatus: 0删除,1新增,2更新
        private String equipmentID;

        public String getEquipmentID() {
            return equipmentID;
        }

        public void setEquipmentID(String equipmentID) {
            this.equipmentID = equipmentID;
        }

        public int getOperationStatus() {
            return operationStatus;
        }

        public void setOperationStatus(int operationStatus) {
            this.operationStatus = operationStatus;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFinOrgId() {
            return finOrgId;
        }

        public void setFinOrgId(String finOrgId) {
            this.finOrgId = finOrgId;
        }

        public String getEmpId() {
            return empId;
        }

        public void setEmpId(String empId) {
            this.empId = empId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhotoURL() {
            return photoURL;
        }

        public void setPhotoURL(String photoURL) {
            this.photoURL = photoURL;
        }

        public String getFaceVerifyDate() {
            return faceVerifyDate;
        }

        public void setFaceVerifyDate(String faceVerifyDate) {
            this.faceVerifyDate = faceVerifyDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
