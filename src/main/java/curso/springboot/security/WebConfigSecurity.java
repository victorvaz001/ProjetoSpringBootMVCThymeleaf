package curso.springboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Override //configura as solicitações de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable() // Desativa as configurações padrão de memória.(spring)
		.authorizeRequests() // Permitir restringir acesso
		.antMatchers(HttpMethod.GET, "/").permitAll() //qualquer usuário terá acesso a pagina inicial
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuário
		.and().logout()// Mapeia URL de Logout e invalida usuário atenticação
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override //Cria autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("victor")
		.password("$2a$10$73fPlhqqkPMf8m62P/OVx.e4G9JewGXi5AlOYnQKDkjkzE7hkA8QK")
		.roles("ADMIN");
	}
	
	@Override //Ignora URL especificas
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}

}
