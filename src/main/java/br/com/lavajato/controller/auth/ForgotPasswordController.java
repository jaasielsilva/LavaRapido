package br.com.lavajato.controller.auth;

import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/esqueci-senha")
    public String esqueciSenhaForm() {
        return "auth/esqueci-senha";
    }

    @PostMapping("/esqueci-senha")
    public String processarEsqueciSenha(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        boolean sucesso = usuarioService.processarEsqueciSenha(email);
        
        if (sucesso) {
            redirectAttributes.addFlashAttribute("success", "Uma nova senha temporária foi enviada para o seu e-mail. Verifique sua caixa de entrada (e spam).");
        } else {
            redirectAttributes.addFlashAttribute("error", "E-mail não encontrado no sistema.");
        }
        
        return "redirect:/login";
    }
}
