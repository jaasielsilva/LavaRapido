package br.com.lavajato.config;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.empresa.EmpresaRepository;
import br.com.lavajato.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (usuarioRepository.findByEmail("master@lavajato.com").isEmpty()) {
                Empresa empresaMaster = criarEmpresa("Lava Jato HQ", "00.000.000/0001-00");
                criarUsuario("Super Admin", "master@lavajato.com", "admin", Perfil.MASTER, empresaMaster);
                System.out.println("Usuário MASTER criado: master@lavajato.com / admin");
            }
        };
    }

    private Empresa criarEmpresa(String nome, String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setNome(nome);
        empresa.setCnpj(cnpj);
        empresa.setAtivo(true);
        return empresaRepository.save(empresa);
    }

    private Usuario criarUsuario(String nome, String email, String senha, Perfil perfil, Empresa empresa) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil);
        usuario.setEmpresa(empresa);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }
}
