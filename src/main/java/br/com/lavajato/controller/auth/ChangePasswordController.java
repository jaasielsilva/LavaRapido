package br.com.lavajato.controller.auth;

import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.usuario.UsuarioRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChangePasswordController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/alterar-senha")
    public String changePasswordForm(Model model) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        // Se não estiver logado ou não precisar alterar senha, redireciona para dashboard
        if (usuario == null) {
            return "redirect:/login";
        }
        if (!usuario.isAlterarSenhaProximoLogin()) {
            return "redirect:/dashboard";
        }
        
        return "auth/alterar-senha";
    }

    @PostMapping("/alterar-senha")
    public String updatePassword(@RequestParam("senha") String senha, 
                                 @RequestParam("confirmarSenha") String confirmarSenha,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario == null) {
            return "redirect:/login";
        }

        if (!senha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
            return "redirect:/alterar-senha";
        }
        
        if (senha.length() < 6) {
             redirectAttributes.addFlashAttribute("error", "A senha deve ter pelo menos 6 caracteres.");
             return "redirect:/alterar-senha";
        }

        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setAlterarSenhaProximoLogin(false);
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "Senha alterada com sucesso! Você já pode acessar o sistema.");
        return "redirect:/dashboard";
    }
}
