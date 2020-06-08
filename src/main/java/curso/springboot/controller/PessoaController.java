package curso.springboot.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;

@Controller
public class PessoaController {
	
	@Autowired //injeção de dependencias
	private PessoaRepository  pessoaRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa") //redirecionamento de URL
	public ModelAndView inicio() {
		//passando objeto vazio
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", new Pessoa()); //passando objeto pra tela, para ficar em edição
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco
		modelAndView.addObject("pessoas", pessoasIt);
		return modelAndView;
	}
	
	//**/salvarpessoa" -> ignora qualquer coisa antes que ele intercepte o salvar pessoa de qualquer forma /savalarpessoa
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa()); //passando objeto pra tela, para ficar em edição(vazio)
		
		
		return andView;
	}
	//ModelAndView -> ligar o modelo de dados cadatrados no banco com a tela(view)
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco
		//pessoas -> objeto vindo da view
		andView.addObject("pessoas", pessoasIt);
		andView.addObject("pessoaobj", new Pessoa()); //passando objeto pra tela, para ficar em edição(vazio)
		
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);//carregando pessoa
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", pessoa.get()); //passando objeto pra tela, para ficar em edição
		
		return modelAndView;
		
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornar pra mesma tela
		modelAndView.addObject("pessoas", pessoaRepository.findAll()); //carrega a lista de pessoas, menos oque foi removido
		modelAndView.addObject("pessoaobj", new Pessoa()); //retorna o objeto vazio
		return modelAndView;
		
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");//retornar pra mesma tela
		modelAndView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa)); //consulta
		modelAndView.addObject("pessoaobj", new Pessoa()); //retorna objeto vazio
		
		return modelAndView;
		
	}
	
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);//carregando pessoa
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", pessoa.get()); //passando objeto pra tela, para ficar em edição
		
		return modelAndView;
		
	}

}
