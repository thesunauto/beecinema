package vn.edu.poly.beecinema.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.poly.beecinema.entity.Khunggio;
import vn.edu.poly.beecinema.entity.Suatchieu;
import vn.edu.poly.beecinema.entity.Sukien;
import vn.edu.poly.beecinema.repository.SuatchieuRepository;
import vn.edu.poly.beecinema.service.SuatChieuService;

import java.util.List;
import java.util.Optional;

@Service
public class SuatChieuServiceImpl implements SuatChieuService {
    @Autowired
    SuatchieuRepository suatchieuRepository;

    @Override
    public Suatchieu findById(Integer id) {
        return Optional.ofNullable(id).map(integer -> suatchieuRepository.getOne(id)).orElse(null);
    }

    @Override
    public List<Suatchieu> getAllSuatChieu() {
        return (List<Suatchieu>) suatchieuRepository.findAll();
    }

    @Override
    public void saveSuatChieu(Suatchieu suatChieu) {
        suatchieuRepository.save(suatChieu);
    }

    @Override
    public void deleteSuatChieu(Integer id) {
        suatchieuRepository.deleteById(id);
    }

    @Override
    public Optional<Suatchieu> findSuatChieuById(Integer id) {
        return suatchieuRepository.findById(id);
    }

    @Override
    public Page<Suatchieu> listAll(int pageNumber, String sortField, String sortDir, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 5,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );
        if (keyword != null) {
            return suatchieuRepository.findAll(keyword, pageable);
        }
        return suatchieuRepository.findAll(pageable);
    }
}
