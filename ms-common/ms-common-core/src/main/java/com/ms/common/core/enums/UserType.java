package com.ms.common.core.enums;

/**
 * 用户类型
 *
 * @author XIAOBING
 */
public enum UserType {

    ADMIN("ADMIN", "总", 0),


    USER("SCHOOL", "学校", 1),
    TEACHER("TEACHER", "教师", 2),
    STUDENT("STUDENT", "用户", 3),

    FEIGN("FEIGN", "内部调用", 2),
    ADMINUSER("ADMINUSER", "总普通用户", 4),

    SCHOOLAPP("SCHOOLAPP", "学校", 5),
    TEACHERAPP("TEACHERAPP", "教师", 6);


    private final String code;
    private final Integer type;
    private final String info;

    UserType(String code, String info, Integer type) {
        this.code = code;
        this.info = info;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public Integer getType() {
        return type;
    }

    public static UserType getByCode(String code) {

        UserType[] values = UserType.values();

        for (UserType value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }

        return null;
    }
}
