package curso.springboot.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.springboot.model.Usuario;
import curso.springboot.repository.UsuarioRepository;

@Service
@Transactional//carrega os acessos de uma transação
public class ImplementacaoUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = userRepository.findUserByLogin(username);
		
		
		
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getPassword(), 
				usuario.isEnabled(), true, 
				true, true, 
				usuario.getAuthorities());
	}

}
