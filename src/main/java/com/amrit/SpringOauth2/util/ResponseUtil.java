package com.amrit.SpringOauth2.util;

import com.amrit.SpringOauth2.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseUtil {
    public static ApiResponse getFailureResponse(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse getFailureResponse(String message, Object data) {
        return ApiResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static ApiResponse getValidationFailureResponse(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static ApiResponse getValidationFailureResponse(String message, Object data) {
        return ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse getSuccessfulServerResponse(Object data, String message, String code) {
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> getSuccessfulServerResponseWithData(T data, String message, String code) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.OK.value());
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        apiResponse.setHttpStatus(HttpStatus.OK);
        apiResponse.setTimestamp(LocalDateTime.now());
        return apiResponse;
    }

    public static ApiResponse getTimeoutResponse(String message, Object data) {
        return ApiResponse.builder()
                .code(HttpStatus.REQUEST_TIMEOUT.value())
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.REQUEST_TIMEOUT)
                .timestamp(LocalDateTime.now())
                .build();

    }

    public static ApiResponse getSuccessfulApiResponse(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .httpStatus(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse getSuccessfulApiResponse(Object data, String message) {
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> getSuccessfulApiResponseWithData(T data, String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setCode(HttpStatus.OK.value());
        apiResponse.setMessage(message);
        apiResponse.setData(data);
        apiResponse.setHttpStatus(HttpStatus.OK);
        apiResponse.setTimestamp(LocalDateTime.now());
        return apiResponse;
    }

    public static ApiResponse getTimeoutApiResponse(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.REQUEST_TIMEOUT.value())
                .message(message)
                .httpStatus(HttpStatus.REQUEST_TIMEOUT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Object> getNotFoundResponse(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(message)
                .httpStatus(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static ApiResponse<Object> getBeanValidationFailureResponse(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static ApiResponse getUnAuthorized(String message) {
        return ApiResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
