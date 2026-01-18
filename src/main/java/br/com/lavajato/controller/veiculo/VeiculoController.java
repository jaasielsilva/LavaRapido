package br.com.lavajato.controller.veiculo;

import br.com.lavajato.model.veiculo.Veiculo;
import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.veiculo.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;
    
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String busca,
            Model model) {
        
        Page<Veiculo> veiculosPage = veiculoService.listarPaginado(
            PageRequest.of(page, size, Sort.by("placa").ascending()), 
            busca
        );
        
        model.addAttribute("veiculos", veiculosPage);
        model.addAttribute("busca", busca);
        return "veiculo/list";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Long clienteId, Model model) {
        Veiculo veiculo = new Veiculo();
        
        if (clienteId != null) {
            clienteService.buscarPorId(clienteId).ifPresent(veiculo::setCliente);
        }
        
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("clientes", clienteService.listarTodos());
        return "veiculo/form";
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
    public String salvar(@ModelAttribute Veiculo veiculo) {
        veiculoService.salvar(veiculo);
        return "redirect:/clientes"; // Redireciona para clientes para ver o veículo adicionado no card
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("veiculo", veiculoService.buscarPorId(id).orElseThrow());
        model.addAttribute("clientes", clienteService.listarTodos());
        return "veiculo/form";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        veiculoService.excluir(id);
        return "redirect:/veiculos";
    }
}
