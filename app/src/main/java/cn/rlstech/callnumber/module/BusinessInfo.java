package cn.rlstech.callnumber.module;


public class BusinessInfo {

    public String id; // 业务id
    public String name; // 业务名称
    public String require; // 业务说明
    public String type;
    public String office_id;
    public String college_id;
    public String hall_id;
    public String createtime;
    public String queueCount; //等待人数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOffice_id() {
        return office_id;
    }

    public void setOffice_id(String office_id) {
        this.office_id = office_id;
    }

    public String getCollege_id() {
        return college_id;
    }

    public void setCollege_id(String college_id) {
        this.college_id = college_id;
    }

    public String getHall_id() {
        return hall_id;
    }

    public void setHall_id(String hall_id) {
        this.hall_id = hall_id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getQueueCount() {
        return queueCount;
    }

    public void setQueueCount(String queueCount) {
        this.queueCount = queueCount;
    }
}
