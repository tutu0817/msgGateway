/** 
 * @Title:
 * @Desription:
 * @Company:CSN
 * @ClassName:BusinessException.java
 * @Author:miss
 * @CreateDate:2021年3月19日   
 * @UpdateUser:miss  
 * @Version:0.1 
 *    
 */ 

package com.exam.gate.common.exception;

/** 
 * @ClassName: BusinessException 
 * @Description: 已知的业务异常，不需要打印堆栈，只需显示message，如验证码错误，用户名密码不正确之类，还有各种输入格式校验，这些异常堆栈打印出来没有任何意义
 *               需要用来排查问题或故障的异常（系统出错信息）才需要打印堆栈
 * @author: miss 
 * @date: 2021年3月19日
 * 
 */
public class BusinessException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1343413232L;
    
    //有参的构造方法
    public BusinessException(String message){
        super(message);
        
    }   
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
    
}
