package com.gxy.utils;

/**
 * @Classname ResponseUtil
 * @Date 2024/10/29
 * @Created by guoxinyu
 */
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ResponseUtil {
    private static final Gson GSON = new GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .disableHtmlEscaping()
            .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<>())
            .create();

    public static <T> String getResponseJson(ResponseModel<T> responseModel) {
        return GSON.toJson(responseModel);
    }

    /**
     * 获取返回成功的Json
     *
     * @param data        返回的数据
     * @param paginations 分页的信息
     * @return 成功的Json
     */
    public static <T> String getSuccessJson(T data, Pagination... paginations) {
        ResponseModel<T> responseModel = new ResponseModel<>();
        responseModel.setSuccess(true);
        responseModel.setData(data);
        if (null != paginations && paginations.length > 0) {
            responseModel.setPagination(paginations[0]);
        }
        return GSON.toJson(responseModel);
    }

    /**
     * 获取失败返回的Json
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 获取失败返回的Json
     */
    public static String getErrorJson(Integer errorCode, String errorMsg) {
        ResponseModel<Object> responseModel = new ResponseModel<>();
        responseModel.setSuccess(false);
        responseModel.setCode(errorCode);
        responseModel.setErrorMsg(errorMsg);
        return GSON.toJson(responseModel);
    }

    static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<T> rawType = (Class<T>) typeToken.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringNullAdapter();
        }
    }

    static class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.value("");
            } else {
                writer.value(value);
            }
        }
    }
}