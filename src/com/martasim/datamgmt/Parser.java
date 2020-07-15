package com.martasim.datamgmt;

import com.martasim.models.DayOfTheWeek;

import java.util.zip.ZipFile;

abstract class Parser {
    Database database;
    ZipFile zipFile;

    Parser(Database database, ZipFile zipFile) {
        this.database = database;
        this.zipFile = zipFile;
    }

    abstract void parse(DayOfTheWeek dayOfTheWeek);
}
