package main.java.com.mooctest.data.response;

/**
 * Created on 2018/10/9
 */
public enum ServerCode {

    SUCCESS(20000,"OK"),

    //通用错误状态码,30000-39999
    PARAM_WRONG(30000,"参数错误"),
    ENTITY_NOT_A_COURSE(30001, "找不到该课程相关信息"),
    ENTITY_NOT_A_COURSE_RESOURCE(30002, "找不到该课程资源相关信息"),

    //考试相关状态码: 40000-49999
    EXAM_ENDED(40000,"考试已结束"),

    //分数相关状态码：50000-59999
    UPLOAD_RECORD_FAIL(50000,"存储提交记录失败"),
    NO_APPLY_FOUND(50001,"未找到申请提交记录"),

    //系统错误：60000-69999
    SYSTEM_ERROR(60000,"系统错误"),
    DATA_NOT_CONSISTENT(60001,"数据不一致"),

    //Node相关错误： 70000-79999
    META_NODE_NOT_EXIST(70000,"元node不存在"),

    //用户相关错误： 80000-89999
    NO_USER_FOUND(80000, "无法获取用户参数");

    private int code;
    private String msg;
    ServerCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
