package com.finx.service;

import com.finx.dto.request.CryptoRequest;
import com.finx.exception.ResourceNotFoundException;
import com.finx.model.CryptoAsset;
import com.finx.model.User;
import com.finx.repository.CryptoAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoAssetRepository cryptoAssetRepository;

    @Transactional(readOnly = true)
    public List<CryptoAsset> findByUser(Long userId) {
        return cryptoAssetRepository.findByUserId(userId);
    }

    @Transactional
    public CryptoAsset create(Long userId, CryptoRequest req) {
        User user = new User();
        user.setId(userId);
        CryptoAsset asset = CryptoAsset.builder()
                .user(user)
                .symbol(req.getSymbol().toUpperCase())
                .name(req.getName())
                .amount(req.getAmount())
                .avgCost(req.getAvgCost())
                .build();
        CryptoAsset saved = cryptoAssetRepository.save(asset);
        log.info("Created crypto asset id={} for userId={}", saved.getId(), userId);
        return saved;
    }

    @Transactional
    public CryptoAsset update(Long userId, Long assetId, CryptoRequest req) {
        CryptoAsset asset = findOwnedAsset(userId, assetId);
        asset.setSymbol(req.getSymbol().toUpperCase());
        asset.setName(req.getName());
        asset.setAmount(req.getAmount());
        asset.setAvgCost(req.getAvgCost());
        return cryptoAssetRepository.save(asset);
    }

    @Transactional
    public void delete(Long userId, Long assetId) {
        CryptoAsset asset = findOwnedAsset(userId, assetId);
        cryptoAssetRepository.delete(asset);
        log.info("Deleted crypto asset id={} for userId={}", assetId, userId);
    }

    private CryptoAsset findOwnedAsset(Long userId, Long assetId) {
        CryptoAsset asset = cryptoAssetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("CryptoAsset", assetId));
        if (!asset.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("CryptoAsset", assetId);
        }
        return asset;
    }
}
