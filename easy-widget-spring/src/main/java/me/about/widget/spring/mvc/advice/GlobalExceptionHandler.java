package me.about.widget.spring.mvc.advice;

import me.about.widget.spring.mvc.exception.BizException;
import me.about.widget.spring.mvc.result.Result;
import me.about.widget.spring.mvc.result.ValidFieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 统一异常处理
 *
 * @Valid 与 @Validated 的应用场景
 * https://langinteger.github.io/2019/09/13/java-bean-validation/
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 18:02
 * @description:
 */


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * form表单形式的参数验证
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {BindException.class})
    public Result validParameterWrap(BindException ex) {
        logger.error(ex.getMessage(),ex);
        List<FieldError> errors= ex.getBindingResult().getFieldErrors();
        return buildValidFieldError(errors);
    }

    /**
     * json body形式的参数验证
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Result validParameterWrap2(MethodArgumentNotValidException ex) {
        logger.error(ex.getMessage(),ex);
        List<FieldError> errors= ex.getBindingResult().getFieldErrors();
        return buildValidFieldError(errors);
    }

    /**
     * controller 转换单一属性检验失败 比如：int,string,list
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public Result validParameterWrap3(ConstraintViolationException ex) {
        logger.error(ex.getMessage(),ex);
        List<ValidFieldError> results = new ArrayList<>();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for ( ConstraintViolation constraintViolation : constraintViolations ) {
            ValidFieldError validFieldError = new ValidFieldError();
            validFieldError.setField(constraintViolation.getPropertyPath().toString());
            validFieldError.setMessage(constraintViolation.getMessage());
            results.add(validFieldError);
        }
        return Result.failed(400,"参数异常：参数检验失败",results);
    }

    private Result buildValidFieldError(List<FieldError> errors) {
        List<ValidFieldError> results = new ArrayList<>();
        for(FieldError fieldError : errors) {
            ValidFieldError validFieldError = new ValidFieldError();
            validFieldError.setField(fieldError.getField());
            validFieldError.setMessage(fieldError.getDefaultMessage());
            results.add(validFieldError);
        }
        return Result.failed(400,"参数异常：参数检验失败",results);
    }

    @ExceptionHandler(BizException.class)
    public Result businessException(BizException ex) {
        return Result.failed(ex.getCode(),"【业务异常】:" + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result exception(Exception ex) {
        logger.error(ex.getMessage(),ex);
        return Result.failed(500,"【内部异常】:" + ex.getMessage());
    }
}
