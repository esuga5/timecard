package com.vvenn.timecard.config;

import com.vvenn.timecard.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 認証管理設定
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    // "AuthenticationManagerBuilder auth"に記載したid,pwで認証を可能にする設定
    protected void configureAuthenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userService).passwordEncoder(this.passwordEncoder());
    }

    /**
     * パスワードを暗号化します。
     *
     * @return 暗号化されたパスワード
     */
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 静的なコンテンツへのアクセスを認証管理の対象から除外します。
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("\favicon.ico", "/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登録・ログイン・ログアウトの各URL以外はログインが必要
        http.authorizeRequests().antMatchers("/login").permitAll().antMatchers("/logout").permitAll().anyRequest()
                .authenticated();
        // ログインURLと成功/失敗時それぞれのリダイレクト先
        http.formLogin().loginProcessingUrl("/login").loginPage("/login").failureUrl("/login?error")
                .defaultSuccessUrl("/")
                // ログインに使うフォーム項目のname属性
                .usernameParameter("username").passwordParameter("password");
        // セッション関連設定
        http.logout().logoutUrl("/logout").deleteCookies("JSESSIONID").invalidateHttpSession(true);
    }
}
