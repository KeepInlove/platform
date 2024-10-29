package com.gxy.utils;

import java.io.Serializable;

/**
 * @Classname ResponseModel
 * @Date 2024/10/29
 * @Created by guoxinyu
 */
public class ResponseModel<T> implements Serializable {

    private static final long serialVersionUID = -5040869630471982948L;
    /**
     * 成功返回的数据
     */
    private T data;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 状态码
     */
    private Integer code = 0;
    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 分页信息
     */
    private Pagination pagination;

    public ResponseModel() {
    }

    public ResponseModel(T data) {
        this.success = true;
        this.data = data;
    }

    public ResponseModel(Integer errorCode, String errorMsg) {
        this.success = false;
        this.code = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 成功的返回
     */
    public static <T> ResponseModel<T> success(T data) {
        return new ResponseModel<>(data);
    }

    /**
     * 错误的返回
     */
    public static <T> ResponseModel<T> error(int errorCode, String errorMsg) {
        return new ResponseModel<>(errorCode, errorMsg);
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
