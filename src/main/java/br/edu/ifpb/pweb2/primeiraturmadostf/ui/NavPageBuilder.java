package br.edu.ifpb.pweb2.primeiraturmadostf.ui;

public class NavPageBuilder {
    private NavPage paginator;
    public static NavPage newNavPage(int currentPage, int pageSize, int totalPages, int totalItems, Long totalElements) {
        NavPageBuilder builder = new NavPageBuilder();
        builder.start();
        builder.setCurrentPage(currentPage);
        builder.setTotalItems(totalItems);
        builder.setTotalPages(totalPages);
        builder.setPageSize(pageSize);
        builder.setTotalElements(totalElements);
        return builder.finish();
    }

    private NavPageBuilder(){
        this.start();
    }



    public NavPageBuilder start(){
        this.paginator = new NavPage();
        return this;
    }

    public NavPageBuilder setCurrentPage(int currentPage){
        this.paginator.setCurrentPage(currentPage);
        return this;
    }

    public NavPageBuilder setTotalItems(int totalItems){
        this.paginator.setTotalItems(totalItems);
        return this;
    }

    public NavPageBuilder setTotalPages(int totalPages){
        this.paginator.setTotalPages(totalPages);
        return this;
    }

    public NavPageBuilder setPageSize(int pageSize){
        this.paginator.setPageSize(pageSize);
        return this;

    }

    public NavPageBuilder setTotalElements(Long totalElements){
        this.paginator.setTotalElements(totalElements);
        return this;
    }

    public NavPage finish(){
        return this.paginator;
    }


}
