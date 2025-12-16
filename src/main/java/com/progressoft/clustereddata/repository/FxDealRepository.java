package com.progressoft.clustereddata.repository;

import com.progressoft.clustereddata.entity.FxDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FxDealRepository extends JpaRepository<FxDeal, String> {

    boolean existsByDealUniqueId(String dealUniqueId);

    Optional<FxDeal> findByDealUniqueId(String dealUniqueId);
}
