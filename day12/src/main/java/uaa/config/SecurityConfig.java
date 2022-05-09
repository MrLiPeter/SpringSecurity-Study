package uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.message.Message;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.thymeleaf.standard.expression.MessageExpression;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import uaa.filter.RestAuthenticationFilter;

import javax.sql.DataSource;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Import(SecurityProblemSupport.class)
@Order(99)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final SecurityProblemSupport securityProblemSupport;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                requestMatchers(req->req.mvcMatchers("/authorize/**","/api/**","/admin/**"))
                .exceptionHandling(exp->exp
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(req->req.
                        antMatchers("/authorize/**").permitAll().
                        antMatchers("/api/**").hasRole("USER")
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterAt(restAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

    }

    private RestAuthenticationFilter restAuthenticationFilter() throws Exception {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(jsonAuthticationSuccessHandler());
        filter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler());
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }

    /**
     * 退出成功后处理
     * @return
     */
    private LogoutSuccessHandler jsonLogoutSuccessHandler() {
        return (req, res, auth) -> {
            val objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding("utf-8");
            res.sendRedirect("/login");
            log.debug("退出成功");
        };
    }

    /***
     * 登录失败后处理
     * @return
     */
    private AuthenticationFailureHandler jsonAuthenticationFailureHandler() {
        return (req, res, exp) -> {
            val objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding("utf-8");
            val errData = Map.of(
                    "title", "认证失败",
                    "details", exp.getMessage()
            );
            res.getWriter().println(objectMapper.writeValueAsString(errData));
        };
    }

    /***
     * 登录成功后处理
     * @return
     */
    private AuthenticationSuccessHandler jsonAuthticationSuccessHandler() {
        return (req, res, auth) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(auth));
            log.debug("认证成功");
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username,password,enabled from lxh_users where username=?")//如果想通过name来登录,可以写成select name as username 。。。。
                .usersByUsernameQuery("select username,password,enabled from lxh_authorities where username=?")
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder(); //BCrypt加密器
//        return new MessageDigestPasswordEncoder("MD5"); //MD5加密
        //多种编码方式共存
        val idForDefault = "bcrypt";
        val encoders = Map.of(idForDefault,
                new BCryptPasswordEncoder(),"SHA-1",
                new MessageDigestPasswordEncoder("SHA-1"));
        return new DelegatingPasswordEncoder(idForDefault,encoders);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/public/**","/error/**","/h2-console/**");
    }
}
