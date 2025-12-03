package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Documento;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.DocumentoRepository;

@Service
@Transactional
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    // Tipos MIME permitidos
    private static final String[] TIPOS_PERMITIDOS = {
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "image/jpeg",
        "image/png",
        "image/gif",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
    };
    
    // Tamanho máximo: 10MB
    private static final long TAMANHO_MAXIMO = 10 * 1024 * 1024; // 10MB em bytes

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    /**
     * Faz upload de um documento para um processo.
     * 
     * @param arquivo Arquivo a ser enviado
     * @param processo Processo ao qual o documento pertence
     * @param descricao Descrição opcional do documento
     * @return Documento salvo
     * @throws IOException Se houver erro ao salvar o arquivo
     * @throws IllegalArgumentException Se o arquivo for inválido
     */
    public Documento uploadDocumento(MultipartFile arquivo, Processo processo, String descricao) 
            throws IOException, IllegalArgumentException {
        
        // Validar arquivo
        validarArquivo(arquivo);
        
        // Criar diretório se não existir
        Path diretorioUpload = Paths.get(uploadDir, "processos", processo.getId().toString());
        Files.createDirectories(diretorioUpload);
        
        // Gerar nome único para o arquivo
        String extensao = obterExtensao(arquivo.getOriginalFilename());
        String nomeArquivo = UUID.randomUUID().toString() + "." + extensao;
        Path caminhoCompleto = diretorioUpload.resolve(nomeArquivo);
        
        // Salvar arquivo no sistema de arquivos
        Files.copy(arquivo.getInputStream(), caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
        
        // Criar entidade Documento
        Documento documento = new Documento();
        documento.setNomeOriginal(arquivo.getOriginalFilename());
        documento.setNomeArquivo(nomeArquivo);
        documento.setCaminho(caminhoCompleto.toString());
        documento.setTipoMime(arquivo.getContentType());
        documento.setTamanho(arquivo.getSize());
        documento.setDescricao(descricao);
        documento.setProcesso(processo);
        documento.setDataUpload(LocalDateTime.now());
        
        // Salvar no banco de dados
        return documentoRepository.save(documento);
    }
    
    /**
     * Lista todos os documentos de um processo.
     */
    public List<Documento> findByProcesso(Processo processo) {
        return documentoRepository.findByProcessoOrderByDataUploadDesc(processo);
    }
    
    /**
     * Busca um documento por ID.
     */
    public Documento findById(Long id) {
        return documentoRepository.findById(id)
                .orElse(null);
    }
    
    /**
     * Remove um documento (arquivo e registro no banco).
     */
    public boolean removerDocumento(Long id) throws IOException {
        Documento documento = findById(id);
        if (documento == null) {
            return false;
        }
        
        // Remover arquivo do sistema de arquivos
        Path caminhoArquivo = Paths.get(documento.getCaminho());
        if (Files.exists(caminhoArquivo)) {
            Files.delete(caminhoArquivo);
        }
        
        // Remover registro do banco
        documentoRepository.delete(documento);
        return true;
    }
    
    /**
     * Valida o arquivo antes do upload.
     */
    private void validarArquivo(MultipartFile arquivo) throws IllegalArgumentException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio.");
        }
        
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 10MB.");
        }
        
        String tipoMime = arquivo.getContentType();
        if (tipoMime == null || !isTipoPermitido(tipoMime)) {
            throw new IllegalArgumentException(
                "Tipo de arquivo não permitido. Tipos permitidos: PDF, DOC, DOCX, XLS, XLSX, JPG, PNG, GIF.");
        }
    }
    
    /**
     * Verifica se o tipo MIME é permitido.
     */
    private boolean isTipoPermitido(String tipoMime) {
        for (String tipo : TIPOS_PERMITIDOS) {
            if (tipo.equals(tipoMime)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtém a extensão do arquivo.
     */
    private String obterExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            return "bin";
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();
    }
}

