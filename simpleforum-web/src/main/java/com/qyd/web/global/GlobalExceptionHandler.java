package com.qyd.web.global;

import com.qyd.api.model.exception.ForumAdviceException;
import com.qyd.api.model.vo.ResVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 注解实现全局异常处理
 *
 * @author 邱运铎
 * @date 2024-04-27 16:36
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ForumAdviceException.class)
    public ResVo<String> handleForumAdviceException(ForumAdviceException e) {
        return ResVo.fail(e.getStatus());
    }
}
