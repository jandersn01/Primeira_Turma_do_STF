package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Documento;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    
    List<Documento> findByProcesso(Processo processo);
    
    List<Documento> findByProcessoOrderByDataUploadDesc(Processo processo);
    
}



