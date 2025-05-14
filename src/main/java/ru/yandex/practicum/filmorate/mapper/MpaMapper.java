package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.dto.mpa.NewMpaRequest;
import ru.yandex.practicum.filmorate.dto.mpa.UpdateMpaRequest;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaMapper {
    public Mpa mapToMpa(NewMpaRequest request) {
        return new Mpa(
                null,
                request.getName()
        );
    }

    public Mpa updateFromRequest(Mpa existing, UpdateMpaRequest request) {
        if (request.hasName()) existing.setName(request.getName());
        return existing;
    }

    public MpaDto mapToMpaDto(Mpa mpa) {
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }
}
