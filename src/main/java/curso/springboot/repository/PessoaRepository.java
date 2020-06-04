package curso.springboot.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.springboot.model.Pessoa;

@Repository //ativando os recursos
@Transactional //para o spring controlar a parte de persistencia
public interface PessoaRepository extends CrudRepository<Pessoa, Long>{
	
	

}
