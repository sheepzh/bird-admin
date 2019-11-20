package com.bird.config;

import com.bird.mvc.AuthorityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * mvc配置，跨域请求等
 *
 * @author zhyyy
 **/
@Configuration
public class WebMvcConfigurerBean implements WebMvcConfigurer {

    private final AuthorityInterceptor authorityInterceptor;

    @Autowired
    public WebMvcConfigurerBean(AuthorityInterceptor authorityInterceptor) {
        this.authorityInterceptor = authorityInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 所有请求允许跨域
        // pathPattern使用**而不是/**，解决以下bug
        // 当前端服务、后端服务和客户端运行在同一台机器上时，会出现跨域问题
        registry.addMapping("**")
                .allowedMethods("*")
                .allowedOrigins("*")
                .maxAge(3600)
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        registry.addInterceptor(authorityInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
