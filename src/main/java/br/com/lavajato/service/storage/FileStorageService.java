package br.com.lavajato.service.storage;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.ServicoAvulsoFoto;
import br.com.lavajato.repository.servico.ServicoAvulsoFotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileStorageService {

    private static final String BASE_UPLOAD_DIR = "uploads";

    @Autowired
    private ServicoAvulsoFotoRepository fotoRepository;

    public void salvarFotosServico(Empresa empresa, ServicoAvulso servicoAvulso, MultipartFile[] arquivos) throws IOException {
        if (arquivos == null || arquivos.length == 0) return;

        Path dir = Path.of(BASE_UPLOAD_DIR, "servicos", String.valueOf(empresa.getId()), String.valueOf(servicoAvulso.getId()));
        Files.createDirectories(dir);

        for (MultipartFile file : arquivos) {
            if (file == null || file.isEmpty()) continue;
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) continue;

            String original = sanitizeFilename(file.getOriginalFilename());
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String filename = ts + "_" + (original != null ? original : "foto.jpg");

            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/servicos/" + empresa.getId() + "/" + servicoAvulso.getId() + "/" + filename;

            ServicoAvulsoFoto foto = new ServicoAvulsoFoto();
            foto.setServicoAvulso(servicoAvulso);
            foto.setUrl(url);
            fotoRepository.save(foto);
        }
    }

    private String sanitizeFilename(String name) {
        if (name == null) return null;
        String n = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        n = n.replaceAll("[^a-zA-Z0-9._-]", "_");
        return n;
    }
}
