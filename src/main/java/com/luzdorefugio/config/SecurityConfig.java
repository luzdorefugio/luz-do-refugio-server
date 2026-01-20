package com.luzdorefugio.config;

import com.luzdorefugio.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desativar CSRF (não é necessário para APIs REST com JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configurar CORS (Aqui ligamos ao método lá de baixo)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Definir quem pode entrar onde
                .authorizeHttpRequests(auth -> auth
                        // Rotas Públicas (Loja, Login, Registo, Criar Orders)
                        .requestMatchers("/api/shop/**", "/api/auth/**", "/api/orders").permitAll()

                        // Rotas de Admin (Requer autenticação)
                        .requestMatchers("/api/admin/**").authenticated()

                        // Qualquer outra coisa precisa de login
                        .anyRequest().authenticated()
                )

                // 4. Sessão Stateless (Não guardamos sessão no servidor, usamos Token)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Configurar os Providers de Autenticação
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- AQUI ESTÁ A CONFIGURAÇÃO DE CORS CENTRALIZADA ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // --- MUDANÇA CRÍTICA AQUI ---
        // Apaguei o setAllowedOrigins e uso APENAS o setAllowedOriginPatterns
        // Isto permite usar o * para apanhar todos os links do Vercel
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:4200",               // O teu PC
                "http://192.168.1.238:4200",               // O teu PC
                "http://localhost",                    // O teu PC (alternativa)
                "https://luz-do-refugio.vercel.app",   // Produção
                "https://luz-do-refugio-*.vercel.app", // <--- AGORA O * JÁ FUNCIONA
                "https://*-luzdorefugios-projects.vercel.app" // <--- Dica extra: Apanha os links da tua equipa/projeto específico
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}