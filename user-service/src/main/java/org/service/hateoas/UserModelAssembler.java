package org.service.hateoas;

import org.service.controller.UserController;
import org.service.dto.UserResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>> {

    @Override
    public EntityModel<UserResponseDto> toModel(UserResponseDto dto) {
        return EntityModel.of(dto)
                .add(linkTo(UserController.class)
                        .slash(dto.getId())
                        .withSelfRel())
                .add(linkTo(UserController.class)
                        .withRel("users"));
    }

    public CollectionModel<EntityModel<UserResponseDto>> toCollectionModel(
            List<UserResponseDto> dtos) {

        List<EntityModel<UserResponseDto>> models = dtos.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models)
                .add(linkTo(UserController.class).withSelfRel());
    }
}