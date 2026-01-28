package br.com.lavajato.service.empresa;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.repository.empresa.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public Page<Empresa> listarPaginado(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    public long contarAtivas() {
        return empresaRepository.countByAtivoTrue();
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Empresa salvar(Empresa empresa) {
        if (empresa.getCnpj() != null) {
            String digits = empresa.getCnpj().replaceAll("\\D", "");
            if (digits.length() > 14) {
                digits = digits.substring(0, 14);
            }
            if (digits.length() == 14) {
                String formatted = digits.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
                empresa.setCnpj(formatted);
            }
        }
        return empresaRepository.save(empresa);
    }

    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }
    
    public void excluir(Long id) {
        empresaRepository.deleteById(id);
    }
}
