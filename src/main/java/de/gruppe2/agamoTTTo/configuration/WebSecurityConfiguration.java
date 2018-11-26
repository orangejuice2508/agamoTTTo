package de.gruppe2.agamoTTTo.configuration;

import de.gruppe2.agamoTTTo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterInvocation;

/**
 * Configure and enable Spring Security for our application.
 * And enable security based on method annotations.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;

    private BCryptPasswordEncoder passwordEncoder;

    private SecurityExpressionHandler<FilterInvocation> webExpressionHandler;

    @Autowired
    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder passwordEncoder, SecurityExpressionHandler<FilterInvocation> webExpressionHandler) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.webExpressionHandler = webExpressionHandler;
    }

    /**
     * Set the service which is responsible for finding the user in the database
     * and set the necessary passwortEncoder for comparing the entered with the
     * saved password.
     *
     * @param auth SecurityBuilder for JDB based authentication used to create an AuthenticationManager.
     * @throws Exception If an error occurs while adding the UserDetailsService.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
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

        // Add the ExpressionHandler
        http.authorizeRequests().expressionHandler(webExpressionHandler);

        // The login page does not require an user to be logged in
        http.authorizeRequests().antMatchers("/").permitAll();

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
