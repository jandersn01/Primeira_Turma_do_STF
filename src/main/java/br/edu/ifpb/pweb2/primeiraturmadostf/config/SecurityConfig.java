package br.edu.ifpb.pweb2.primeiraturmadostf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos - permitir todos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // Paginas publicas (login, logout, erros)
                .requestMatchers("/login", "/logout", "/error", "/error/**").permitAll()

                // Rotas do Aluno (Admin tambem tem acesso)
                .requestMatchers("/aluno/**").hasAnyRole("ALUNO", "ADMIN")

                // Rotas do Professor (Professor, Coordenador e Admin podem acessar)
                .requestMatchers("/professor/**").hasAnyRole("PROFESSOR", "COORDENADOR", "ADMIN")

                // Rotas do Coordenador (Admin tambem tem acesso)
                .requestMatchers("/coordenador/**").hasAnyRole("COORDENADOR", "ADMIN")

                // Rotas de Administração
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Qualquer outra requisição precisa estar autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .usernameParameter("matricula")
                .passwordParameter("senha")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }
}
