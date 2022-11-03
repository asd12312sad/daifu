package com.ms.common.aop;

import com.ms.common.domain.R;
import com.ms.common.core.domain.ResultCode;
import com.ms.common.core.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 请求错误捕捉
 *
 * @param <T>
 * @author xiaobing
 */
@Slf4j
@RestControllerAdvice
public class DeployException<T> implements ResponseBodyAdvice<T> {
    @Override
    public T beforeBodyWrite(T t, MethodParameter arg1, MediaType arg2, Class<? extends HttpMessageConverter<?>> arg3,
                             ServerHttpRequest arg4, ServerHttpResponse arg5) {
        return t;
    }

    @Override
    public boolean supports(MethodParameter arg0, Class<? extends HttpMessageConverter<?>> arg1) {
        return true;
    }


    @ExceptionHandler(value = Exception.class)
    public R defaultErrorHandler(Exception e, HttpServletResponse response) throws Exception {
        this.Throwing(e);


        if (e instanceof BaseException) {
            response.setStatus(200);

            return R.failed(400, ((BaseException) e).getDefaultMessage());
        }


        //参数校验get
        else if (e instanceof ConstraintViolationException) {
            response.setStatus(200);

            StringBuffer sb = new StringBuffer();
            ConstraintViolationException exs = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                sb.append(item.getMessage() + "/");
            }
            return R.failed(400, "请求参数错误" + sb.toString());
        }

        //参数校验post
        if (e instanceof MethodArgumentNotValidException) {
            response.setStatus(200);

            MethodArgumentNotValidException exs = (MethodArgumentNotValidException) e;

            BindingResult bindingResult = exs.getBindingResult();
            StringBuffer sb = new StringBuffer();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                sb.append(fieldError.getDefaultMessage() + "/");
            }
            return R.failed(400, "请求参数错误" + sb.toString());

        }
       else if (e instanceof HttpRequestMethodNotSupportedException) {
            response.setStatus(200);
            return R.setToCode(ResultCode.ERROR);

        } else if (e instanceof HttpMessageNotReadableException) {
            response.setStatus(200);
            return R.setToCode(ResultCode.ERROR);


        } else {
            response.setStatus(200);
            return R.setToCode(ResultCode.ERROR);

        }

    }

    private void Throwing(Exception ex) {
        log.info("[信息]异常：" + ex.getMessage());
        log.info("[信息]异常：" + ex.getStackTrace());
        StringBuffer sb = new StringBuffer("");
        for (int k = 0; k < ex.toString().length() + 10; k++) {
            sb.append("*");
        }
        log.info(sb.toString());
        log.info("*");
        log.info("*     日志定位：error_" + ex.hashCode());
        log.info("*     异常信息：" + ex.toString());
        log.info("*     相关位置：");
        for (int i = 0; i < ex.getStackTrace().length; i++) {
            StackTraceElement stackTraceElement = ex.getStackTrace()[i];
            if (stackTraceElement.getClassName().startsWith("com.ms")) {
                log.info("*" + stackTraceElement.toString());
            }
        }
    }
}
