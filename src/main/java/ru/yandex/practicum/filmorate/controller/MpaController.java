package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> findAll() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{mpaId}")
    public MpaDto findById(@PathVariable Integer mpaId) {
        return mpaService.getMpaById(mpaId);
    }
}
