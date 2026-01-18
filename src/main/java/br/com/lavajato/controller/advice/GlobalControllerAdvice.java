package br.com.lavajato.controller.advice;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("empresaLogada")
    public Empresa getEmpresaLogada() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario != null) {
            return usuario.getEmpresa();
        }
        return null;
    }
}
