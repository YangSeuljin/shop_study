package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login") //로그인 페이지 URL을 설정
                .defaultSuccessUrl("/") // 로그인 성공시 이동할 URL
                .usernameParameter("email") // 로그인 시 사용할 파라미터 이름으로 email을 지정
                .failureUrl("/members/login/error") // 로그인 실패 시 이동할 URL을 설정
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //로그아웃 URL을 설정
                .logoutSuccessUrl("/");

        //시큐리티 처리에 HttpServiceRequest를 이용한다는 것을 의미
        //permitAll()을 통해 모든 사용자가 인증없이 해당 경로에 접근할 수 있도록 설정.
        http.authorizeRequests().mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                // /admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근이 가능하도록 설정.
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                // 설정해준 경로를 제외한 나머지 경로들은 모두 인증을 요구하도록 설정.
                .anyRequest().authenticated();

        //인증되지 않은 사용자가 리소스에 접근하였을 때 수행되는 핸들러를 등록함.
        http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

    }

    //static 디렉터리의 하위 파일은 인증을 무시하도록 설정.
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

}
