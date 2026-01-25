package br.com.lavajato.controller.auth;

import br.com.lavajato.service.empresa.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "empresaId", required = false) Long empresaId, Model model) {
        if (empresaId != null) {
            empresaService.buscarPorId(empresaId).ifPresent(empresa -> {
                model.addAttribute("empresaLogo", empresa.getLogo());
                model.addAttribute("empresaNome", empresa.getNome());
            });
        }
        return "login";
    }
}
