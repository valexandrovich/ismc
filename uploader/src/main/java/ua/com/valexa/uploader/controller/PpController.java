package ua.com.valexa.uploader.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.com.valexa.afscommon.dto.uploader.UploaderPpRequestDto;
import ua.com.valexa.afscommon.scheduler.StoredJobRequestDto;
import ua.com.valexa.afscommon.scheduler.StoredStepRequestDto;
import ua.com.valexa.uploader.model.UploadPpFile;
import ua.com.valexa.uploader.repository.UploadPpFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pp")
public class PpController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${downloader.mount.point}")
    private String mountPoint;

    @Value("${queue.cpms.job.request}")
    private String cmpsJobRequestQueue;

    @Autowired
    UploadPpFileRepository uploadPpFileRepository;

    @GetMapping("/tst")
    public String tst(){
        return "Uploader test";
    }


    @GetMapping("/files")
    public List<UploadPpFile> getFiles(){
        return uploadPpFileRepository.findAll();
    }


    @DeleteMapping("/delete/{id}")
    public void deleteFile(
            @PathVariable("id") UUID id
    ) {
        try {
            UploadPpFile file = uploadPpFileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));

            Path filePath = Paths.get(file.getFilePath());
            Files.delete(filePath);

            // Optionally, delete the record from the database if the file is successfully deleted from the filesystem
            uploadPpFileRepository.deleteById(id);

        } catch (IOException e) {
            // Handle file deletion failure (file not found, access issues, etc.)
            throw new RuntimeException("Failed to delete the file: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            // Handle cases where the file is not found in the database
            throw new RuntimeException("Failed to find the file record: " + e.getMessage(), e);
        }


    }
    @PostMapping("/upload/{id}")
    public void uploadFile(
            @PathVariable("id") UUID id
    ){
        System.out.println(id);

        UploadPpFile file = uploadPpFileRepository.findById(id).get();
        file.setIsNew(false);
        file = uploadPpFileRepository.save(file);


        StoredJobRequestDto jobRequestDto = new StoredJobRequestDto();
        jobRequestDto.setShortName("upload_pp");
        jobRequestDto.setName("Завантаження файлу фізичних осіб");
        jobRequestDto.setSource("FILE");
        jobRequestDto.setInitiatorName(file.getAuthor());

        StoredStepRequestDto storedStepRequestDto = new StoredStepRequestDto();
        storedStepRequestDto.setStepOrder(1);
        storedStepRequestDto.setService("importer");
        storedStepRequestDto.setWorker("uploader_pp");
        storedStepRequestDto.setWorker("uploader_pp");
        storedStepRequestDto.setIsSkipable(false);
        storedStepRequestDto.getParameters().put("file", file.getFilePath());

        jobRequestDto.getSteps().add(storedStepRequestDto);

        rabbitTemplate.convertAndSend(cmpsJobRequestQueue , jobRequestDto);

    }


    @PostMapping("/upload")
    public void upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("author") String author,
            @RequestParam("rowsCount") Long rowsCount
            ){
        System.out.println("UPLOAD");

        if (file.isEmpty()) {
            System.out.println("Failed to store empty file.");
            return;
        }

        try {

            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + file.getOriginalFilename();
            saveFile(file, fileName);

            UploadPpFile uploadPpFile = new UploadPpFile();
            uploadPpFile.setId(uuid);
            uploadPpFile.setAuthor(author);
            uploadPpFile.setRowsCount(rowsCount);
            uploadPpFile.setIsNew(true);
            uploadPpFile.setCreateDate(LocalDateTime.now());
//            uploadPpFile.setFileName(file.getOriginalFilename());

            String originalFilename = file.getOriginalFilename();
            String newFilename = originalFilename;

            if (originalFilename != null && originalFilename.endsWith(".csv")) {
                newFilename = originalFilename.substring(0, originalFilename.length() - 4);
            }

            uploadPpFile.setFileName(newFilename);

            uploadPpFile.setFilePath(mountPoint + System.getProperty("file.separator") + fileName);
            uploadPpFileRepository.save(uploadPpFile);

            System.out.println("File uploaded successfully.");
        } catch (IOException e) {
            System.out.println("Failed to store file: " + e.getMessage());
        }


//        System.out.println(uploaderPpRequestDto);
    }


    private void saveFile(MultipartFile file, String fileName) throws IOException {
        // Ensure the upload directory exists
        Path uploadPath = Paths.get(mountPoint);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file to the specified directory
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
    }

}
