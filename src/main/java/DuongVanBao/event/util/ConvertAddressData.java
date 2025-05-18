package DuongVanBao.event.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ConvertAddressData {

    public static void main(String[] args) {
        System.out.println("Reading data files...");

        ObjectMapper mapper = new ObjectMapper();
        String resourcePath = "";

        try (
                BufferedWriter writer = new BufferedWriter(new FileWriter("addresses.sql"))
        ) {
            InputStream tinhStream = ConvertAddressData.class.getClassLoader()
    .getResourceAsStream("data/tinh_tp.json");

InputStream quanStream = ConvertAddressData.class.getClassLoader()
    .getResourceAsStream("data/quan_huyen.json");

InputStream xaStream = ConvertAddressData.class.getClassLoader()
    .getResourceAsStream("data/xa_phuong.json");

    Map<String, Map<String, Object>> provinces = mapper.readValue(
    new InputStreamReader(tinhStream, StandardCharsets.UTF_8),
    new TypeReference<>() {}
);

Map<String, Map<String, Object>> districts = mapper.readValue(
    new InputStreamReader(quanStream, StandardCharsets.UTF_8),
    new TypeReference<>() {}
);

Map<String, Map<String, Object>> wards = mapper.readValue(
    new InputStreamReader(xaStream, StandardCharsets.UTF_8),
    new TypeReference<>() {}
);


            // Lưu ID tỉnh -> UUID
            Map<String, String> provinceIds = new HashMap<>();
            Map<String, String> districtIds = new HashMap<>();

            // ---------- Ghi tỉnh/thành phố ----------
            for (Map.Entry<String, Map<String, Object>> entry : provinces.entrySet()) {
                String code = entry.getKey();
                String name = (String) entry.getValue().get("name");

                String id = UUID.randomUUID().toString();
                provinceIds.put(code, id);

                writer.write(String.format(
                        "INSERT INTO TINHTHANH (maTinhThanh, tenTinhThanh, ngayTao) VALUES ('%s', '%s', NOW());\n",
                        id, name.replace("'", "''")
                ));
            }

            // ---------- Ghi quận/huyện ----------
            for (Map.Entry<String, Map<String, Object>> entry : districts.entrySet()) {
                String code = entry.getKey();
                Map<String, Object> data = entry.getValue();

                String name = (String) data.get("name");
                String parentCode = (String) data.get("parent_code");
                String provinceId = provinceIds.get(parentCode);

                if (provinceId == null) continue; // bỏ nếu thiếu liên kết tỉnh

                String id = UUID.randomUUID().toString();
                districtIds.put(code, id);

                writer.write(String.format(
                        "INSERT INTO QUANHUYEN (maQuanHuyen, tenQuanHuyen, maTinhThanh, ngayTao) " +
                                "VALUES ('%s', '%s', '%s', NOW());\n",
                        id, name.replace("'", "''"), provinceId
                ));
            }

            // ---------- Ghi xã/phường ----------
            for (Map.Entry<String, Map<String, Object>> entry : wards.entrySet()) {
                String code = entry.getKey();
                Map<String, Object> data = entry.getValue();

                String name = (String) data.get("name");
                String parentCode = (String) data.get("parent_code");
                String districtId = districtIds.get(parentCode);

                if (districtId == null) continue;

                String id = UUID.randomUUID().toString();

                writer.write(String.format(
                        "INSERT INTO PHUONGXA (maPhuongXa, tenPhuongXa, maQuanHuyen, ngayTao) " +
                                "VALUES ('%s', '%s', '%s', NOW());\n",
                        id, name.replace("'", "''"), districtId
                ));
            }

            System.out.println("✅ Xuất file addresses.sql thành công!");

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
