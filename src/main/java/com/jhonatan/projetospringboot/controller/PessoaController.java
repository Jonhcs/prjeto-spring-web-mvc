package com.jhonatan.projetospringboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.jhonatan.projetospringboot.model.Pessoa;
import com.jhonatan.projetospringboot.model.Telefone;
import com.jhonatan.projetospringboot.model.Usuario;
import com.jhonatan.projetospringboot.repository.PessoaRepository;
import com.jhonatan.projetospringboot.repository.TelefoneRepository;
import com.jhonatan.projetospringboot.repository.UsuarioRepository;

@Controller
@RequestMapping("/")
public class PessoaController {
	

	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private UsuarioRepository  usuarioRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@GetMapping("/cadastropessoa")
	public ModelAndView inicio(Authentication auth) {
		
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoa", new Pessoa());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;
	}
	@PostMapping(path = "/salvarpessoa", consumes = "multipart/form-data")
	public ModelAndView salvar(Authentication auth,@Valid Pessoa pessoa, BindingResult bindin, final MultipartFile file) throws IOException {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);

		
		pessoa.setTelefones(telefoneRepository.listaTelefonesPorPessoa(pessoa.getId()));
		
		if (bindin.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			
			modelAndView.addObject("pessoas",pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			modelAndView.addObject("pessoa", new Pessoa());
			
			List<String> msg = new ArrayList<String>();
			
			for (ObjectError ObjError : bindin.getAllErrors()) {
				msg.add(ObjError.getDefaultMessage());
			}
			modelAndView.addObject("msg", msg);
			return modelAndView;
			
		}
		
		if (file.getSize() > 0) {
			pessoa.setArquivo(file.getBytes());
			pessoa.setContentType(file.getContentType());
			pessoa.setFileName(file.getOriginalFilename());
			
		}else {
			if (pessoa.getId() != null && pessoa.getId() > 0) {
				Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
				pessoa.setArquivo(pessoaTemp.getArquivo());
				pessoa.setContentType(pessoaTemp.getContentType());
				pessoa.setFileName(pessoaTemp.getFileName());
			}
		}
		pessoaRepository.save(pessoa);
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoa", new Pessoa());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;
	}
	
	@GetMapping("/listapessoas")
	public ModelAndView pessoas(Authentication auth) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("usuarioLogado", usuariolog);
		andView.addObject("pessoas",pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoa", new Pessoa());
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(Authentication auth,@PathVariable("idpessoa") Long idpessoa) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);

		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoa", pessoa.get());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;
	}
	
	@GetMapping("/deletarpessoa/{idpessoa}")
	public ModelAndView delete(Authentication auth,@PathVariable("idpessoa") Long idpessoa) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);

		
		pessoaRepository.deleteById(idpessoa);
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoa", new Pessoa());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;	
		
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(Authentication auth,@RequestParam("nomepesquisar") String nomepesquisa, @RequestParam("pesquisarsexo") String pesquisarsexo) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);

		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		
		List<Pessoa> pessoas = new ArrayList<>();
		
		if (pesquisarsexo != null && pesquisarsexo.isEmpty()) {
			pessoas = pessoaRepository.procurarPessoaPorSexo(nomepesquisa, pesquisarsexo);
		}else {
			pessoas = pessoaRepository.procurarPessoaPorNome(nomepesquisa);
		}
		
		
		andView.addObject("pessoas", pessoas );
		andView.addObject("pessoa", new Pessoa());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;	
	}
	
	@GetMapping("**/baixararquivo/{idpessoa}")
	public void baixarArquivo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		
		if (pessoa.getArquivo() != null) {
			response.setContentLength(pessoa.getArquivo().length);
			
			response.setContentType(pessoa.getContentType());
			
			String headerkey = "Content-Disposition";
		
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getFileName());
			response.setHeader(headerkey, headerValue);
			
			response.getOutputStream().write(pessoa.getArquivo());
		}
	}
	
	@GetMapping("/pessoaspag")
	public ModelAndView carragaPessoasPorDemanda(Authentication auth,@PageableDefault(size = 5) Pageable pageable, ModelAndView model) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);

		
		Page<Pessoa> pagePessoa = pessoaRepository.findAll(pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoa", new Pessoa());
		model.addObject("usuarioLogado", usuariolog);
		model.setViewName("cadastro/cadastropessoa");
		return model;
	}
	
	
}
