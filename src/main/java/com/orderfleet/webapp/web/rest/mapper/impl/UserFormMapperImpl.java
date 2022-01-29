package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.web.rest.dto.UserFormDTO;
import com.orderfleet.webapp.web.rest.mapper.UserFormMapper;
@Component
public class UserFormMapperImpl extends UserFormMapper {

  @Override
    public UserFormDTO userFormToUserFormDTO(UserForm userForm) {
        if ( userForm == null ) {
            return null;
        }

        UserFormDTO userFormDTO = new UserFormDTO();

        userFormDTO.setFormPid( userFormFormPid( userForm ) );
        userFormDTO.setUserPid( userFormUserPid( userForm ) );
        userFormDTO.setUserName( userFormUserLogin( userForm ) );
        userFormDTO.setFormName( userFormFormName( userForm ) );
        userFormDTO.setSortOrder( userForm.getSortOrder() );

        return userFormDTO;
    }

    @Override
    public List<UserFormDTO> userFormsToUserFormDTOs(List<UserForm> userForms) {
        if ( userForms == null ) {
            return null;
        }

        List<UserFormDTO> list = new ArrayList<UserFormDTO>();
        for ( UserForm userForm : userForms ) {
            list.add( userFormToUserFormDTO( userForm ) );
        }

        return list;
    }

    @Override
    public UserForm userFormDTOToUserForm(UserFormDTO userFormDTO) {
        if ( userFormDTO == null ) {
            return null;
        }

        UserForm userForm = new UserForm();

        userForm.setForm( userFormFormPid( userFormDTO.getFormPid() ) );
        userForm.setUser( userFormUserPid( userFormDTO.getUserPid() ) );
        userForm.setSortOrder( userFormDTO.getSortOrder() );

        return userForm;
    }

    @Override
    public List<UserForm> userFormDTOsToUserForms(List<UserFormDTO> userFormDTOs) {
        if ( userFormDTOs == null ) {
            return null;
        }

        List<UserForm> list = new ArrayList<UserForm>();
        for ( UserFormDTO userFormDTO : userFormDTOs ) {
            list.add( userFormDTOToUserForm( userFormDTO ) );
        }

        return list;
    }

    private String userFormFormPid(UserForm userForm) {

        if ( userForm == null ) {
            return null;
        }
        Form form = userForm.getForm();
        if ( form == null ) {
            return null;
        }
        String pid = form.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userFormUserPid(UserForm userForm) {

        if ( userForm == null ) {
            return null;
        }
        User user = userForm.getUser();
        if ( user == null ) {
            return null;
        }
        String pid = user.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String userFormUserLogin(UserForm userForm) {

        if ( userForm == null ) {
            return null;
        }
        User user = userForm.getUser();
        if ( user == null ) {
            return null;
        }
        String login = user.getLogin();
        if ( login == null ) {
            return null;
        }
        return login;
    }

    private String userFormFormName(UserForm userForm) {

        if ( userForm == null ) {
            return null;
        }
        Form form = userForm.getForm();
        if ( form == null ) {
            return null;
        }
        String name = form.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

}
