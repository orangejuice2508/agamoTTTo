package de.gruppe2.agamoTTTo.configuration;

import de.gruppe2.agamoTTTo.security.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * This class is used to define beans which are used for security measures.
 */
@Configuration
public class SecurityConfiguration {

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
     * Set up the Role Hierarchy as obtained from the static method.
     *
     * @return roleHierarchy the created hierarchy of the roles in the system
     */
    @Bean
    public RoleHierarchyImpl roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(Role.getRoleHierarchyStringRepresentation());
        return roleHierarchy;
    }

    /**
     *  Create web expression handler to pass created hierarchy to it.
     *
     * @return SecurityExpressionHandler evaluates expressions based on the roleHierarchy
     */
    @Bean
    public SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return defaultWebSecurityExpressionHandler;
    }
}
