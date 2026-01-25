package br.com.lavajato.controller.veiculo;

import br.com.lavajato.model.veiculo.Veiculo;
import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.veiculo.VeiculoService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;
    
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(
            @PageableDefault(size = 10, sort = "placa", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String busca,
            Model model) {
        
        Page<Veiculo> veiculosPage = veiculoService.listarPaginado(pageable, busca);
        
        model.addAttribute("veiculos", veiculosPage);
        model.addAttribute("busca", busca);
        return "veiculo/list";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long clienteId, Model model) {
        try {
            Veiculo veiculo = new Veiculo();
            
            if (clienteId != null) {
                clienteService.buscarPorId(clienteId).ifPresent(veiculo::setCliente);
            }
            
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("clientes", clienteService.listarTodos());
            return "veiculo/form";
        } catch (Exception e) {
            model.addAttribute("erroGlobal", "Erro ao carregar formulário: " + e.getMessage());
            return "redirect:/veiculos";
        }
    }

    @PostMapping(value = "/salvar", headers = "X-Requested-With=XMLHttpRequest")
    @ResponseBody
    public ResponseEntity<String> salvarAjax(@ModelAttribute Veiculo veiculo) {
        try {
            veiculoService.salvar(veiculo);
            return ResponseEntity.ok("Veículo salvo com sucesso");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro ao salvar o veículo");
        }
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("veiculo") Veiculo veiculo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Importante: Recarregar listas auxiliares (clientes) para o dropdown
            model.addAttribute("clientes", clienteService.listarTodos());
            return "veiculo/form";
        }
        
        try {
            veiculoService.salvar(veiculo);
            redirectAttributes.addFlashAttribute("veiculoSucesso", "salvo");
            return "redirect:/veiculos";
        } catch (IllegalStateException e) {
            // Adiciona o erro global e recarrega listas
            model.addAttribute("erroGlobal", e.getMessage());
            model.addAttribute("clientes", clienteService.listarTodos());
            return "veiculo/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Veiculo veiculo = veiculoService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado ou você não tem permissão para acessá-lo."));
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("clientes", clienteService.listarTodos());
            return "veiculo/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("veiculoErro", e.getMessage());
            return "redirect:/veiculos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("veiculoErro", "Erro ao carregar veículo: " + e.getMessage());
            return "redirect:/veiculos";
        }
    }
    
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        br.com.lavajato.model.usuario.Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        try {
            if (usuarioLogado.getPerfil() == br.com.lavajato.model.usuario.Perfil.FUNCIONARIO) {
                throw new IllegalStateException("Você não tem permissão para excluir este registro.");
            }
            veiculoService.excluir(id);
            redirectAttributes.addFlashAttribute("veiculoSucesso", "excluido");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("veiculoErro", e.getMessage());
        }
        return "redirect:/veiculos";
    }
}
