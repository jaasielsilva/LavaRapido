package br.com.lavajato.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NovoContratoDTO {
    // Dados da Empresa
    private String nomeEmpresa;
    private String cnpj;
    private MultipartFile logoFile;

    // Dados do Admin
    private String nomeAdmin;
    private String emailAdmin;
    private String senhaAdmin;
}
