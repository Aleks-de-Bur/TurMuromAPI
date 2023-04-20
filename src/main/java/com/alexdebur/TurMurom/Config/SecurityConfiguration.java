package com.alexdebur.TurMurom.Config;

import com.alexdebur.TurMurom.Services.CustomUserDetailsService;
import com.alexdebur.TurMurom.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security
        .authentication.dao.DaoAuthenticationProvider;
import org.springframework.security
        .config.annotation.authentication
        .builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security
        .config.annotation.web.builders.HttpSecurity;
import org.springframework.security
        .config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security
        .config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security
        .crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security
        .web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/", "/product/**", "/images/**", "/registration", "/user/**", "/static/**")
//                .permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/log_in")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll();
//    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                //Доступ только для не зарегистрированных пользователей
                .antMatchers("/sign_up", "/log_in").not().fullyAuthenticated()
                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/authorization/example", "/personal_cabinet/admin", "/authorization/admin").hasRole("ADMIN")
                .antMatchers("/map", "/personal_cabinet").hasRole("USER")
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