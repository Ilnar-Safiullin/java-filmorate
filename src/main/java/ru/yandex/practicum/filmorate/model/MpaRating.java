package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MpaRating {
    private Integer mpaId;
    private String name;

    public MpaRating(Integer mpaId, String name) {
        this.mpaId = mpaId;
        this.name = name;
    }
}