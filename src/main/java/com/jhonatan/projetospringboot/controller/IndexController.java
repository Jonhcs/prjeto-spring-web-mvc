package com.jhonatan.projetospringboot.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.jhonatan.projetospringboot.model.Pessoa;
import com.jhonatan.projetospringboot.model.Role;
import com.jhonatan.projetospringboot.model.Usuario;
import com.jhonatan.projetospringboot.repository.PessoaRepository;
import com.jhonatan.projetospringboot.repository.RoleRepository;
import com.jhonatan.projetospringboot.repository.UsuarioRepository;

@Controller
@RequestMapping("/")
public class IndexController {
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository; 
	
	@GetMapping
	public ModelAndView index(Authentication auth, HttpSession session) {
		String nome = auth.getName();
		ModelAndView andView = new ModelAndView("index");
		Usuario usuario = usuarioRepository.buscarUsuarioPorLogin(nome);
		
		if ( usuario != null) {
			andView.addObject("usuarioLogado", usuario);
			return andView;
		}

		return andView;
	}
	
	@GetMapping("/editarperfil")
	public ModelAndView mostrarEdicao(Authentication auth) {
		String login = auth.getName();
		ModelAndView andView = new ModelAndView("cadastro/editarprincipal.html");
		Usuario usuario = usuarioRepository.buscarUsuarioPorLogin(login);
		List<Pessoa> pessoas = pessoaRepository.findAll();
		andView.addObject("usuario", usuario);
		andView.addObject("pessoas", pessoas);
		andView.addObject("usuarioLogado", usuario);
		
		return andView;
	}

	@GetMapping("/cadastrousuario")
	public ModelAndView inicio(Authentication auth) {
		String login = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(login);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastrousuario");
		andView.addObject("roles", roleRepository.findAll());
		andView.addObject("usuarioLogado", usuariolog);
		return andView;
	}
	
	@PostMapping(path = "/cadastrousuario",  consumes = "multipart/form-data")
	public ModelAndView salvar(Authentication auth, Usuario usuario, final MultipartFile file) throws IOException {
		ModelAndView andView = new ModelAndView("login");
		String nome = auth.getName();
		Usuario usuariolog = usuarioRepository.buscarUsuarioPorLogin(nome);
		usuario.setSenha(bcrypt.encode(usuario.getSenha()));
		
		
		if (file.getSize() > 0) {
			usuario.setArquivo(file.getBytes());
			usuario.setContentType(file.getContentType());
			usuario.setFileName(file.getOriginalFilename());
			
		}else {
			if (usuario.getId() != null && usuario.getId() > 0) {
				Usuario usuarioTemp = usuarioRepository.findById(usuario.getId()).get();
				usuario.setArquivo(usuarioTemp.getArquivo());
				usuario.setContentType(usuarioTemp.getContentType());
				usuario.setFileName(usuarioTemp.getFileName());
			}
		}
		
		usuarioRepository.save(usuario);
		andView.addObject("usuarioLogado", usuariolog);
		SecurityContextHolder.clearContext();
		return andView;
	}
}
