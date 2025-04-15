package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Film getFilmById(int id);
    ArrayList<Film> getAllFilms();
    void deleteFilm(int id);
    Map<Integer, Set<Integer>> getFilmLikes();
}