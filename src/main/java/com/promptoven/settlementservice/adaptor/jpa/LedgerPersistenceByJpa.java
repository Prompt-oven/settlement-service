package com.promptoven.settlementservice.adaptor.jpa;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.promptoven.settlementservice.adaptor.jpa.entity.SoldProductLedgerEntity;
import com.promptoven.settlementservice.adaptor.jpa.repository.SoldProductLedgerRepository;
import com.promptoven.settlementservice.application.port.out.call.LedgerPersistence;
import com.promptoven.settlementservice.application.service.dto.SoldProductLedgerDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class LedgerPersistenceByJpa implements LedgerPersistence {

	private final SoldProductLedgerRepository soldProductLedgerRepository;

	@Override
	public void record(SoldProductLedgerDTO soldProductLedgerDTO) {
		soldProductLedgerRepository.save(DtoEntityMapper.toEntity(soldProductLedgerDTO));
	}

	@Override
	public List<SoldProductLedgerDTO> get(Pair<LocalDate, LocalDate> range, String targetUUID) {
		LocalDate beginDate = range.getFirst();
		LocalDate endDate = range.getSecond();
		return soldProductLedgerRepository.findBySellerUUIDAndSoldAtIsBetween(targetUUID, beginDate, endDate).stream()
			.map(DtoEntityMapper::toDTO)
			.toList();
	}

	@Override
	public List<SoldProductLedgerDTO> getUnsettled(String targetUUID) {
		return soldProductLedgerRepository.findBySellerUUIDAndSettled(targetUUID, false).stream()
			.map(DtoEntityMapper::toDTO)
			.toList();
	}
}

class DtoEntityMapper {
	public static SoldProductLedgerDTO toDTO(SoldProductLedgerEntity soldProductLedgerEntity) {
		return SoldProductLedgerDTO.builder()
			.sellerUUID(soldProductLedgerEntity.getSellerUUID())
			.productName(soldProductLedgerEntity.getProductName())
			.price(soldProductLedgerEntity.getPrice())
			.soldAt(soldProductLedgerEntity.getSoldAt())
			.settled(soldProductLedgerEntity.isSettled())
			.build();
	}

	public static SoldProductLedgerEntity toEntity(SoldProductLedgerDTO soldProductLedgerDTO) {
		return SoldProductLedgerEntity.builder()
			.sellerUUID(soldProductLedgerDTO.getSellerUUID())
			.productName(soldProductLedgerDTO.getProductName())
			.price(soldProductLedgerDTO.getPrice())
			.soldAt(soldProductLedgerDTO.getSoldAt())
			.settled(soldProductLedgerDTO.isSettled())
			.build();
	}
}