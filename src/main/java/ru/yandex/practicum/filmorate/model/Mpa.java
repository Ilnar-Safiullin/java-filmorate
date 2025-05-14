package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
}
