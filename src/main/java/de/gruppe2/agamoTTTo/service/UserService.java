package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.entity.User;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import de.gruppe2.agamoTTTo.security.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Service which is used for dealing with the users of our application.
 */

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private EmailService emailService;

    private MessageSource messageSource;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    /**
     * This method tries to find a user from the database based on the entered email.
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

    /**
     * This method uses the userRepository to try to add a user to the database.
     * Before saving it, a random password is generated
     * In order to inform the new user about his credentials an email is sent to his email address.
     *
     * @param user the user as obtained from the controller
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public void addUser(User user){
        // Generate a random password and hash it.
        String randomPassword = generateRandomPassword();
        user.setEncryptedPassword(passwordEncoder.encode(randomPassword));

        /* Try to save the user to the database before sending the mail.
           Reason: If email address is already registered an exception is
           thrown by the repository message. Then the mail doesn't have to
           be and won't be sent.
        */
        userRepository.save(user);

        /* If the user was added successfully the email can be sent.
           Note: We obtain the subject and text of the email from "messages.properties" by using the messageSource.
           Since the text of the email contains parameters (name, email, password),
           we need to pass them to the messageSource.
         */
        String subject = messageSource.getMessage("employees.add.email.subject", null, Locale.getDefault());
        Object[] parameters = {user.getLastName(), user.getEmail(), randomPassword};
        String text = messageSource.getMessage("employees.add.email.text", parameters, Locale.getDefault());
        emailService.sendHTMLEmail(user.getEmail(), subject, text);
    }

    /**
     * Used for generating a random password for a newly registered user.
     *
     * @return the random password
     */
    private String generateRandomPassword(){
        int length = 10;
        SecureRandom secureRandom = new SecureRandom();
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ$%&/()?!+-,.*abcdefghijklmnopqrstuvwxyz";

        StringBuilder randomPassword = new StringBuilder(length);

        // Add random characters until the desired length off the password is reached.
        for (int i =0; i < length; i++){
            randomPassword.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }

        return randomPassword.toString();
    }
}
