package br.com.lavajato.controller.venda;

import br.com.lavajato.dto.VendaDTO;
import br.com.lavajato.service.venda.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping("/salvar")
    public ResponseEntity<String> salvarVenda(@RequestBody VendaDTO vendaDTO) {
        try {
            vendaService.registrarVenda(vendaDTO);
            return ResponseEntity.ok("Venda realizada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao realizar venda: " + e.getMessage());
        }
    }
}
