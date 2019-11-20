package com.bird;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Springboot 启动类。
 * 排除mybatis自带分页插件
 * <p>
 * EnableAsync 开启异步任务
 * EnableScheduling 开启定时任务
 * EnableTransactionManagement SpringBoot托管事务管理
 * EnableAspectJAutoProxy  AOP默认开启
 *
 * @author zhyyy
 */
@SpringBootApplication(exclude = PageHelperAutoConfiguration.class)
@MapperScan(basePackages = {"com.bird.dao.extend.*", "com.bird.dao"})
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class BirdApplication extends SpringBootServletInitializer {

    /**
     * 配置FastJson进行消息拼装
     *
     * @return HttpMessageConverters
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        // 1.定义一个converters转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        // 2.添加fastjson配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(UTF_8);
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        SerializeConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        // 3.在converter中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        // 4.返回HttpMessageConverters对象
        return new HttpMessageConverters(fastConverter);
    }

    @Bean
    public PageInterceptor pageHelper() {
        //分页插件
        PageInterceptor pageHelper = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);
        //添加插件
        new SqlSessionFactoryBean().setPlugins(pageHelper);
        return pageHelper;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BirdApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BirdApplication.class, args);
    }
}
