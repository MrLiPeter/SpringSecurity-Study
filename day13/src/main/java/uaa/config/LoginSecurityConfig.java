package uaa.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Slf4j
@Configuration
@Order(100)
public class LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(req -> req.anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login")
            .failureUrl("/login?error")
            .defaultSuccessUrl("/")
            .permitAll())
            .logout(logout -> logout.logoutUrl("/perform_logout")
                    .logoutSuccessUrl("/login"))
            .rememberMe(rememberme->rememberme.tokenValiditySeconds(30*24*3600).rememberMeCookieName("somekeyToRemember"));//rememberMe设置

    }

//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        //inMemoryAuthentication 从内存中获取
//        auth.inMemoryAuthentication().
//                passwordEncoder(new BCryptPasswordEncoder()).
//                withUser("user1").
//                password(new BCryptPasswordEncoder().encode("123")).roles("USER");
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**","/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
