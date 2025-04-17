package com.filemanager.service.impl;

import com.deepl.api.DeepLException;
import com.filemanager.service.TranslateService;
import com.filemanager.service.WordFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spire.doc.*;
import com.spire.doc.collections.DocumentObjectCollection;
import com.spire.doc.fields.ShapeObject;
import com.spire.doc.fields.TextRange;
import com.spire.doc.formatting.CharacterFormat;
import com.spire.doc.formatting.ParagraphFormat;
import com.spire.doc.interfaces.IParagraph;
import com.spire.doc.interfaces.IPicture;

import java.util.ArrayList;

@Service
public class WordFileServiceImpl implements WordFileService {

    private static final Logger logger = LoggerFactory.getLogger(WordFileServiceImpl.class);
    @Autowired
    TranslateService translateService;
    @Override
    public String translateWordFile(String filelocation, String sourceLang, String targetLang) throws DeepLException, InterruptedException {
        Document document = new Document();
        document.loadFromFile(filelocation);
        ArrayList<String> filecontent = new ArrayList<>();
        // Iterate through sections
        for (Object sectionObj : document.getSections()) {
            Section section = (Section) sectionObj;
            System.out.println("\n====== Section Details ======");
            System.out.println("\n-- Section Properties --");
            System.out.println("Page Setup:");
            PageSetup pageSetup = section.getPageSetup();
            System.out.println("  Orientation: " + pageSetup.getOrientation());
            System.out.println("  Page Width: " + pageSetup.getClientWidth());
            /*System.out.println("  Page Height: " + pageSetup.getPageHeight());
            System.out.println("  Top Margin: " + pageSetup.getTopMargin());
            System.out.println("  Bottom Margin: " + pageSetup.getBottomMargin());
            System.out.println("  Left Margin: " + pageSetup.getLeftMargin());
            System.out.println("  Right Margin: " + pageSetup.getRightMargin());*/
            System.out.println("  Header Distance: " + pageSetup.getHeaderDistance());
            System.out.println("  Footer Distance: " + pageSetup.getFooterDistance());

            // Headers and Footers
            System.out.println("\n-- Headers --");
            System.out.println("First Page Header: " + (section.getHeadersFooters().getFirstPageHeader() != null));
            System.out.println("Odd Header: " + (section.getHeadersFooters().getOddHeader() != null));
            System.out.println("Even Header: " + (section.getHeadersFooters().getEvenHeader() != null));

            System.out.println("\n-- Footers --");
            System.out.println("First Page Footer: " + (section.getHeadersFooters().getFirstPageFooter() != null));
            System.out.println("Odd Footer: " + (section.getHeadersFooters().getOddFooter() != null));
            System.out.println("Even Footer: " + (section.getHeadersFooters().getEvenFooter() != null));
            // Paragraphs in section
            System.out.println("\n-- Paragraphs in Section --");

            for (Object section_obj : section.getChildObjects())
            {
                System.out.println(section_obj.getClass());
                if(section_obj instanceof Body){
                    Body body = (Body) section_obj;
                    DocumentObjectCollection child_objs = body.getChildObjects();
                    for(Object child_obj : child_objs){
                        System.out.println(child_obj.getClass());
                        if(child_obj instanceof IParagraph){
                            IParagraph para = (IParagraph) child_obj;
                            System.out.println("\n--- NEW PARAGRAPH ---");
                            System.out.println("Text: " + para.getText());
                            filecontent.add(para.getText());
                            try {
                                String translated = translateService.translateText(para.getText(), sourceLang, targetLang);
                                System.out.println("Translated Text: " + translated);
                                para.setText(translated);
                            } catch (DeepLException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            System.out.println("Style Name: " + para.getStyleName());

                            ParagraphFormat para_format = para.getFormat();
                            System.out.println("\n-- Format Properties --");
                            System.out.println("Background Color: " + para_format.getBackColor());
                            System.out.println("Horizontal Alignment: " + para_format.getHorizontalAlignment());
                            System.out.println("Line Spacing Rule: " + para_format.getLineSpacingRule());
                            System.out.println("Line Spacing: " + para_format.getLineSpacing());
                            System.out.println("First Line Indent: " + para_format.getFirstLineIndent());
                            System.out.println("Left Indent: " + para_format.getLeftIndent());
                            System.out.println("Right Indent: " + para_format.getRightIndent());
                            System.out.println("Before Spacing: " + para_format.getBeforeSpacing());
                            System.out.println("After Spacing: " + para_format.getAfterSpacing());
                            System.out.println("No Space Between Paragraphs: " + para_format.getAutoSpaceDE());
                            System.out.println("Widow/Orphan Control: " + para_format.getTextAlignment());
                            System.out.println("Keep Lines: " + para_format.getKeepLines());
                            System.out.println("Keep Follow: " + para_format.getKeepFollow());
                            System.out.println("--------------------");
                            // Iterate through elements in paragraph
                            for (Object obj : para.getChildObjects()) {
                                System.out.println("\nElement Type: " + obj.getClass().getSimpleName());
                                if (obj instanceof TextRange) {
                                    TextRange text = (TextRange) obj;
                                    System.out.println("Text: " + text.getText());
                                    CharacterFormat textrange_format = text.getCharacterFormat();
                                    System.out.println("Font Name: " + textrange_format.getFontName());
                                    System.out.println("Font Size: " + textrange_format.getFontSize());
                                    System.out.println("Bold: " + textrange_format.getBold());
                                    System.out.println("Italic: " + textrange_format.getItalic());
                                    System.out.println("Underline: " + textrange_format.getUnderlineStyle());
                                    System.out.println("Text Color: " + textrange_format.getTextColor());
                                } else if (obj instanceof ShapeObject) {
                                    ShapeObject shape = (ShapeObject) obj;
                                    System.out.println("Shape Type: " + shape.getShapeType());
                                    System.out.println("Width: " + shape.getWidth());
                                    System.out.println("Height: " + shape.getHeight());
                                } else if (obj instanceof TableCell) {
                                    TableCell cell = (TableCell) obj;
                                    System.out.println("Cell Text: " + cell.getCellFormat());
                                } else if (obj instanceof IPicture) {
                                    IPicture pic = (IPicture) obj;
                                    System.out.println("Picture Width: " + pic.getWidth());
                                    System.out.println("Picture Height: " + pic.getHeight());
                                }
                            }
                        }

                    }
                }
            }
        }
        document.saveToFile("files/output.docx", FileFormat.Docx);
        // Close and release the original document object
        document.close();
        document.dispose();

        return filelocation;
    }


}