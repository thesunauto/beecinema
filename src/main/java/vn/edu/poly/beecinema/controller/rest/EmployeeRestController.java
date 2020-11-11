package vn.edu.poly.beecinema.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import vn.edu.poly.beecinema.commons.*;
import vn.edu.poly.beecinema.config.HttpSessionConfig;
import vn.edu.poly.beecinema.entity.Ghe;
import vn.edu.poly.beecinema.entity.Phim;
import vn.edu.poly.beecinema.entity.Suatchieu;
import vn.edu.poly.beecinema.entity.Sukien;
import vn.edu.poly.beecinema.service.*;
import vn.edu.poly.beecinema.storage.StorageService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeRestController {
    @Autowired
    private PhimService phimService;
    @Autowired
    private SuatChieuService suatChieuService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DayGheService dayGheService;
    @Autowired
    private GheService gheService;
    @Autowired
    private VeService veService;
    @Autowired
    private  SukienService sukienService;

    public EmployeeRestController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/list-film-showing")
    public ResponseEntity listFilmShowing() {
        List<PhimResponse> phimResponses = new ArrayList<>();
        phimService.getAllPhim().forEach(value -> {
            PhimResponse phim = new PhimResponse();
            List<Integer> idsuatchieu = new ArrayList<>();
            value.getSuatchieus().forEach(suatchieu -> {
                idsuatchieu.add(suatchieu.getId());
            });
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            phim.setId(value.getId());
            phim.setDotuoi(value.getDotuoi().getTen());
            phim.setNgonngu(value.getNgonngu().getTen());
            phim.setLoaiphim(value.getLoaiphim().getTen());
            phim.setTen(value.getTen());
            phim.setHinhanh(value.getHinhanh());
            phim.setNgaybatdau(value.getNgaybatdau().format(dateTimeFormatter));
            phim.setNgayketthuc(value.getNgayketthuc().format(dateTimeFormatter));
            phim.setDodai(value.getDodai());
            phim.setIdsuatchieu(idsuatchieu);
            phimResponses.add(phim);
        });
        return ResponseEntity.ok().body(phimResponses);
    }


    @GetMapping("/getone/{id}")
    public ResponseEntity getFilm(@PathVariable(value = "id") String id) {

        Phim value = phimService.findPhimById(id).get();
        PhimResponse phim = new PhimResponse();
        List<Integer> idsuatchieu = new ArrayList<>();
        value.getSuatchieus().forEach(suatchieu -> {
            idsuatchieu.add(suatchieu.getId());
        });
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        phim.setId(value.getId());
        phim.setDotuoi(value.getDotuoi().getTen());
        phim.setNgonngu(value.getNgonngu().getTen());
        phim.setLoaiphim(value.getLoaiphim().getTen());
        phim.setTen(value.getTen());
        phim.setHinhanh(value.getHinhanh());
        phim.setNgaybatdau(value.getNgaybatdau().format(dateTimeFormatter));
        phim.setNgayketthuc(value.getNgayketthuc().format(dateTimeFormatter));
        phim.setDodai(value.getDodai());
        phim.setIdsuatchieu(idsuatchieu);

        return ResponseEntity.ok().body(phim);

    }

    @GetMapping("/getResourse/{filename:.+}")
    public ResponseEntity getResourseFile(@PathVariable String filename) {
        Resource resource = storageService.loadAsResource(filename);
        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/getPath/{filename:.+}")
    public ResponseEntity getPathFile(@PathVariable String filename) {
        Path resource = storageService.load(filename);
        return ResponseEntity.ok().body(resource);
    }


    @GetMapping("/getGhe/{idsuatchieu}")
    public ResponseEntity getGhe(HttpSessionConfig httpSessionConfig, HttpSession httpSession, @PathVariable Integer idsuatchieu) {
        List<VeResponse> veResponses = new ArrayList<>();
        final List<VeResponse> veResponsesCurent = (List<VeResponse>) httpSession.getAttribute("veresponse");
        List<HttpSession> httpSessions = httpSessionConfig.getActiveSessions();
        if (!httpSessions.isEmpty()) {
            httpSessions.forEach(session -> {
                if(session!=null){
                    if (session.getAttribute("veresponse") != null) {
                        veResponses.addAll((List<VeResponse>) session.getAttribute("veresponse"));
                    }
                }
            });
        }



        List<DayGheResponse> gheResponses = new ArrayList<>();
        Suatchieu suatchieu = suatChieuService.findById(idsuatchieu);
        dayGheService.findDayGheByPhong(suatchieu.getPhong().getId()).forEach(dayghe -> {
            List<GheResponse> gheResponses1 = new ArrayList<>();
            gheService.findByPhongAndDayGhe(suatchieu.getPhong().getId(), dayghe.getId()).forEach(ghe -> {
                Integer stt = ghe.getTrangthai();
                for (VeResponse veResponse : veResponses) {
                    if (veResponse.getIdsuatchieu().equals(suatchieu.getId())) {
                        if (veResponse.getIdghe().equals(ghe.getId())) {
                            stt = 3;
                            for(VeResponse veResponse1 : veResponsesCurent){
                                if(veResponse1.getIdsuatchieu().equals(suatchieu.getId())){
                                    if(veResponse1.getIdghe().equals(ghe.getId())){
                                        stt =2;
                                    }
                                }
                            }
                        }
                    }
                }



                if (veService.IsExists(suatchieu.getId(), ghe.getId())) {
                    stt = 1;
                }
                if (ghe.getTrangthai() == 1) {
                    stt = 4;
                }

                gheResponses1.add(new GheResponse(ghe.getId(), ghe.getCol(), ghe.getPhong().getId(), ghe.getDayghe().getId(), ghe.getDayghe().getTen(), ghe.getLoaighe().getId(), stt));
            });
            gheResponses.add(new DayGheResponse(dayghe.getId(), dayghe.getTen(), gheResponses1));
        });
        return ResponseEntity.ok().body(gheResponses);
    }

    @PostMapping("/setghefocus")
    @ResponseBody
    public ResponseEntity setGheFocus(HttpSession httpSession, @RequestBody VeResponse veResponse) {
        if (httpSession.getAttribute("veresponse") == null) {
            httpSession.setAttribute("veresponse", new ArrayList<VeResponse>());
        }
        List<VeResponse> veResponses = (List<VeResponse>) httpSession.getAttribute("veresponse");
        for (int i = 0; i < veResponses.size(); i++) {
            if (veResponse.getIdsuatchieu().equals(veResponses.get(i).getIdsuatchieu())) {
                if (veResponse.getIdghe().equals(veResponses.get(i).getIdghe())) {
                    veResponses.remove(i);
                    httpSession.setAttribute("veresponse", veResponses);
                    return ResponseEntity.ok().body("true");
                }
            }
        }
        veResponses.add(veResponse);
        httpSession.setAttribute("veresponse", veResponses);
        return ResponseEntity.ok().body("true");
    }

    @PostMapping("/getSuatChieu/{idsuatchieu}")
    public ResponseEntity getSuatChieu(@PathVariable Integer idsuatchieu){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Suatchieu suatchieu =suatChieuService.findById(idsuatchieu);
        return ResponseEntity.ok().body(SuatChieuResponse.builder()
                .id(suatchieu.getId())
                .idphim(suatchieu.getPhim().getTen())
                .batdau(suatchieu.getKhunggio().getBatdau().format(dateTimeFormatter))
                .ketthuc(suatchieu.getKhunggio().getKetthuc().format(dateTimeFormatter))
                .dongia(Double.valueOf(suatchieu.getDongia()))
                .build());
    }


    @PostMapping("/getGheChoosen")
    public ResponseEntity getGheChoosen(HttpSession session){
        List<VeResponse> veResponsesCurent = new ArrayList<>();
        if(session.getAttribute("veresponse")!=null){
            veResponsesCurent =(List<VeResponse>) session.getAttribute("veresponse");
            veResponsesCurent.forEach(veResponse -> {
                Ghe ghe = gheService.findGheById(veResponse.getIdghe()).get();
                veResponse.setGheResponse(GheResponse.builder()
                        .id(ghe.getId())
                        .col(ghe.getCol())
                        .tenDay(ghe.getDayghe().getTen())
                        .build());
            });
        }
        return ResponseEntity.ok().body(veResponsesCurent);
    }

    @PostMapping("/getTotal")
    public ResponseEntity getTotal(HttpSession session,@RequestParam(value = "idsukien",required = false) String idsukien){
        float tongcong = 0;
        List<VeResponse> veResponsesCurent =(List<VeResponse>) session.getAttribute("veresponse");
        List<Ghe> ghes = new ArrayList<>();
        Suatchieu suatchieu = suatChieuService.findById(veResponsesCurent.get(0).getIdsuatchieu());
        for(VeResponse veResponse : veResponsesCurent){
            Ghe ghe = gheService.findGheById(veResponse.getIdghe()).get();
            tongcong += suatchieu.getDongia().floatValue()+ghe.getDayghe().getGia().floatValue()+ghe.getLoaighe().getGia().floatValue();
        }
        if(!idsukien.equals("noevent")){Sukien sukien = sukienService.findSukienById(idsukien).get();tongcong -= sukien.getGiam();}
        return ResponseEntity.ok().body(tongcong);
    }

    @PostMapping("/saveTicket")
    public ResponseEntity saveTicket(HttpSession session,@RequestParam(value = "idsukien",required = false)  String idsukien){
        List<VeResponse> veResponsesCurent =(List<VeResponse>) session.getAttribute("veresponse");
        Suatchieu suatchieu = suatChieuService.findById(veResponsesCurent.get(0).getIdsuatchieu());
        if(!idsukien.equals("noevent")){Sukien sukien = sukienService.findSukienById(idsukien).get();}
        for (VeResponse veResponse:veResponsesCurent) {
            Ghe ghe = gheService.findGheById(veResponse.getIdghe()).get();
        }
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/clearsession")
    public void clearSession(HttpSession session) {
        session.setAttribute("veresponse",new ArrayList<VeResponse>());
    }

    @GetMapping("/close")
    public void killSession(HttpSession session) {
        session.removeAttribute("veresponse");
    }
}
