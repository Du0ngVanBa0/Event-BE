package DuongVanBao.event.util;

import DuongVanBao.event.model.entity.KhuVucMau;
import DuongVanBao.event.repository.KhuVucMauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class KhuVucMauDataLoader implements CommandLineRunner {

    @Autowired
    private KhuVucMauRepository khuVucMauRepository;

    @Override
    public void run(String... args) throws Exception {
        if (khuVucMauRepository.count() == 0) {
            createDefaultTemplates();
        }
    }



    private void createDefaultTemplates() {
        try {
            List<KhuVucMau> templates = Arrays.asList(
                    createTemplate("Sân khấu chính", "Khu vực ngay trước sân khấu",
                            "#FF6B6B", "RECTANGLE", 1, 300, 80, 200, 120),

                    createTemplate("Khu VIP trái", "Khu vực VIP bên trái",
                            "#4ECDC4", "RECTANGLE", 2, 80, 250, 120, 100),

                    createTemplate("Khu VIP phải", "Khu vực VIP bên phải",
                            "#45B7D1", "RECTANGLE", 3, 600, 250, 120, 100),

                    createTemplate("Khu thường A", "Khu vực thường phía sau",
                            "#96CEB4", "RECTANGLE", 4, 150, 400, 250, 120),

                    createTemplate("Khu thường B", "Khu vực thường xa nhất",
                            "#FFEAA7", "RECTANGLE", 5, 450, 400, 250, 120),

                    createTemplate("Sân khấu phụ", "Sân khấu nhỏ bên cạnh",
                            "#DDA0DD", "CIRCLE", 6, 700, 150, 120, 120)
            );

            for (KhuVucMau template : templates) {
                try {
                    KhuVucMau saved = khuVucMauRepository.save(template);
                    System.out.println("Created template: " + saved.getMaKhuVucMau() + " - " + saved.getTenKhuVuc());
                } catch (Exception e) {
                    System.err.println("Error creating template " + template.getTenKhuVuc() + ": " + e.getMessage());
                }
            }

            System.out.println("Finished creating " + templates.size() + " default KhuVucMau templates");
        } catch (Exception e) {
            System.err.println("Error in createDefaultTemplates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private KhuVucMau createTemplate(String tenKhuVuc, String moTa,
                                     String mauSac, String hinhDang, Integer thuTuHienThi,
                                     Integer toaDoX, Integer toaDoY, Integer chieuRong, Integer chieuCao) {
        KhuVucMau template = new KhuVucMau();
        template.setTenKhuVuc(tenKhuVuc);
        template.setMoTa(moTa);
        template.setMauSac(mauSac);
        template.setHinhDang(hinhDang);
        template.setThuTuHienThi(thuTuHienThi);
        template.setHoatDong(true);
        template.setToaDoXMacDinh(toaDoX);
        template.setToaDoYMacDinh(toaDoY);
        template.setChieuRongMacDinh(chieuRong);
        template.setChieuCaoMacDinh(chieuCao);
        template.setNgayTao(LocalDateTime.now());
        return template;
    }
}