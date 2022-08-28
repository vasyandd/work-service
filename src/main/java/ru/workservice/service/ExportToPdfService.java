package ru.workservice.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import ru.workservice.WorkServiceFXApplication;
import ru.workservice.model.ExportTableRow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


@Service
public class ExportToPdfService {

    private final static String FONT = WorkServiceFXApplication.class.getResource("fonts/arial.ttf").toExternalForm();

    private final static List<String> titles = new ArrayList<>() {{
        add("Контракт/Изделие");
        add("Стоимость");
        add("План (всего)");
        add("Факт (всего)");
        add("Период");
        add("Янв");
        add("Фев");
        add("Мар");
        add("Апр");
        add("Май");
        add("Июнь");
        add("Июль");
        add("Авг");
        add("Сен");
        add("Окт");
        add("Нояб");
        add("Дек");
        add("Извещения");
    }};

    private final static float[] widths = new float[] {13,10,5,5,5,4,4,4,4,4,4,4,4,4,4,4,4,14};

    public void saveTableToPdf(File file, List<ExportTableRow> exportTableRows) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate(), 10,10,10,10);
        PdfWriter.getInstance(document, Files.newOutputStream(file.toPath()));

        BaseFont bf = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 8);

        document.open();
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        addTableHeader(table, font);
        for (ExportTableRow row : exportTableRows) {
            addRowInTable(table, row, font);
        }


//        addRows(table);
//        addCustomRows(table);

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table, Font font) {
        titles
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRowInTable(PdfPTable table, ExportTableRow row, Font font) {
        table.addCell(new PdfPCell(new Phrase(row.getProductOrContract(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getProductPrice(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getScheduledProductQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getActualProductQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getPeriod(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getJanQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getFebQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getMarQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getAprQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getMayQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getJunQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getJulQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getAugQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getSepQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getOctQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getNovQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getDecQuantity(), font)));
        table.addCell(new PdfPCell(new Phrase(row.getNote(), font)));
    }
}
