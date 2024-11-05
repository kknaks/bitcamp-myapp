package bitcamp.myapp;

import bitcamp.myapp.annotation.LoginUserArgumentResolver;
import bitcamp.myapp.interceptor.AdminInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
@PropertySource("file:${user.home}/config/ncp.properties")
@EnableTransactionManagement
public class ServerApp implements WebMvcConfigurer {

  @Autowired
  ApplicationContext appCtx;

  public ServerApp() {
    System.getProperties().setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
  }

  public static void main(String[] args) {
    SpringApplication.run(ServerApp.class, args);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new AdminInterceptor())
        .addPathPatterns("/user*");
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new LoginUserArgumentResolver());
  }
}
