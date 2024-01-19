package ru.workservice.service;


import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.workservice.model.entity.DeliveryStatement;
import ru.workservice.model.entity.ScanFile;
import ru.workservice.repository.ScanFileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScanFileService {
    private final ScanFileRepository repository;

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

    public List<ScanFile> findAllByDeliveryStatementId(Set<Long> ids) {
        return repository.findAllByDeliveryStatementIdIn(ids);
    }

    @Transactional
    public List<ScanFile> saveAllByDeliveryStatement(List<ScanFile> scanFiles, DeliveryStatement deliveryStatement) {
        repository.deleteAllByDeliveryStatementId(deliveryStatement.getId());
        scanFiles.forEach(f -> f.setDeliveryStatement(deliveryStatement));
        return repository.saveAll(scanFiles);
    }

    public void deleteByDeliverStatementId(Long id) {
        repository.deleteAllByDeliveryStatementId(id);
    }
}
