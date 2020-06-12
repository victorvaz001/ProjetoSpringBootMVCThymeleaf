package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	//invocando os repositorys
	
	@Autowired //injeção de dependencias
	private PessoaRepository  pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired //para colocar o recurso dentro da classe
	private ProfissaoRepository profissaoRepository;
	
	//metodo invado ao abrir a tela
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa") //redirecionamento de URL
	public ModelAndView inicio() {
		//passando objeto vazio
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", new Pessoa()); //passando objeto pra tela, para ficar em edição
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	//**/salvarpessoa" -> ignora qualquer coisa antes que ele intercepte o salvar pessoa de qualquer forma /savalarpessoa
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		//carregando os telefones do objeto pessoa
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");//retorna na mesma tela
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();//vindo do banco, consultar todos
			modelAndView.addObject("pessoas", pessoasIt);//continuar mostrando a lista de pessoas
			modelAndView.addObject("pessoaobj", pessoa); //vai dar o erro e vai continuar com o formulario preenchido, com os objetos
			
			//mostar as validações
			List<String> msg = new ArrayList<String>();
			for(ObjectError objectError : bindingResult.getAllErrors()) { //varrendo a lista de erros
				msg.add(objectError.getDefaultMessage()); //vem das anotações, @NotNull, @NotEmpty do model Pessoa
			}
			
			modelAndView.addObject("msg", msg); //salvando pessoa
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView;
		}
		
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
		modelAndView.addObject("profissoes", profissaoRepository.findAll());//carregar os dados em tela
		
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
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
								  @RequestParam("pesquisasexo") String pesquisasexo) {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesquisasexo != null && !pesquisasexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNSexo(nomepesquisa, pesquisasexo);
		} else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");//retornar pra mesma tela
		modelAndView.addObject("pessoas", pessoas); //consulta
		modelAndView.addObject("pessoaobj", new Pessoa()); //retorna objeto vazio
		
		return modelAndView;
		
	}
	
	@GetMapping("**/pesquisarpessoa")
	public void imprimePDF(@RequestParam("nomepesquisa") String nomepesquisa,
								  @RequestParam("pesquisasexo") String pesquisasexo,
								  HttpServletRequest request,
								  HttpServletResponse response) throws Exception {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		//caso tenha nome e sexo informado, irá buscar
		if(pesquisasexo != null && !pesquisasexo.isEmpty() 
			&& nomepesquisa != null && !nomepesquisa.isEmpty()) {/*Busca por nome e sexo*/
			
			pessoaRepository.findPessoaByNSexo(nomepesquisa, pesquisasexo);
			
		}else if(nomepesquisa != null && !nomepesquisa.isEmpty()) {/*Busca somente por nome*/
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		
		
		}else if(pesquisasexo != null && !pesquisasexo.isEmpty()) {/*Busca somente por sexo*/
			pessoas = pessoaRepository.findPessoaBySexo(pesquisasexo);
		
		}
		
		else {/*Busca todos*/
			
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		
		/*Chamar o serviço que faz a geração do relatório*/
		byte[] pdf = reportUtil.geraRelatorio(pessoas, "pessoa", request.getServletContext());
		
		/*Tamanho da resposta, para o navegador*/
		response.setContentLength(pdf.length);
		
		/*Definir na resposta o tipo do arquivo*/
		response.setContentType("application/octet-stream"); //para arquivos pdf, midia, etc...
		
		/*Definir o cabeçalho da resposta*/
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attchment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		/*Finaliza a resposta para o navegador*/
		response.getOutputStream().write(pdf); //escreve os bytes do pdf
	}
	
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);//carregando pessoa
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", pessoa.get()); //passando objeto pra tela, para ficar em edição
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa)); //carrega telefones da pessoa
		
		return modelAndView;
		
	}
	
	//**, ignora oque vem antes e intercpeta oque está depois
	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addfonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {

		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get(); //consulta a pessoa
		
		if(telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			if(telefone.getNumero().isEmpty()) {
				msg.add("Número deve ser informado");
			}
			
			if(telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informado");
			}
			
			modelAndView.addObject("msg", msg);
			return modelAndView;
			
		}
		
		
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); //retornar pra mesma tela
		telefone.setPessoa(pessoa);
		
		telefoneRepository.save(telefone);//salva, amarra no banco
		
		modelAndView.addObject("pessoaobj", pessoa);//objeto pai sendo mostrado
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));//carrega os telefones
		
		return modelAndView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removerTelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();//carrega o objeto telefone
		
		telefoneRepository.deleteById(idtelefone); //deleta o telefona
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", pessoa);//objeto pai sendo mostrado
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));//carrega os telefones, menos oque foi removido
		return modelAndView;
		
	}

}
