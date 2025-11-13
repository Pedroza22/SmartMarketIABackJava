package com.smartmarket.backend.web;

import com.smartmarket.backend.model.Analysis;
import com.smartmarket.backend.model.ScrapingResult;
import com.smartmarket.backend.repository.AnalysisRepository;
import com.smartmarket.backend.repository.ScrapingRepository;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports")
public class ReportsController {

    private final ScrapingRepository scrapingRepository;
    private final AnalysisRepository analysisRepository;

    public ReportsController(ScrapingRepository scrapingRepository, AnalysisRepository analysisRepository) {
        this.scrapingRepository = scrapingRepository;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping(value = "/scraping.csv", produces = "text/csv")
    @Operation(summary = "Exportar scraping en CSV", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<byte[]> scrapingCsv() {
        List<ScrapingResult> data = scrapingRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,user,platform,sourceUrl,createdAt\n");
        for (ScrapingResult s : data) {
            sb.append(s.getId()).append(',')
              .append(s.getUser() != null ? s.getUser().getUsername() : "")
              .append(',')
              .append(s.getPlatform() != null ? s.getPlatform() : "")
              .append(',')
              .append(s.getSourceUrl() != null ? s.getSourceUrl() : "")
              .append(',')
              .append(s.getCreatedAt()).append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scraping.csv")
                .contentType(MediaType.valueOf("text/csv"))
                .body(bytes);
    }

    @GetMapping(value = "/analyses.csv", produces = "text/csv")
    @Operation(summary = "Exportar analyses en CSV", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<byte[]> analysesCsv() {
        List<Analysis> data = analysisRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,user,product,status,durationMs,createdAt\n");
        for (Analysis a : data) {
            sb.append(a.getId()).append(',')
              .append(a.getUser() != null ? a.getUser().getUsername() : "")
              .append(',')
              .append(a.getProduct() != null ? a.getProduct().getName() : "")
              .append(',')
              .append(a.getStatus() != null ? a.getStatus() : "")
              .append(',')
              .append(a.getDurationMs() != null ? a.getDurationMs() : 0)
              .append(',')
              .append(a.getCreatedAt()).append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analyses.csv")
                .contentType(MediaType.valueOf("text/csv"))
                .body(bytes);
    }

    @GetMapping(value = "/scraping.xlsx")
    @Operation(summary = "Exportar scraping en XLSX", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<byte[]> scrapingXlsx() {
        List<ScrapingResult> data = scrapingRepository.findAll();
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet("scraping");
            Row header = sh.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("user");
            header.createCell(2).setCellValue("platform");
            header.createCell(3).setCellValue("sourceUrl");
            header.createCell(4).setCellValue("createdAt");
            int r = 1;
            for (ScrapingResult s : data) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getUser() != null ? s.getUser().getUsername() : "");
                row.createCell(2).setCellValue(s.getPlatform() != null ? s.getPlatform() : "");
                row.createCell(3).setCellValue(s.getSourceUrl() != null ? s.getSourceUrl() : "");
                row.createCell(4).setCellValue(s.getCreatedAt().toString());
            }
            wb.write(baos);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scraping.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/analyses.xlsx")
    @Operation(summary = "Exportar analyses en XLSX", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<byte[]> analysesXlsx() {
        List<Analysis> data = analysisRepository.findAll();
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet("analyses");
            Row header = sh.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("user");
            header.createCell(2).setCellValue("product");
            header.createCell(3).setCellValue("status");
            header.createCell(4).setCellValue("durationMs");
            header.createCell(5).setCellValue("createdAt");
            int r = 1;
            for (Analysis a : data) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(a.getUser() != null ? a.getUser().getUsername() : "");
                row.createCell(2).setCellValue(a.getProduct() != null ? a.getProduct().getName() : "");
                row.createCell(3).setCellValue(a.getStatus() != null ? a.getStatus() : "");
                row.createCell(4).setCellValue(a.getDurationMs() != null ? a.getDurationMs() : 0);
                row.createCell(5).setCellValue(a.getCreatedAt().toString());
            }
            wb.write(baos);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analyses.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/scraping.pdf")
    @Operation(summary = "Exportar scraping en PDF", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<byte[]> scrapingPdf() {
        List<ScrapingResult> data = scrapingRepository.findAll();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Scraping Results"));
            Table table = new Table(5);
            table.addCell("id");
            table.addCell("user");
            table.addCell("platform");
            table.addCell("sourceUrl");
            table.addCell("createdAt");
            for (ScrapingResult s : data) {
                table.addCell(String.valueOf(s.getId()));
                table.addCell(s.getUser() != null ? s.getUser().getUsername() : "");
                table.addCell(s.getPlatform() != null ? s.getPlatform() : "");
                table.addCell(s.getSourceUrl() != null ? s.getSourceUrl() : "");
                table.addCell(s.getCreatedAt().toString());
            }
            doc.add(table);
            doc.close();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scraping.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/analyses.pdf")
    @Operation(summary = "Exportar analyses en PDF", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<byte[]> analysesPdf() {
        List<Analysis> data = analysisRepository.findAll();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Analyses"));
            Table table = new Table(6);
            table.addCell("id");
            table.addCell("user");
            table.addCell("product");
            table.addCell("status");
            table.addCell("durationMs");
            table.addCell("createdAt");
            for (Analysis a : data) {
                table.addCell(String.valueOf(a.getId()));
                table.addCell(a.getUser() != null ? a.getUser().getUsername() : "");
                table.addCell(a.getProduct() != null ? a.getProduct().getName() : "");
                table.addCell(a.getStatus() != null ? a.getStatus() : "");
                table.addCell(String.valueOf(a.getDurationMs() != null ? a.getDurationMs() : 0));
                table.addCell(a.getCreatedAt().toString());
            }
            doc.add(table);
            doc.close();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analyses.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
