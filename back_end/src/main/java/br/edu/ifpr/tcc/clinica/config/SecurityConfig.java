package br.edu.ifpr.tcc.clinica.config;

import br.edu.ifpr.tcc.clinica.domain.UserAccount;
import br.edu.ifpr.tcc.clinica.repository.UserAccountRepository;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserAccountRepository users) {
        return username -> users.findByUsername(username)
                .map(account -> User.withUsername(account.getUsername()).password(account.getPasswordHash())
                        .roles("CLINIC").build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .build();
    }

    @Bean
    CommandLineRunner createInitialUser(UserAccountRepository users, PasswordEncoder encoder,
            @Value("${APP_ADMIN_USERNAME:admin}") String username,
            @Value("${APP_ADMIN_PASSWORD:}") String configuredPassword) {
        return args -> {
            if (users.count() != 0) return;
            String password = configuredPassword;
            if (password == null || password.isBlank()) {
                byte[] bytes = new byte[24];
                new SecureRandom().nextBytes(bytes);
                password = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                log.warn("Conta inicial criada: usuário '{}'; senha temporária gerada: {}. Guarde-a e configure APP_ADMIN_PASSWORD.",
                        username, password);
            } else {
                log.warn("Conta inicial '{}' criada a partir de APP_ADMIN_PASSWORD. Altere a senha de demonstração antes de qualquer uso real.",
                        username);
            }
            users.save(new UserAccount(username, encoder.encode(password)));
        };
    }
}
