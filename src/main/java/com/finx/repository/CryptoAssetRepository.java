package com.finx.repository;

import com.finx.model.CryptoAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CryptoAssetRepository extends JpaRepository<CryptoAsset, Long> {
    List<CryptoAsset> findByUserId(Long userId);
}
