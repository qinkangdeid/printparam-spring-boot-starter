package com.github.printparam.starter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qinkangdeid
 */
@ConfigurationProperties(
        prefix = "print-param"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintParamProperties {
    /**
     * 是否在日志中打印输入信息，默认true
     */
    private boolean enableInputParam = true;
    /**
     * 是否在日志中打印header信息 默认false
     */
    private boolean enableHeaderParam;
    /**
     * 是否在日志中打印输出信息，默认false
     */
    private boolean enableOutputResult;
    /**
     * 拦截哪些url进行日志打印 默认/*表示全部拦截，正则表达式
     */
    private String filterIncludePattern = "/*";
    /**
     * 正则表达式描述不拦截哪些url打印日志，默认如左
     */
    private String filterExcludePattern = "(/webjars/.*|/css/.*|/images/.*|/fonts/.*|/js/.*)";


}
