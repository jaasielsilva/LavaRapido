package br.com.lavajato.config;

import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.service.usuario.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Usuario usuario = usuarioService.getUsuarioLogado();
        
        if (usuario != null && usuario.isAlterarSenhaProximoLogin()) {
            response.sendRedirect("/alterar-senha");
        } else {
            super.setDefaultTargetUrl("/dashboard");
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
