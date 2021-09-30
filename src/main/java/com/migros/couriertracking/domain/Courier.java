package com.migros.couriertracking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.awt.geom.Point2D;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class Courier {
	private Long id;
	private Date entranceDate;
	private Point2D.Double lastPoint;
	private double totalTravel;
}
