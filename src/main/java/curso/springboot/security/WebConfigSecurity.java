package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.springboot.model.Usuario;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	
	@Override //configura as solicitações de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable() // Desativa as configurações padrão de memória.(spring)
		.authorizeRequests() // Permitir restringir acesso
		.antMatchers(HttpMethod.GET, "/").permitAll() //qualquer usuário terá acesso a pagina inicial
		.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuário
		.loginPage("/login")
		.defaultSuccessUrl("/cadastropessoa")
		.failureUrl("/login?error=true")
		.and().logout().logoutSuccessUrl("/login")// Mapeia URL de Logout e invalida usuário atenticação
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override //Cria autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		/*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("victor")
		.password("$2a$10$73fPlhqqkPMf8m62P/OVx.e4G9JewGXi5AlOYnQKDkjkzE7hkA8QK")
		.roles("ADMIN");*/
	}
	
	@Override //Ignora URL especificas
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}
	
	public String getLoginUsuario() {
		Usuario user = (Usuario) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		return user.getUsername().toString();
	}

}
