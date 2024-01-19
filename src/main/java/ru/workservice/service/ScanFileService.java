package ru.workservice.service;


import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import ru.workservice.model.entity.ScanFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScanFileService {

    public List<ScanFile> createScanFiles(List<File> files) {
        return files.stream()
                .map(f -> {
                    ScanFile scanFile = new ScanFile();
                    try {
                        byte[] content = Files.readAllBytes(f.toPath());
                        scanFile.setContent(content);
                        scanFile.setName(FilenameUtils.getName(f.getName()));
                        scanFile.setExtension(FilenameUtils.getExtension(f.getName()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return scanFile;
                })
                .collect(Collectors.toList());
    }

    public void downloadFiles(File file, List<ScanFile> scanFiles) {
        for (ScanFile scanFile : scanFiles) {
            try {
                String path = file.getAbsolutePath() + "\\" + scanFile.getName();
                Files.write(Path.of(path), scanFile.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
