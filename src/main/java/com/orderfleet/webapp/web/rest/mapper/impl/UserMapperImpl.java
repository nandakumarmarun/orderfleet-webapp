package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.mapper.UserMapper;
@Component
public class UserMapperImpl extends UserMapper {



    @Override
    public UserDTO userToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setPid( user.getPid() );
        userDTO.setLogin( user.getLogin() );
        userDTO.setFirstName( user.getFirstName() );
        userDTO.setLastName( user.getLastName() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setMobile( user.getMobile() );
        userDTO.setActivated( user.getActivated() );
        userDTO.setChartColor( user.getChartColor() );
        userDTO.setDashboardUIType( user.getDashboardUIType() );
        userDTO.setDiscontinued( user.getDiscontinued() );
        Set<String> set = stringsFromAuthorities( user.getAuthorities() );
        if ( set != null ) {
            userDTO.setAuthorities( set );
        }
        userDTO.setLangKey( user.getLangKey() );
        userDTO.setDeviceKey( user.getDeviceKey() );

        return userDTO;
    }

    @Override
    public List<UserDTO> usersToUserDTOs(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>();
        for ( User user : users ) {
            list.add( userToUserDTO( user ) );
        }

        return list;
    }

    @Override
    public User userDTOToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setCompany( companyFromPid( userDTO.getCompanyPid() ) );
        user.setLogin( userDTO.getLogin() );
        user.setFirstName( userDTO.getFirstName() );
        user.setLastName( userDTO.getLastName() );
        user.setEmail( userDTO.getEmail() );
        user.setMobile( userDTO.getMobile() );
        user.setActivated( userDTO.isActivated() );
        user.setLangKey( userDTO.getLangKey() );
        Set<Authority> set = authoritiesFromStrings( userDTO.getAuthorities() );
        if ( set != null ) {
            user.setAuthorities( set );
        }
        user.setChartColor( userDTO.getChartColor() );
        user.setDashboardUIType( userDTO.getDashboardUIType() );
        user.setDiscontinued( userDTO.getDiscontinued() );

        return user;
    }

    @Override
    public List<User> userDTOsToUsers(List<UserDTO> userDTOs) {
        if ( userDTOs == null ) {
            return null;
        }

        List<User> list = new ArrayList<User>();
        for ( UserDTO userDTO : userDTOs ) {
            list.add( userDTOToUser( userDTO ) );
        }

        return list;
    }

}
