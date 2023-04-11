package com.alexdebur.TurMurom.Config;

import com.alexdebur.TurMurom.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security
        .authentication.dao.DaoAuthenticationProvider;
import org.springframework.security
        .config.annotation.authentication
        .builders.AuthenticationManagerBuilder;
import org.springframework.security
        .config.annotation.web.builders.HttpSecurity;
import org.springframework.security
        .config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security
        .config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security
        .crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security
        .web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                //Доступ только для не зарегистрированных пользователей
                .antMatchers("/sign_up", "/log_in").not().fullyAuthenticated()
                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/authorization/example").hasRole("ADMIN")
                .antMatchers("/map").hasRole("USER")
                //Доступ разрешен всем пользователей
                .antMatchers("/places/**", "/","/guides/**",
                        "/assets/**", "/photos/**", "routes/**").permitAll()
                //Все остальные страницы требуют аутентификации
                .anyRequest().authenticated()
                .and()
                //Настройка для входа в систему
                .formLogin()
                .loginPage("/log_in")
                //Перенаправление на главную страницу после успешного входа
                .defaultSuccessUrl("/places/1?sortField=title&sortDir=asc&scheme=list")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/");
    }
}