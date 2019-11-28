package keti.sgs.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("!insecure")
@Configuration
@EnableWebSecurity
public class WebSercurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private DataSource dataSource;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf().disable();
    httpSecurity.authorizeRequests().antMatchers("/*").hasAuthority("ADMIN");
    httpSecurity.headers().frameOptions().disable();
    httpSecurity.formLogin().loginPage("/login").permitAll().and().logout().logoutUrl("/logout")
        .deleteCookies("JSESSIONID").logoutSuccessUrl("/login");
  }

  @Override
  public void configure(WebSecurity webSecurity) throws Exception {
    webSecurity.ignoring().antMatchers("/api/**", "swagger-ui.html", "/swagger-resources",
        "/configuration/security", "/swagger/**", "/console/**", "/h2-console/**");

    // for static resource
    webSecurity.ignoring().antMatchers("/css/**", "/js/**", "/js/**/**", "image/**", "/fonts/**",
        "lib/**");
  }
}
