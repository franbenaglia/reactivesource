package com.fab.reactivesource.source.model;

import java.util.Date;

public record Climate(double temperature, double humidity, long channel, int idx, Date date) {

}
