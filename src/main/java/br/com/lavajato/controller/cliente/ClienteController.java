package br.com.lavajato.controller.cliente;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.empresa.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientesPage = clienteService.listarPaginado(pageable);
        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("clientes", clientesPage.getContent());
        return "cliente/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("empresas", empresaService.listarTodas());
        return "cliente/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente) {
        clienteService.salvar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id).orElseThrow());
        model.addAttribute("empresas", empresaService.listarTodas());
        return "cliente/form";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return "redirect:/clientes";
    }
}
