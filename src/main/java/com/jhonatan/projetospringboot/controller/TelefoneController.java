package com.jhonatan.projetospringboot.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jhonatan.projetospringboot.model.Pessoa;
import com.jhonatan.projetospringboot.model.Telefone;
import com.jhonatan.projetospringboot.model.Usuario;
import com.jhonatan.projetospringboot.repository.PessoaRepository;
import com.jhonatan.projetospringboot.repository.TelefoneRepository;
import com.jhonatan.projetospringboot.repository.UsuarioRepository;

@Controller
@RequestMapping()
public class TelefoneController {
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private UsuarioRepository  usuarioRepository;
	
	@GetMapping("/telefone/{idpessoa}")
	public ModelAndView telefones(Authentication auth,@PathVariable("idpessoa") Long idpessoa) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		andView.addObject("telefones", telefoneRepository.listaTelefonesPorPessoa(idpessoa));
		andView.addObject("pessoa", pessoa.get());
		andView.addObject("telefone", new Telefone());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;
	}
	
	@PostMapping("**/salvartelefone/{pid}")
	public ModelAndView salvar(Authentication auth,@PathVariable("pid") Long pid ,Telefone telefone) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		
		
		Pessoa pessoa= pessoaRepository.findById(pid).get();
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoa", pessoa);
		andView.addObject("telefone", new Telefone());
		andView.addObject("usuarioLogado", usuariolog);
		andView.addObject("telefones", telefoneRepository.listaTelefonesPorPessoa(pid));
		return andView;
	}
	
	@GetMapping("/deletartelefone/{idTlefone}")
	public ModelAndView deletar(Authentication auth, @PathVariable("idTlefone") Long idTlefone) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		
		
		Telefone telefone = telefoneRepository.findById(idTlefone).get();
		Pessoa pessoa = telefone.getPessoa();
		telefoneRepository.delete(telefone);
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoa", pessoa);
		andView.addObject("telefone", new Telefone());
		andView.addObject("usuarioLogado", usuariolog);
		andView.addObject("telefones", telefoneRepository.listaTelefonesPorPessoa(pessoa.getId()));
		return andView;
	}
	
	@GetMapping("/editartelefone/{idTlefone}")
	public ModelAndView editar(Authentication auth, @PathVariable("idTlefone") Long idTlefone) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		
		
		
		Telefone telefone = telefoneRepository.findById(idTlefone).get();
		Pessoa pessoa = telefone.getPessoa();
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoa", pessoa);
		andView.addObject("telefone", telefone);
		andView.addObject("usuarioLogado", usuariolog);
		andView.addObject("telefones", telefoneRepository.listaTelefonesPorPessoa(pessoa.getId()));
		return andView;
	}
	
}
