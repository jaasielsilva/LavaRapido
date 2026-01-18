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
        return "usuario/list";
    }  

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        
        Usuario logado = usuarioService.getUsuarioLogado();
        if (logado.isMaster()) {
            model.addAttribute("empresas", empresaService.listarTodas());
        }
        
        return "usuario/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Usuario usuario = new Usuario();
        Usuario logado = usuarioService.getUsuarioLogado();
        usuario.setEmpresa(logado.getEmpresa()); // Default to current company
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        
        if (logado.isMaster()) {
            model.addAttribute("empresas", empresaService.listarTodas());
        }
        
        return "usuario/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        Usuario logado = usuarioService.getUsuarioLogado();
        if (!logado.isMaster()) {
            usuario.setEmpresa(logado.getEmpresa()); // Garante segurança
        }
        usuarioService.salvar(usuario);
        return "redirect:/usuarios";
    }
}
