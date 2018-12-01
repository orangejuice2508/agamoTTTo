package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.entity.User;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Service which is used for dealing with the users of our application.
 */

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This function tries to find a user from the database based on the entered email.
     * If the user was found, an authority is assigned to him, based on the role which is stored in the database.
     *
     * @param email the email as entered in the login form
     * @return user the newly created Spring User-Object with email as username and role as granted authorities
     * @throws UsernameNotFoundException if no user was found with the entered email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null){
            log.info("No such user found");
            throw new UsernameNotFoundException(email);
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(authority);

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncryptedPassword(), grantedAuthorities);
    }


}
