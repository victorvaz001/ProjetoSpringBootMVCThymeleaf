package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
	@RequestMapping(method = RequestMethod.GET, value = "**/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5) Pageable pageable
			, ModelAndView model, @RequestParam("nomepesquisa") String nomepesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa,pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("nomepesquisa", nomepesquisa);
		model.setViewName("cadastro/cadastropessoa");
		
		return model;
		
		
	}
	
	//**/salvarpessoa" -> ignora qualquer coisa antes que ele intercepte o salvar pessoa de qualquer forma /savalarpessoa
	@RequestMapping(method = RequestMethod.POST, 
			value = "**/salvarpessoa", consumes = {"multipart/form-data"}) //para dizer que o formulario faz upload
	public ModelAndView salvar(@Valid Pessoa pessoa, 
				BindingResult bindingResult, final MultipartFile file) throws IOException {
		
		
		
		//carregando os telefones do objeto pessoa
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		//tratar os erros
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");//retorna na mesma tela
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			modelAndView.addObject("pessoaobj", pessoa);
			
			//mostar as validações
			List<String> msg = new ArrayList<String>();
			for(ObjectError objectError : bindingResult.getAllErrors()) { //varrendo a lista de erros
				msg.add(objectError.getDefaultMessage()); //vem das anotações, @NotNull, @NotEmpty do model Pessoa
			}
			
			modelAndView.addObject("msg", msg); //salvando pessoa
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView;
		}
		
		System.out.println(file.getContentType());
		System.out.println(file.getName());
		System.out.println(file.getOriginalFilename());
		
		if(file.getSize() > 0) {//Cadastro um curriculo
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		}else {
			if(pessoa.getId() != null && pessoa.getId() > 0) {//editanto
				
			Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
			
			pessoa.setCurriculo(pessoaTemp.getCurriculo());// manter o mesmo
			pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
			pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
			}
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa()); //passando objeto pra tela, para ficar em edição(vazio)
		
		
		return andView;
	}
	
	//ModelAndView -> ligar o modelo de dados cadatrados no banco com a tela(view)
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");	//pessoas -> objeto vindo da view
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa()); //passando objeto pra tela, para ficar em edição(vazio)
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);//carregando pessoa
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornar pra mesma tela
		modelAndView.addObject("pessoaobj", pessoa.get()); //passando objeto pra tela, para ficar em edição
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));//carregar os dados em tela
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
		
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa"); //retornar pra mesma tela
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));	//pessoas -> objeto vindo da view; //carrega a lista de pessoas, menos oque foi removido
		modelAndView.addObject("pessoaobj", new Pessoa()); //retorna o objeto vazio
		return modelAndView;
		
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
								  @RequestParam("pesquisasexo") String pesquisasexo,
								  @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas = null;
		
		if(pesquisasexo != null && !pesquisasexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexo(nomepesquisa, pesquisasexo, pageable);
		} else {
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");//retornar pra mesma tela
		modelAndView.addObject("pessoas", pessoas); //consulta
		modelAndView.addObject("pessoaobj", new Pessoa()); //retorna objeto vazio
		modelAndView.addObject("nomepesquisa",nomepesquisa);
		
		
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
	
	@GetMapping("**baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, 
			HttpServletResponse response) throws IOException {
	
		/*Consultar o obejto pessoa no banco de dados*/
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if (pessoa.getCurriculo() != null) {
	
			/*Setar tamanho da resposta*/
			response.setContentLength(pessoa.getCurriculo().length);
			
			/*Tipo do arquivo para download ou pode ser generica application/octet-stream*/
			response.setContentType(pessoa.getTipoFileCurriculo());
			
			/*Define o cabeçalho da resposta*/
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			
			/*Finaliza a resposta passando o arquivo*/
			response.getOutputStream().write(pessoa.getCurriculo());
			
		}
	}
	
	

}
