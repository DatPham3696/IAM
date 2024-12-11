package com.example.security_demo.service;

import com.example.security_demo.entity.UserProfile;
import com.example.security_demo.repository.IUserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ImportDataService {
    private final IUserProfileRepository userProfileRepository;

    public String importUserFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        rows.next();
        while (rows.hasNext()) {
            Row row = rows.next();
            String stt = getCellValue(row.getCell(0));
            String username = getCellValue(row.getCell(1));
            String fullName = getCellValue(row.getCell(2));
            String birthDate = getCellValue(row.getCell(3));
            String street = getCellValue(row.getCell(4));
            String district = getCellValue(row.getCell(5));
            String province = getCellValue(row.getCell(6));
            String experience = getCellValue(row.getCell(7));
            validation(username, fullName, birthDate, experience);
            userProfileRepository.save(UserProfile.builder()
                    .username(username)
                    .fullName(fullName)
                    .birthDate(birthDate)
                    .street(street)
                    .district(district)
                    .province(province)
                    .experience(experience)
                    .build());
        }
        return "import succesful";
    }
    private void validation(String username, String fullName, String birthDate, String experience){
        if (username.isEmpty() || fullName.isEmpty() || isUsernameExists(username) || !isValidDate(birthDate) || !isNumeric(experience))
        {
            throw new IllegalArgumentException("argument invalid");
        }
    }
    private boolean isUsernameExists(String username) {
        return userProfileRepository.findByUsername(username).isPresent();
    }

    private boolean isValidDate(String date) {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
