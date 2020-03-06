package pokal.pdf;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import pokal.model.Mitglied;
import pokal.model.Zug;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class PDFGenerator {
    final private static PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    final private static PDFont font = PDType1Font.HELVETICA;
    final private static float FONT_SIZE = 16;
    final private static float LINE_OFFSET = FONT_SIZE * 1.4f;

    private PDFGenerator() {}

    public static void generateSingleZugPDF(final File file, final Zug zug) throws IOException {
        final PDDocument doc = new PDDocument();

        final PDPage page = new PDPage();
        final PDPageContentStream stream = new PDPageContentStream(doc, page);
        final PDRectangle mediabox = page.getMediaBox();

        printZug(stream, zug, mediabox.getLowerLeftX() + 72, mediabox.getUpperRightY() - 72, mediabox.getWidth() - 72);

        stream.close();
        doc.addPage(page);

        doc.save(file);
        doc.close();
    }

    public static void generatePDF(final File file, final List<Zug> list, final Mitglied best, final Mitglied bestYoungling) throws IOException {
        final PDDocument doc = new PDDocument();

        printDeckblatt(doc, list.get(0), best, bestYoungling);

        PDPage page = new PDPage();
        PDPageContentStream stream = new PDPageContentStream(doc, page);
        PDRectangle mediabox = page.getMediaBox();
        float yOffset = mediabox.getUpperRightY() - 72;

        int pos = 0;
        int lastScore = -1;
        int lastZehner = -1;
        for (Zug z : list) {
            if (!isEnoughSpaceLeft(z, yOffset, 72)) {
                System.out.println("not enough space @" + z.getName());
                stream.close();
                doc.addPage(page);

                page = new PDPage();
                stream = new PDPageContentStream(doc, page);
                mediabox = page.getMediaBox();
                yOffset = mediabox.getUpperRightY() - 72;
            }
            float margin = yOffset == mediabox.getUpperRightY() - 72 ? 0 : LINE_OFFSET * 2;
            if(z.getScore() != lastScore || z.getZehner() != lastZehner) {
                pos++;
            }
            yOffset = printZug(stream, z, pos, shouldAddNotice(z, list) && z.getZehner() != 0,mediabox.getLowerLeftX() + 72, yOffset - margin, mediabox.getWidth() - 72);
            lastScore = z.getScore();
            lastZehner = z.getZehner();
        }

        stream.close();
        doc.addPage(page);
        doc.save(file);
        doc.close();
    }

    private static boolean shouldAddNotice(final Zug zug, final List<Zug> list) {
        final int index = list.indexOf(zug);
        final boolean sameAsPrevious = index != 0 && zug.getScore() == list.get(index - 1).getScore();
        final boolean sameAsNext = list.size() >= index + 2 && zug.getScore() == list.get(index + 1).getScore();
        return sameAsPrevious || sameAsNext;
    }

    private static void printDeckblatt(final PDDocument document, final Zug winningZug, final Mitglied winningMitglied, final Mitglied winningYoungling) throws IOException {
        final PDPage page = new PDPage();
        final PDPageContentStream stream = new PDPageContentStream(document, page);
        final PDRectangle mediabox = page.getMediaBox();

        final String headline = "BSV-Pokalschuss " + Calendar.getInstance().get(Calendar.YEAR);
        final String winningZugLine = winningZug.getName() + " - " + winningZug.getScore() + " Ringe";
        final String winningMitgliedLine = winningMitglied.getName() + " - " + winningMitglied.getShot() + " Ringe";
        final String winningYounglingLine = winningYoungling == null ? "" : winningYoungling.getName() + " - " + winningYoungling.getShot() + " Ringe";

        final PDImageXObject logo = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(PDFGenerator.class.getResourceAsStream("/assets/logo.png")), "Logo");
        stream.drawImage(logo, (mediabox.getWidth() - 162) / 2, mediabox.getHeight() - 212 - 50, 162, 212);

        float yOffset = mediabox.getHeight() - 362;

        stream.beginText();
        stream.newLineAtOffset((mediabox.getWidth() - (fontBold.getStringWidth(headline) / 1000 * 32)) / 2, yOffset);
        stream.setFont(fontBold, 32);
        stream.showText(headline);
        stream.endText();

        yOffset -= 133;
        stream.beginText();
        stream.newLineAtOffset((mediabox.getWidth() - ((fontBold.getStringWidth("Bester Zug: ") + font.getStringWidth(winningZugLine)) / 1000 * 18)) / 2, yOffset);
        stream.setFont(fontBold, 18);
        stream.showText("Bester Zug: ");
        stream.setFont(font, 18);
        stream.showText(winningZugLine);
        stream.endText();

        yOffset -= 33;
        stream.beginText();
        stream.newLineAtOffset((mediabox.getWidth() - ((fontBold.getStringWidth("Bester Schütze: ") + font.getStringWidth(winningMitgliedLine)) / 1000 * 18)) / 2, yOffset);
        stream.setFont(fontBold, 18);
        stream.showText("Bester Schütze: ");
        stream.setFont(font, 18);
        stream.showText(winningMitgliedLine);
        stream.endText();

        yOffset -= 33;
        stream.beginText();
        stream.newLineAtOffset((mediabox.getWidth() - ((fontBold.getStringWidth("Bester Jungschütze: ") + font.getStringWidth(winningYounglingLine)) / 1000 * 18)) / 2, yOffset);
        stream.setFont(fontBold, 18);
        stream.showText("Bester Jungschütze: ");
        stream.setFont(font, 18);
        stream.showText(winningYounglingLine);
        stream.endText();

        stream.close();
        document.addPage(page);
    }

    private static void printZug(final PDPageContentStream stream, final Zug zug, final float startX, final float startY, final float endX) throws IOException {
        printZug(stream, zug, -1, false, startX, startY, endX);
    }

    private static float printZug(final PDPageContentStream stream, final Zug zug, final int pos, final boolean addNotice, final float startX, final float startY, final float endX) throws IOException {
        float yOffset = startY;

        stream.beginText();
        stream.newLineAtOffset(startX, yOffset);
        stream.setFont(fontBold, FONT_SIZE);
        if (pos > 0) {
            stream.showText(pos + ". " + (addNotice ? "(durch Schussbild) " : ""));
        }
        stream.showText(zug.getName());
        stream.endText();

        stream.beginText();
        stream.newLineAtOffset(endX - font.getStringWidth(String.valueOf(zug.getScore())) / 1000 * FONT_SIZE, yOffset );
        stream.showText(String.valueOf(zug.getScore()));
        stream.endText();

        stream.moveTo(startX, yOffset - 5);
        stream.lineTo(endX, yOffset - 5);
        stream.stroke();

        final List<Mitglied> sorted = zug.getSortedMitglieder();
        for (Mitglied m : sorted) {
            if (m.getShot() != -1) {
                yOffset -= LINE_OFFSET;
                stream.beginText();
                stream.newLineAtOffset(startX, yOffset);
                stream.setFont(font, FONT_SIZE);
                stream.showText(m.getName());
                stream.endText();

                stream.beginText();
                stream.setFont(sorted.indexOf(m) > 4 ? font : fontBold, FONT_SIZE);
                stream.newLineAtOffset(endX - (sorted.indexOf(m) > 4 ? font.getStringWidth("100" + m.getShot()) / 1000 * FONT_SIZE : font.getStringWidth(m.getShot().toString()) / 1000 * FONT_SIZE), yOffset );
                stream.showText(m.getShot().toString());
                stream.endText();
            }
        }

        return yOffset;
    }

    private static boolean isEnoughSpaceLeft(final Zug zug, final float startY, final float endY) {
        int printableCount = 0;
        for (Mitglied m : zug.getMitglieder()) {
            if (m.getShot() != -1) printableCount++;
        }

        return startY - (LINE_OFFSET * (printableCount + 1)) > endY;
    }
}
