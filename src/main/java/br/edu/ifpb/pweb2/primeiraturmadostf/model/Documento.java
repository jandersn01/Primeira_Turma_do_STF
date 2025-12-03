package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "documento",
    indexes = {
        @Index(name = "idx_documento_processo", columnList = "processo_id")
    }
)
public class Documento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String nomeOriginal; // Nome original do arquivo enviado pelo usuário
    
    @Column(nullable = false, length = 255)
    private String nomeArquivo; // Nome do arquivo no sistema de arquivos
    
    @Column(nullable = false, length = 500)
    private String caminho; // Caminho completo do arquivo no sistema
    
    @Column(nullable = false, length = 100)
    private String tipoMime; // Ex: application/pdf, image/jpeg, etc.
    
    @Column(nullable = false)
    private Long tamanho; // Tamanho em bytes
    
    @Column(nullable = false)
    private LocalDateTime dataUpload;
    
    @Column(length = 500)
    private String descricao; // Descrição opcional do documento
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;
    
    // Construtores
    public Documento() {
        this.dataUpload = LocalDateTime.now();
    }
    
    public Documento(String nomeOriginal, String nomeArquivo, String caminho, 
                     String tipoMime, Long tamanho, Processo processo) {
        this();
        this.nomeOriginal = nomeOriginal;
        this.nomeArquivo = nomeArquivo;
        this.caminho = caminho;
        this.tipoMime = tipoMime;
        this.tamanho = tamanho;
        this.processo = processo;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNomeOriginal() {
        return nomeOriginal;
    }
    
    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }
    
    public String getNomeArquivo() {
        return nomeArquivo;
    }
    
    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
    
    public String getCaminho() {
        return caminho;
    }
    
    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }
    
    public String getTipoMime() {
        return tipoMime;
    }
    
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }
    
    public Long getTamanho() {
        return tamanho;
    }
    
    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }
    
    public LocalDateTime getDataUpload() {
        return dataUpload;
    }
    
    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Processo getProcesso() {
        return processo;
    }
    
    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
    
    // Métodos auxiliares
    public String getTamanhoFormatado() {
        if (tamanho < 1024) {
            return tamanho + " B";
        } else if (tamanho < 1024 * 1024) {
            return String.format("%.2f KB", tamanho / 1024.0);
        } else {
            return String.format("%.2f MB", tamanho / (1024.0 * 1024.0));
        }
    }
    
    public String getExtensao() {
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            return nomeOriginal.substring(nomeOriginal.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "Documento{id=" + id + ", nomeOriginal='" + nomeOriginal + "', processo=" + 
               (processo != null ? processo.getNumero() : "null") + "}";
    }
}

