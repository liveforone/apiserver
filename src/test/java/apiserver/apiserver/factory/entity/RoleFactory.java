package apiserver.apiserver.factory.entity;

import apiserver.apiserver.entity.member.Role;
import apiserver.apiserver.entity.member.RoleType;

public class RoleFactory {

    public static Role createRole() {
        return new Role(RoleType.ROLE_NORMAL);
    }
}
