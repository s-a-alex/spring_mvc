package util;

import entity.Singer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SingerGrid {
    private int totalPages;
    private int currentPage;
    private long totalRecords;
    private List<Singer> singerData;

    public SingerGrid(int totalPages, int currentPage, long totalRecords, List<Singer> singerData){
        setTotalPages(totalPages);
        setCurrentPage(currentPage);
        setTotalRecords(totalRecords);
        setSingerData(singerData);
    }
}
