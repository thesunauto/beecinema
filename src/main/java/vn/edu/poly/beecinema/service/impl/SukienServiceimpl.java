package vn.edu.poly.beecinema.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.poly.beecinema.entity.LoaiPhim;
import vn.edu.poly.beecinema.entity.Sukien;
import vn.edu.poly.beecinema.repository.SukienRepository;
import vn.edu.poly.beecinema.service.SukienService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SukienServiceimpl implements SukienService {
    @Autowired
    private SukienRepository sukienRepository;

    @Override
//    public List<Sukien> getAllSukien() {
//        List<Sukien> sukiens = sukienRepository.findAll();
//        return sukiens;
//    }
    public List<Sukien> getAllSukien() {
        return (List<Sukien>) sukienRepository.findAll();
    }
    @Override
    public void saveSukien(Sukien sukien) {
        sukienRepository.save(sukien);
    }

    @Override
    public void deleteSukien(String id) {
        sukienRepository.deleteById(id);
    }

    @Override
    public Optional<Sukien> findSukienById(String id) {
        return sukienRepository.findById(id);
    }

    @Override
    public List<Sukien> getAllSuKienActive() {
        List<Sukien> sukiens = new ArrayList<>();
        sukienRepository.findAll().forEach(sukien -> {
            if(sukien.getNgaybatdau().compareTo(LocalDateTime.now())<0&&sukien.getNgayketthuc().compareTo(LocalDateTime.now())>0){
                sukiens.add(sukien);
            }
        });
        return sukiens;
    }
}
