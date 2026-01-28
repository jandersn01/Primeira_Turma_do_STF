package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Documento;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.DocumentoRepository;

@Service
@Transactional
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    // Tipos MIME permitidos - Apenas PDF conforme RF
    private static final String[] TIPOS_PERMITIDOS = {
        "application/pdf"
    };

    // Tamanho máximo: 5MB conforme RF
    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024; // 5MB em bytes

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    /**
     * Faz upload de um documento para um processo.
     * Apenas processos com status CRIADO podem receber documentos.
     *
     * @param arquivo Arquivo a ser enviado
     * @param processo Processo ao qual o documento pertence
     * @param descricao Descrição opcional do documento
     * @return Documento salvo
     * @throws IOException Se houver erro ao salvar o arquivo
     * @throws IllegalArgumentException Se o arquivo for inválido
     * @throws IllegalStateException Se o processo não estiver com status CRIADO
     */
    public Documento uploadDocumento(MultipartFile arquivo, Processo processo, String descricao)
            throws IOException, IllegalArgumentException, IllegalStateException {

        // Validar status do processo - apenas CRIADO permite upload
        validarStatusProcesso(processo);

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
     * Carrega o arquivo como Resource para download.
     *
     * @param documento Documento a ser carregado
     * @return Resource do arquivo
     * @throws IOException Se o arquivo não existir ou não puder ser lido
     */
    public Resource carregarArquivoComoResource(Documento documento) throws IOException {
        if (documento == null) {
            throw new IllegalArgumentException("Documento não pode ser nulo.");
        }

        Path caminhoArquivo = Paths.get(documento.getCaminho());
        if (!Files.exists(caminhoArquivo)) {
            throw new IOException("Arquivo não encontrado: " + documento.getNomeOriginal());
        }

        try {
            Resource resource = new UrlResource(caminhoArquivo.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Não foi possível ler o arquivo: " + documento.getNomeOriginal());
            }
        } catch (MalformedURLException e) {
            throw new IOException("Erro ao acessar o arquivo: " + e.getMessage());
        }
    }

    /**
     * Remove um documento (arquivo e registro no banco).
     * Apenas documentos de processos com status CRIADO podem ser removidos.
     *
     * @param id ID do documento
     * @param processo Processo ao qual o documento pertence (para validação de status)
     * @return true se removido com sucesso
     * @throws IOException Se houver erro ao remover o arquivo
     * @throws IllegalStateException Se o processo não estiver com status CRIADO
     */
    public boolean removerDocumento(Long id, Processo processo) throws IOException, IllegalStateException {
        // Validar status do processo
        validarStatusProcesso(processo);

        Documento documento = findById(id);
        if (documento == null) {
            return false;
        }

        // Verificar se o documento pertence ao processo informado
        if (!documento.getProcesso().getId().equals(processo.getId())) {
            throw new IllegalArgumentException("Documento não pertence ao processo informado.");
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
     * Remove um documento (arquivo e registro no banco).
     * @deprecated Use removerDocumento(Long id, Processo processo) para validar status
     */
    @Deprecated
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
     * Valida se o processo está com status que permite upload de documentos.
     * Apenas processos com status CRIADO podem receber novos documentos.
     */
    private void validarStatusProcesso(Processo processo) throws IllegalStateException {
        if (processo == null) {
            throw new IllegalArgumentException("Processo não pode ser nulo.");
        }
        if (processo.getStatus() != StatusProcesso.CRIADO) {
            throw new IllegalStateException(
                "Não é possível anexar documentos a este processo. " +
                "Apenas processos com status 'Criado' aceitam novos documentos. " +
                "Status atual: " + processo.getStatus().getDescricao());
        }
    }

    /**
     * Verifica se o processo permite upload de documentos.
     * @param processo Processo a ser verificado
     * @return true se o processo permite upload, false caso contrário
     */
    public boolean podeAnexarDocumento(Processo processo) {
        return processo != null && processo.getStatus() == StatusProcesso.CRIADO;
    }

    /**
     * Valida o arquivo antes do upload.
     */
    private void validarArquivo(MultipartFile arquivo) throws IllegalArgumentException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio.");
        }
        
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("Arquivo muito grande. Tamanho máximo permitido: 5MB.");
        }

        String tipoMime = arquivo.getContentType();
        if (tipoMime == null || !isTipoPermitido(tipoMime)) {
            throw new IllegalArgumentException(
                "Tipo de arquivo não permitido. Apenas arquivos PDF são aceitos.");
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



