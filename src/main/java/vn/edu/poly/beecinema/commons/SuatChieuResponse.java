package vn.edu.poly.beecinema.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuatChieuResponse {
    private Integer id;
    private String idphim;
    private String idphong;
    private String tenphong;
    private String batdau;
    private String ketthuc;
    private Double dongia;
}
