package br.edu.ifpb.pweb2.primeiraturmadostf.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class NavPage {
    private int currentPage;
    private int totalPages;
    private int totalItems;
    private int pageSize;
    private Long totalElements;
}
