package com.rev.githubrepo.helpers;

import com.rev.githubrepo.api.model.ApiError;
import com.rev.githubrepo.ui.activities.GetUserIdActivity;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ApiError parseError(Response<?> response){
        Converter<ResponseBody, ApiError> converter =
                GetUserIdActivity.retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        }catch (IOException e){
            return new ApiError();
        }

        return error;
    }
}
