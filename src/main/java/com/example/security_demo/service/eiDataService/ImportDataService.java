package com.example.security_demo.service.eiDataService;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class ImportDataService {
    private final IUserProfileRepository userProfileRepository;

    public String importUserFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();
        rows.next();
        StringBuilder errorReport = new StringBuilder();
        int rowIndex = 1;
        while (rows.hasNext()) {
            Row row = rows.next();
            rowIndex++;
            try{
            String stt = getCellValue(row.getCell(0));
            String username = getCellValue(row.getCell(1));
            String fullName = getCellValue(row.getCell(2));
            String birthDate = getCellValue(row.getCell(3));
            String street = getCellValue(row.getCell(4));
            String district = getCellValue(row.getCell(5));
            String province = getCellValue(row.getCell(6));
            String experience = getCellValue(row.getCell(7));
            validation(username, fullName, birthDate, experience);
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
            }catch (IllegalArgumentException e){
                errorReport.append(String.format("Row %d: %s\n", rowIndex, e.getMessage()));
            }
        }
        workbook.close();
        if(errorReport.length() > 0){
            return "Import error: \n" + errorReport.toString();
        }
        return "Import successfull ";
    }
    private void validation(String username, String fullName, String birthDate, String experience){
       StringBuilder errors = new StringBuilder();
       if(username.isEmpty()){
           errors.append("Username is empty ");
       }else if(isUsernameExists(username)){
           errors.append("Username already exist ");
       }

       if(fullName.isEmpty()){
           errors.append("Full name is empty ");
       }

       if(!isValidDate(birthDate)){
           errors.append("Birth date is invalid,expected format: yyyy-MM-dd ");
       }

       if(!isNumeric(experience)){
           errors.append("Experience must be a number ");
       }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString().trim());
        }
    }


    private boolean isUsernameExists(String username) {
        return userProfileRepository.findByUsername(username).isPresent();
    }
    private boolean isValidDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    private boolean isNumeric(String str) {
        try {
            double doubleValue = Double.parseDouble(str);
            int intValue = (int) doubleValue;
            return doubleValue == intValue;
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
