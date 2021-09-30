package com.migros.couriertracking.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourierInfoDTO {
	private Date time;
	private Long courierId;
	private Double lat;
	private Double lng;
}
