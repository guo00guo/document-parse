package com.mooctest.data.enums;

/**
 * @author guochao
 * @date 2020-05-26 11:49
 */
public enum JustificationEnum {
    LEFT(0),
    CENTER(1),
    RIGHT(2),
    LEFT_AND_RIGHT(3);

    private int value;

    JustificationEnum(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static String getJustification(int value){
        switch (value){
            case 0:return "LEFT";
            case 1:return "CENTER";
            case 2:return "RIGHT";
            case 3:return "LEFT AND RIGHT";
        }
        return null;
    }
}
