package curso.springboot.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.springboot.model.Pessoa;

@Repository //ativando os recursos
@Transactional //para o spring controlar a parte de persistencia
public interface PessoaRepository extends CrudRepository<Pessoa, Long>{
	
	//JPQL, spring data
	@Query("select p from Pessoa p where p.nome like %?1%") //consulta no banco
	List<Pessoa> findPessoaByName(String nome);
	
	@Query("select p from Pessoa p where p.sexopessoa like ?1") //consulta no banco
	List<Pessoa> findPessoaBySexo(String sexo);
	
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexopessoa = ?2") //consulta no banco
	List<Pessoa> findPessoaByNSexo(String nome, String sexopessoa);
	

}
