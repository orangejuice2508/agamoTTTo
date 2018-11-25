package de.gruppe2.agamoTTTo.configuration;

import de.gruppe2.agamoTTTo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configure and enable Spring Security for our application.
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Define the bean for hashing passwords with the BCrypt algorithm.
     *
     * @return bcryptPasswordEncoder Object used for hashing and comparing passwords.
     * */

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Set the service which is responsible for finding the user in the database
     * and set the necessary passwortEncoder for comparing the entered with the
     * saved password.
     *
     * @param auth SecurityBuilder for JDB based authentication used to create an AuthenticationManager.
     * @throws Exception If an error occurs, when adding the UserDetailsService.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

    }

    /**
     * Finally configure the Security Configuration for this application.
     *
     * @param http Used to configure specific HTTP requests.
     * @throws Exception If any request can't be configured properly.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        // The login page does not require an user to be logged in
        http.authorizeRequests().antMatchers("/").permitAll();

        // /userInfo page requires login as ROLE_USER or ROLE_ADMIN.
        // If no login, it will redirect to /login page.
        http.authorizeRequests().antMatchers("/userInfo").access("hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_VORGESETZTER', 'ROLE_MITARBEITER')");

        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin").access("hasRole('ROLE_ADMINISTRATOR')");

        // When the user is logged in as ROLE_X,
        // but wants to access a page that requires ROLE_Y,
        // AccessDeniedException will be thrown.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/accessDenied");

        // Configuration for login and logout
        http.authorizeRequests().and().formLogin()//
                .loginPage("/") // Page with the login form
                .loginProcessingUrl("/j_spring_security_check") // Submit URL of login form (provided by Spring Security).
                .defaultSuccessUrl("/home") // Redirect to this page, when login was successful.
                .failureUrl("/?loginError=true") // Redirect to this page, when login failed.
                .usernameParameter("email") // Define the entered e-mail as the username parameter
                .passwordParameter("password") // Define the entered password as the password parameter
                .and().logout() // Configuration for logout
                .logoutUrl("/logout") // Page for logout (provided by Spring Security).
                .logoutSuccessUrl("/?logout=successful"); // Redirect to this page, when logout was successful.
    }
}
