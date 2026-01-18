package br.com.lavajato.controller.usuario;

import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.service.empresa.EmpresaService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage = usuarioService.listarPaginado(pageable);
        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("usuariosInativos", usuarioService.listarInativos());
        return "usuario/list";
    }  

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + id));
        Usuario logado = usuarioService.getUsuarioLogado();
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("isMaster", logado.isMaster());
        if (logado.isMaster()) {
            model.addAttribute("empresas", empresaService.listarTodas());
        }
        return "usuario/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(id);
            redirectAttributes.addFlashAttribute("usuarioSucesso", "excluido");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("usuarioErro", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Usuario usuario = new Usuario();
        Usuario logado = usuarioService.getUsuarioLogado();
        usuario.setEmpresa(logado.getEmpresa()); // Default to current company
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("isMaster", logado.isMaster());
        
        if (logado.isMaster()) {
            model.addAttribute("empresas", empresaService.listarTodas());
        }
        
        return "usuario/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        Usuario logado = usuarioService.getUsuarioLogado();
        if (!logado.isMaster()) {
            usuario.setEmpresa(logado.getEmpresa()); // Garante segurança
        }
        try {
            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("usuarioSucesso", usuario.getId() == null ? "criado" : "atualizado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("usuarioErro", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
