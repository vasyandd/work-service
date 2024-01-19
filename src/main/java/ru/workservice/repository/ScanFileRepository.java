package ru.workservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.workservice.model.entity.ScanFile;

import java.util.List;
import java.util.Set;

public interface ScanFileRepository extends JpaRepository<ScanFile, Long> {

    List<ScanFile> findAllByDeliveryStatementIdIn(Set<Long> ids);

    void deleteAllByDeliveryStatementId(Long id);
}
