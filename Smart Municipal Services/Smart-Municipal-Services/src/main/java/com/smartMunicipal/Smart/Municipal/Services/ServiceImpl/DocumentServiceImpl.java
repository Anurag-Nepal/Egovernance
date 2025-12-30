package com.smartMunicipal.Smart.Municipal.Services.ServiceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartMunicipal.Smart.Municipal.Services.Entity.Application;
import com.smartMunicipal.Smart.Municipal.Services.Entity.Document;
import com.smartMunicipal.Smart.Municipal.Services.Entity.User;
import com.smartMunicipal.Smart.Municipal.Services.Enums.ApplicationStatus;
import com.smartMunicipal.Smart.Municipal.Services.Enums.DocumentCategory;
import com.smartMunicipal.Smart.Municipal.Services.Payload.DocumentDTO;
import com.smartMunicipal.Smart.Municipal.Services.Payload.DocumentRequest;
import com.smartMunicipal.Smart.Municipal.Services.Repository.ApplicationRepository;
import com.smartMunicipal.Smart.Municipal.Services.Repository.DocumentRepository;
import com.smartMunicipal.Smart.Municipal.Services.Repository.UserRepository;
import com.smartMunicipal.Smart.Municipal.Services.Service.DocumentService;
import com.smartMunicipal.Smart.Municipal.Services.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    @Value("${app.verification-url-prefix}")
    private String verificationUrlPrefix;


    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ApplicationRepository applicationRepository;


    @Override
    public String requestForDocument(DocumentRequest request) {

        // 1. Validate User
        Optional<User> userOpt = userRepository.findById(Integer.valueOf(request.getIssuedByUserId()));
        if (userOpt.isEmpty()) return "User not found with ID: " + request.getIssuedByUserId();
        User user = userOpt.get();

        // 2. Create Application
        Application application = new Application();
        application.setUser(user);
        application.setCategory(DocumentCategory.valueOf(request.getDocumentCategory()));
        application.setStatus(ApplicationStatus.UNDER_REVIEW);
        application.setSubmittedAt(LocalDateTime.now());
        application.setDocumentTitle(request.getTitle());
        applicationRepository.save(application);

        return "Document Request Initiated. You would be replied within few hours with the document requested.";
    }

    @Override
    public Boolean checkDocumentAuthenticity(Integer documentId, String hash) {
        // Validate input parameters
        if (documentId == null || hash == null || hash.trim().isEmpty()) {
            return false;
        }
        
        // Fetch document from database
        Optional<Document> docOpt = documentRepository.findById(documentId);
        
        // Verify hash matches using timing-safe comparison
        return docOpt
                .map(document -> {
                    String storedHash = document.getDocumentHash();
                    // Null-safe comparison
                    if (storedHash == null) {
                        return false;
                    }
                    // Use MessageDigest.isEqual for timing-safe comparison (prevents timing attacks)
                    return MessageDigest.isEqual(
                            storedHash.getBytes(StandardCharsets.UTF_8),
                            hash.trim().getBytes(StandardCharsets.UTF_8)
                    );
                })
                .orElse(false);
    }

    @Override
    public String verifyApplicationAndIssueDocument(Integer applicationId) {

        // 1. Fetch application
        Optional<Application> appOpt = applicationRepository.findById(applicationId);
        if (appOpt.isEmpty()) return "Application not found";

        Application app = appOpt.get();
        User user = app.getUser();

        // 2. Create Document from Application
        Document document = new Document();
        document.setTitle(app.getDocumentTitle());
        document.setIssuedTo(user);
        document.setCategory(app.getCategory());
        document.setIssuedAt(LocalDateTime.now());

        // 3. Generate Hash
        String rawData = document.getTitle() + user.getId() + document.getCategory() + document.getIssuedAt();
        String hash = generateDocumentHash(rawData);
        document.setDocumentHash(hash);

        app.setStatus(ApplicationStatus.VERIFIED_AND_APPROVED);
        applicationRepository.save(app);
        // 4. Save Document
        Document saved = documentRepository.save(document);

        // 5. Generate PDF with QR
        byte[] pdfBytes = generatePdfWithQr(saved);

        // 6. Send Email
        emailService.sendEmailWithAttachment(
                user.getEmail(),
                "Your Official Document: " + saved.getTitle(),
                "Please find attached your official document with a QR code for authenticity verification.",
                pdfBytes,
                "Document-" + saved.getId() + ".pdf"
        );

        return "Document successfully issued and emailed.";
    }

    private String generateDocumentHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hash generation failed", e);
        }
    }

    private Image generateQrCode(String content) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, 150, 150);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);

            return Image.getInstance(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }
    }

    private byte[] generatePdfWithQr(Document document) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Use com.itextpdf.text.Document (not PdfDocument)
            com.itextpdf.text.Document pdf = new com.itextpdf.text.Document(PageSize.A4);
            PdfWriter.getInstance(pdf, out);
            pdf.open();

            // Title Font
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

            // Header
            Paragraph header = new Paragraph("SMART MUNICIPAL SERVICES", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            pdf.add(header);

            Paragraph subHeader = new Paragraph("Official Document Certificate", headerFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            pdf.add(subHeader);

            pdf.add(new Paragraph("\n"));
            pdf.add(new Paragraph("─".repeat(60), normalFont));
            pdf.add(new Paragraph("\n"));

            // Document Info
            pdf.add(new Paragraph("Document Details:", headerFont));
            pdf.add(new Paragraph("\n"));
            pdf.add(new Paragraph("Document Title: " + document.getTitle(), normalFont));
            pdf.add(new Paragraph("Document ID: " + document.getId(), normalFont));
            pdf.add(new Paragraph("Issued To: " + document.getIssuedTo().getFullName(), normalFont));
            pdf.add(new Paragraph("Email: " + document.getIssuedTo().getEmail(), normalFont));
            pdf.add(new Paragraph("Category: " + document.getCategory().name(), normalFont));
            pdf.add(new Paragraph("Issued At: " + document.getIssuedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), normalFont));

            pdf.add(new Paragraph("\n"));
            pdf.add(new Paragraph("─".repeat(60), normalFont));
            pdf.add(new Paragraph("\n"));

            // QR Code Section
            pdf.add(new Paragraph("Document Verification", headerFont));
            pdf.add(new Paragraph("Scan the QR code below to verify the authenticity of this document:", normalFont));
            pdf.add(new Paragraph("\n"));

            // QR Code with verification URL using path variables (better for QR scanning)
            String verificationUrl = verificationUrlPrefix
                    + "/" + document.getId()
                    + "/" + document.getDocumentHash();

            Image qr = generateQrCode(verificationUrl);
            qr.setAlignment(Element.ALIGN_CENTER);
            qr.scaleToFit(150, 150);
            pdf.add(qr);

            pdf.add(new Paragraph("\n"));
            pdf.add(new Paragraph("Verification URL:", smallFont));
            Paragraph urlPara = new Paragraph(verificationUrl, smallFont);
            urlPara.setAlignment(Element.ALIGN_CENTER);
            pdf.add(urlPara);

            pdf.add(new Paragraph("\n\n"));
            pdf.add(new Paragraph("─".repeat(60), normalFont));

            // Footer
            Paragraph footer = new Paragraph(
                    "This is an electronically generated document. " +
                    "Verify authenticity by scanning the QR code above or visiting the verification URL.",
                    smallFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            pdf.add(footer);

            pdf.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DocumentDTO> findAll() {
        return documentRepository.findAll()
                .stream()
                .map(DocumentDTO::fromEntity)
                .toList();
    }
}
