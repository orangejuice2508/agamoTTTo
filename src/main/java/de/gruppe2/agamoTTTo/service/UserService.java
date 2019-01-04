package de.gruppe2.agamoTTTo.service;

import de.gruppe2.agamoTTTo.domain.entity.PasswordResetToken;
import de.gruppe2.agamoTTTo.domain.entity.Role;
import de.gruppe2.agamoTTTo.domain.entity.User;
import de.gruppe2.agamoTTTo.repository.PasswordResetTokenRepository;
import de.gruppe2.agamoTTTo.repository.UserRepository;
import de.gruppe2.agamoTTTo.security.CustomSecurityUser;
import de.gruppe2.agamoTTTo.security.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service which is used for dealing with the users of our application.
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private PasswordResetTokenRepository passwordResetTokenRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private EmailService emailService;

    private MessageSource messageSource;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService,
                       MessageSource messageSource,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.messageSource = messageSource;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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
        // Generate a random password and hash it. Set default role.
        String randomPassword = generateRandomPassword();
        user.setEncryptedPassword(passwordEncoder.encode(randomPassword));
        user.setRole(new Role(3L, de.gruppe2.agamoTTTo.security.Role.MITARBEITER));

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
     * This method tries to find a user from the database based on the entered email.
     * If the user was found, an authority is assigned to him, based on the role which is stored in the database.
     *
     * @param email the email as entered in the login form
     * @return user the newly created CustomSecurityUser object with "email" as username and "role" as granted authorities
     * @throws UsernameNotFoundException if no user was found with the entered email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.info("No such user found");
            throw new UsernameNotFoundException(email);
        }

        return new CustomSecurityUser(user);
    }

    /**
     * This method uses the userRepository to find users according to the searchTerm.
     *
     * @param searchTerm the entered search term by the user
     * @return a set of users, if users were found; otherwise: an empty set
     */
    @PreAuthorize(Permission.VORGESETZTER)
    public Set<User> findUsersBySearchTerm(String searchTerm) {
        return userRepository.searchForUserByFirstNameOrLastNameOrEmail(searchTerm);
    }

    /**
     * This method uses the userRepository to find a certain user from the database.
     * If no user was found, an empty optional object is returned.
     *
     * @param id the id of the user, which should be found
     * @return the optional user
     */
    @PreAuthorize(Permission.ADMINISTRATOR)
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * This method uses the userRepository to try to update to the database.
     *
     * @param updatedUser the updated user as obtained from the controller
     */
    @PreAuthorize(Permission.ADMINISTRATOR)
    public void updateUser(User updatedUser) {
        // Use the getOne method, so that no more DB fetch has to be executed
        User userToUpdate = userRepository.getOne(updatedUser.getId());

        userToUpdate.setFirstName(updatedUser.getFirstName());
        userToUpdate.setLastName(updatedUser.getLastName());
        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setEnabled(updatedUser.getEnabled());
        userToUpdate.setRole(updatedUser.getRole());
        userRepository.save(userToUpdate);
    }


    /**
     * This method checks whether an entered password matches the password currently stored in the database
     *
     * @param user        the user whose password should be checked
     * @param oldPassword the entered old password in plaintext
     * @return true if the oldPassword matches the the password in the db, false if they do not match
     */
    @PreAuthorize(Permission.IS_AUTHENTICATED)
    public boolean isOldPasswordCorrect(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getEncryptedPassword());
    }

    /**
     * This method uses the userRepository to try to change the password of a user in the database.
     *
     * @param user        the user whose password should be changed
     * @param newPassword the new password in plaintext
     */
    @PreAuthorize(Permission.IS_AUTHENTICATED)
    public void changePassword(User user, String newPassword) {
        user = userRepository.getOne(user.getId());
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setEncryptedPassword(encryptedPassword);

        userRepository.save(user);
    }

    /**
     * This method creates a PasswordResetToken for the user based on a random String.
     * After that an email is sent to the users' email address with the link for updating his password
     *
     * @param user the user as obtained from the controller
     */
    public void sendPasswordResetEmail(User user) {
        // Create a random String
        String token = UUID.randomUUID().toString();

        // Create new PasswordResetToken for user
        createPasswordResetTokenForUser(token, user);

        /* If the passwordToken was added successfully the email can be sent.
           Note: We obtain the subject and text of the email from "messages.properties" by using the messageSource.
           Since the text of the email contains parameters (name, link),
           we need to pass them to the messageSource.
         */
        String subject = messageSource.getMessage("forgot_password.email.subject", null, Locale.getDefault());
        Object[] parameters = {user.getLastName(), "https://132.231.36.203/checkToken?" + "userId=" + user.getId() + "&token=" + token};
        String text = messageSource.getMessage("forgot_password.email.text", parameters, Locale.getDefault());
        emailService.sendHTMLEmail(user.getEmail(), subject, text);
    }

    /**
     * This method creates a PasswordResetToken for a user and tries to save it to the database.
     *
     * @param token the randomly created token
     * @param user  the user which should the token should be associated to
     */
    private void createPasswordResetTokenForUser(String token, User user) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
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
