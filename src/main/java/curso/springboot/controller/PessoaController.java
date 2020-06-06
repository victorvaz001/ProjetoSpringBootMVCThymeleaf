package curso.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;

@Controller
public class PessoaController {
	
	@Autowired //injeção de dependencias
	private PessoaRepository  pessoaRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa") //redirecionamento de URL
	public String inicio() {
		return "cadastro/cadastropessoa";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco
		andView.addObject("pessoas", pessoasIt);
		
		
		return andView;
	}
	//ModelAndView -> ligar o modelo de dados cadatrados no banco com a tela(view)
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco
		
		//pessoas -> objeto vindo da view
		andView.addObject("pessoas", pessoasIt);
		
		return andView;
	}

}
