package edu.tianjinagriculture.yunchunapp.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotRoleException;
import edu.tianjinagriculture.yunchunapp.commom.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotRoleException.class)
    public Result<String> notRoleException(NotRoleException e){
        SaHolder.getResponse().setStatus(403);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handle(RuntimeException e){
        SaHolder.getResponse().setStatus(400);
        return Result.error(e.getMessage());
    }
}
