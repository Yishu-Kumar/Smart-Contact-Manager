package org.springboot.smartcontactmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {

    //********************//
    //Class that contains "Configuration of "Security" like which "Authorization" we want to provide to which "User" (or not)
         //and "User URL".
    //This class provide the authentication to user by fetching his data from database, instead of creating the password by "Security" itself.


    @Bean
    public UserDetailsService getUserDetailsService() {

        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return daoAuthenticationProvider;
    }

    //Configure methods.


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(this.authenticationProvider()); //Used for "Database" authentication and we can use other
        //authentications also according to our needs like "inMemoryAuthentication()" etc.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER").antMatchers("/**").permitAll()
                .and().formLogin()
                .loginPage("/logIn")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/user/index")
//                .failureUrl("/login-failure")
                .and().csrf().disable();

        //"**" -> all "URL's", "/admin/**" -> all "URL's" after "admin" & "/user/**" -> all "URL's" after "user".
        //"antMatchers()" -> to match the "URL's".
        //".hasRole()" -> for providing authorizations according to user "Role" & "permitAll()" -> providing all authorizations to other one's.
        //"loginPage()" -> dynamic ("logIn") page, instead of default page.
        //"loginProcessingUrl()" -> URL to process (or take action on) the "Login" page.
        //"defaultSuccessUrl()" -> URL to send to page after successful login.
        //"failureUrl()" -> URL to send to page after failure in login.
        //"csrf().disable()" -> to disable the "csrf.
    }
}
