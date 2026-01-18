package br.com.lavajato.controller.empresa;

import br.com.lavajato.dto.NovoContratoDTO;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.service.empresa.EmpresaService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empresas", empresaService.listarTodas());
        return "empresa/list";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresa/form";
    }
    
    @GetMapping("/novo-contrato")
    public String novoContrato(Model model) {
        model.addAttribute("contrato", new NovoContratoDTO());
        return "empresa/contrato-form";
    }

    @PostMapping("/salvar-contrato")
    public String salvarContrato(@ModelAttribute NovoContratoDTO contrato) {
        try {
            // 1. Criar e Salvar Empresa
            Empresa empresa = new Empresa();
            empresa.setNome(contrato.getNomeEmpresa());
            empresa.setCnpj(contrato.getCnpj());
            
            if (contrato.getLogoFile() != null && !contrato.getLogoFile().isEmpty()) {
                String base64 = java.util.Base64.getEncoder().encodeToString(contrato.getLogoFile().getBytes());
                String mimeType = contrato.getLogoFile().getContentType();
                empresa.setLogo("data:" + mimeType + ";base64," + base64);
            }
            
            empresa = empresaService.salvar(empresa);
            
            // 2. Criar e Salvar Usuário Admin vinculado
            Usuario admin = new Usuario();
            admin.setNome(contrato.getNomeAdmin());
            admin.setEmail(contrato.getEmailAdmin());
            admin.setSenha(contrato.getSenhaAdmin()); // Service vai criptografar
            admin.setPerfil(Perfil.ADMIN);
            admin.setEmpresa(empresa);
            admin.setAtivo(true);
            
            usuarioService.salvar(admin);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Em produção, adicionar erro ao model e retornar para o form
        }
        
        return "redirect:/empresas";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Empresa empresa, @RequestParam("logoFile") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                // Converter para Base64
                String base64 = java.util.Base64.getEncoder().encodeToString(file.getBytes());
                String mimeType = file.getContentType();
                empresa.setLogo("data:" + mimeType + ";base64," + base64);
            } else if (empresa.getId() != null) {
                // Manter o logo existente se não houve upload
                empresaService.buscarPorId(empresa.getId()).ifPresent(existing -> {
                    if (empresa.getLogo() == null || empresa.getLogo().isEmpty()) {
                        empresa.setLogo(existing.getLogo());
                    }
                });
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        
        empresaService.salvar(empresa);
        return "redirect:/empresas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("empresa", empresaService.buscarPorId(id).orElseThrow());
        return "empresa/form";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        empresaService.excluir(id);
        return "redirect:/empresas";
    }
}
