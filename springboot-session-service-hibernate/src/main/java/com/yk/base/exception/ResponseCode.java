package com.yk.base.exception;

public enum ResponseCode
{
    GLOBAL_EXCEPTION("GLOBAL_EXCEPTION", 10000),

    BIND_EXCEPTION("BindException: 参数绑定失败", 10001),

    DATA_ACCESS_EXCEPTION("DataAccessException: 数据访问异常", 10002),

    HTTP_MESSAGE_NOT_READABLE("HttpMessageNotReadableException: 请求数据参数异常", 10003),

    DATA_INTEGRITY_VIOLATION("DataIntegrityViolationException: 字段验证错误，请完善后重试", 10004),

    MISSING_SERVLET_REQUEST_PARAMETER("MissingServletRequestParameterException: 字段验证错误", 10005),

    METHOD_ARGUMENT_NOT_VALID("MethodArgumentNotValidException: 参数校验异常", 10006),

    HTTP_MEDIA("HttpMediaTypeNotSupportedException or HttpMediaTypeNotAcceptableException: 媒体类型异常", 10007),

    SERVLET_EXCEPTION("ServletException", 10008),

    VALIDATION_EXCEPTION("ValidationException: 参数校验异常", 10009),

    UNAUTHORIZED_EXCEPTION("AccessDeniedException: 权限错误", 10010),

    AUTHENTICATION_EXCEPTION("AuthenticationException: 认证失败", 10011),

    MAX_UPLOAD_SIZE_EXCEEDED("MaxUploadSizeExceededException", 10012),

    NO_HANDLER_FOUND("NoHandlerFoundException", 10013),

    HTTP_REQUEST_METHOD_NOT_SUPPORTED("HttpRequestMethodNotSupportedException", 10014),

    CONSTRAINT_VIOLATION("ConstraintViolationException", 10015),

    URL_OR_PAGE_NOT_FOUND("页面不存在", 10016),


    SIGN_IN_ERROR("密码校验失败", 20000),

    CODE_VERIFY_ERROR("验证码校验失败", 20001),

    ACCOUNT_UN_SIGN_IN_ERROR("用户未登录", 20002),

    ACCOUNT_LOCKED_ERROR("账号被锁定", 20003),

    ACCOUNT_DISABLED_ERROR("帐号被禁用", 20004),

    ACCOUNT_TOO_MANY_LOGIN_ERROR("登录失败次数过多", 20005),

    ACCOUNT_UNKNOWN_ERROR("未知帐号", 20006),

    ACCOUNT_AUTHENTICATION_ERROR("认证错误", 20007),

    ACCOUNT_LOGIN_ERROR("登录错误", 20008),

    ACCOUNT_TOKEN_VERIFY_ERROR("token verify error", 20009),

    ACCOUNT_TOKEN_NOT_EXIST_ERROR("token not exist", 20010),

    ACCOUNT_TOKEN_NOT_SUPPORT_ERROR("token not support", 20011),

    ACCOUNT_USER_NOT_EXIST_ERROR("user not exist", 20012),

    USER_TEST_ERROR("test throw custom exception", 20013),

    USER_ALREADY_USE_ERROR("Username is already in use", 20014),

    USER_NOT_EXIST_ERROR("The user doesn't exist", 20015),

    UNKNOWN_ERROR("未知内部错误", -1),

    SUCCESS("成功", 0);


    public String message;

    public int code;

    ResponseCode(String message, int code)
    {
        this.code = code;
        this.message = message;
    }
}
