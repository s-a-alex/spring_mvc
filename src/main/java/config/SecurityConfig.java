package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"web"})
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // В методе configureGlobal оnределяется механизм аутентификации. В данной уnрощенной конфигурации жестко закодирован
    // единственный nользователь с nрисвоенной ролью USER. Но в nроизводственной среде nользователь должен nроходить аутентификацию
    // через базу данных, nротокол LDAP или механизм SSO.
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        try {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            auth.inMemoryAuthentication().passwordEncoder(passwordEncoder).withUser("user").password(passwordEncoder.encode("user")).roles("USER");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // В цепочке вызовов antMatchers("/*").permitAll() указывается на то, что всем пользователям разрешается войти в данное веб-приложение.
    // В вызове метода .formLogin() определяется поддержка регистрации через заполняемую форму. А во всех последующих вызовах вплоть до метода .and()
    // конфигурируется форма регистрации. Чрез вызов метода .logout() предоставляется ссылка на выход из данного веб-приложения.
    // В приведенной выше конфигурации имя пользователя, пароль и URLдля регистрации задаются явным образом. Но если в представлении употребляются
    // стандартные имена, то из конфигурации необходимо исключить следующую часть:
    //.usernameParameter("username")
    //.passwordParameter("password")
    //.loginProcessingUrl ("/login")
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/", "/home").permitAll()
                .and()
//                .authorizeRequests().antMatchers("/singers/**").permitAll()
                .authorizeRequests().antMatchers("/singers/**").authenticated()
                .and()
                .formLogin().usernameParameter("username").passwordParameter("password").loginProcessingUrl("/login").loginPage("/login").failureUrl("/login?error").defaultSuccessUrl("/singers").permitAll()
                .and()
                .logout().logoutUrl("/logout").logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).logoutSuccessUrl("/login?logout")
                .and()
                .csrf().csrfTokenRepository(repo());
    }

    @Bean
    public CsrfTokenRepository repo() {
        HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
        repo.setParameterName("_csrf");
        repo.setHeaderName("X-CSRF-TOKEN");
        return repo;
    }
}