package ru.workservice.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import ru.workservice.model.ExportTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class ExportToPdfService {

    public void saveTableToPdf(File file, List<ExportTableRow> exportTableRows) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, Files.newOutputStream(file.toPath()));

        document.open();

        PdfPTable table = new PdfPTable(17);
        exportTableRows.forEach(t -> addRowInTable(table, t));

//        addTableHeader(table);
//        addRows(table);
//        addCustomRows(table);

        document.add(table);
        document.close();
    }

    private void addRowInTable(PdfPTable table, ExportTableRow row) {
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6, BaseColor.BLACK);
        table.addCell(new PdfPCell(new Phrase(row.getProductOrContract(), font)));
        table.addCell(row.getProductPrice());
        table.addCell(row.getScheduledProductQuantity());
        table.addCell(row.getActualProductQuantity());
        table.addCell(row.getPeriod());
        table.addCell(row.getJanQuantity());
        table.addCell(row.getFebQuantity());
        table.addCell(row.getMarQuantity());
        table.addCell(row.getAprQuantity());
        table.addCell(row.getMayQuantity());
        table.addCell(row.getJunQuantity());
        table.addCell(row.getJulQuantity());
        table.addCell(row.getAugQuantity());
        table.addCell(row.getSepQuantity());
        table.addCell(row.getOctQuantity());
        table.addCell(row.getNovQuantity());
        table.addCell(row.getDecQuantity());
    }
}
