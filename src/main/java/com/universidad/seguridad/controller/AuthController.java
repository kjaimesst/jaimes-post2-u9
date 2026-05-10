package com.universidad.seguridad.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.universidad.seguridad.model.Usuario;
import com.universidad.seguridad.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/registro";
        }
        try {
            usuarioService.registrar(usuario);
            return "redirect:/login?registrado";
        } catch (RuntimeException ex) {
            bindingResult.rejectValue("email", "error.usuario", ex.getMessage());
            return "auth/registro";
        }
    }

   @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
    String email = authentication.getName();
    
    usuarioService.buscarPorEmail(email)
            .ifPresent(u -> model.addAttribute("nombreUsuario", u.getNombre()));
    
    return "dashboard";
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/panel";
    }
}
