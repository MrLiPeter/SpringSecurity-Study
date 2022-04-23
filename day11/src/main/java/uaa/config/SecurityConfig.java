package uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.message.Message;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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

import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final SecurityProblemSupport securityProblemSupport;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                exceptionHandling(exp->exp
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .authorizeRequests(req->req.
                        antMatchers("/authorize/**").permitAll().
                        antMatchers("/api/**").hasRole("USER")
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterAt(restAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);
//                .formLogin(form -> form.loginPage("/login")
//                        .successHandler(jsonAuthticationSuccessHandler())
//                        .failureHandler(jsonAuthenticationFailureHandler())
//                        .permitAll())
//                .httpBasic(withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)
//                .rememberMe(rememberme->rememberme.tokenValiditySeconds(30*24*3600).rememberMeCookieName("somekeyToRemember"))//rememberMe设置
//                .logout(logout -> logout.logoutUrl("/perform_logout").logoutSuccessHandler(jsonLogoutSuccessHandler()));
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
        auth.inMemoryAuthentication()
                .withUser("user")
//                .password(passwordEncoder().encode("12345678"))
                .password("{bcrypt}$2a$10$n8/JPcFt8jZM6479XvFnQe07ppeDRok4D.kXe3JQX3Y3DsY04JyN2")
                .roles("USER","ADMIN")
                .and().withUser("lisi")
//                .password(new MessageDigestPasswordEncoder("SHA-1").encode("12345678"))
                .password("{SHA-1}{NJrgZSWGJjI8XU6B/uAf3zGNsILyQzMkFvMOPtuVAdM=}984325e3920910d7a0a2baabed434b224284e26f")
                .roles("USER");
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
        web.ignoring().mvcMatchers("/public/**","/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
