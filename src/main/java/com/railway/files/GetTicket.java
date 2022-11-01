package com.railway.files;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.FileNotFoundException;

public class GetTicket {
//    public static void main(String[] args) {
//        ticketPdf("1234", "65432", "Nodiraxon Shoraxmedova",
//                "23 oktabr", "Toshkent", "Samarqand",
//                " 5 Noyabr", "1676", "96654", 8, "120.000");
//    }

    public static void ticketPdf(String ticketId, String orderId, String customerName, String startTime,
                                 String startStationId, String endStationId, String endTime,
                                 String reysId, String vagon_id, int place,  String price) {

        try {
            PdfWriter writer = new PdfWriter("src/main/resources/"+customerName+".pdf");
            PdfDocument pdfDocument = new PdfDocument(writer);
            pdfDocument.addNewPage();
            Document document = new Document(pdfDocument);

            float twoCoal = 570f;
            float threeCoal = 190f;
            float twoCoal1 = 150f;
            float twoColumnWith[] = {twoCoal, twoCoal1};
            float fullwidth[] = {threeCoal * 3};
            Paragraph paragraph = new Paragraph("\n");

            Table table = new Table(twoColumnWith);


            table.addCell("O'zbekiston\n Temir Yo'llari").setFontSize(15f).
                    setBorder(Border.NO_BORDER).setBold().setTextAlignment(TextAlignment.CENTER);
            Table nastedTable = new Table(new float[]{twoCoal / 2, twoCoal / 2});
            nastedTable.addCell("Elektron chipta raqami ").setBold().setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            nastedTable.addCell(ticketId).setBorder(Border.NO_BORDER);
            nastedTable.addCell("Buyurtma raqami ").setBold().setBorder(Border.NO_BORDER);
            nastedTable.addCell(orderId).setBorder(Border.NO_BORDER);
            table.addCell(new Cell().add(nastedTable).setBorder(Border.NO_BORDER));
            table.addCell("Electron chipta").setBorder(Border.NO_BORDER) .setTextAlignment(TextAlignment.CENTER);

            float[] floats = {200f, 500f, 250f, 150f, 150f, 160f};
            Table table1 = new Table(floats);
            table.addCell(customerName).setBorder(Border.NO_BORDER) .setTextAlignment(TextAlignment.CENTER);
            table1.addCell("Ketish sana").setBold() .setTextAlignment(TextAlignment.CENTER);
            table1.addCell("Reys nomi ").setBold() .setTextAlignment(TextAlignment.CENTER);
            table1.addCell("Kelish sana").setBold() .setTextAlignment(TextAlignment.CENTER);
            table1.addCell("Xizmat koprsatish klassi").setBold().startNewRow() .setTextAlignment(TextAlignment.CENTER);

            table1.addCell(startTime);
            table1.addCell(startStationId + " ->  " + endStationId);
            table1.addCell(endTime);
            table1.addCell("Konditsionersiz");
            Table table2 = new Table(fullwidth);
            table2.addCell("  Poyezd -> " + "7" + "           Reys Id -> " + reysId + "          vagon Id -> " + vagon_id
                    + "           Joyi -> " + place).setBold().setFontSize(15f);

            Table table3 = new Table(fullwidth);

            Paragraph prices = new Paragraph("Umumiy summa").setBold();
            prices.setTextAlignment(TextAlignment.CENTER).setBold();

            Paragraph value = new Paragraph(price).setBold();
            value.setTextAlignment(TextAlignment.CENTER);

            table3.addCell(String.valueOf(price)).setBold();


            Border border = new SolidBorder(2f);
            Table devider = new Table(fullwidth);
            devider.setBorder(border);
            document.add(paragraph);
            document.add(paragraph);
            document.add(table);

            document.add(paragraph);
            document.add(devider);

            document.add(paragraph);

            document.add(table1);
            document.add(paragraph);

            document.add(table2);
            document.add(table3);
            document.add(prices);
            document.add(value);


            document.close();
            System.out.println("ishladi");


        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
