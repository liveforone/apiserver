package apiserver.apiserver.init;

import apiserver.apiserver.entity.category.Category;
import apiserver.apiserver.entity.member.Member;
import apiserver.apiserver.entity.member.Role;
import apiserver.apiserver.entity.member.RoleType;
import apiserver.apiserver.entity.repository.category.CategoryRepository;
import apiserver.apiserver.exception.RoleNotFoundException;
import apiserver.apiserver.entity.repository.member.MemberRepository;
import apiserver.apiserver.entity.repository.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestInitDB {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CategoryRepository categoryRepository;

    private String adminEmail = "admin@admin.com";
    private String member1Email = "member1@member.com";
    private String member2Email = "member2@member.com";
    private String password = "123456a!";

    private void initRole() {
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin() {
        memberRepository.save(
                new Member(adminEmail, passwordEncoder.encode(password), "admin", "admin",
                        List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                                roleRepository.findByRoleType(RoleType.ROLE_ADMIN).orElseThrow(RoleNotFoundException::new)))
        );
    }

    private void initTestMember() {
        memberRepository.saveAll(
                List.of(
                        new Member(member1Email, passwordEncoder.encode(password), "member1", "member1",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))),
                        new Member(member2Email, passwordEncoder.encode(password), "member2", "member2",
                                List.of(roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new))))
        );
    }

    private void initCategory() {
        Category category1 = new Category("category1", null);
        Category category2 = new Category("category2", category1);
        categoryRepository.saveAll(List.of(category1, category2));
    }

    @Transactional
    public void initDB() {
        initRole();
        initTestAdmin();
        initTestMember();
        initCategory();
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getMember1Email() {
        return member1Email;
    }

    public String getMember2Email() {
        return member2Email;
    }

    public String getPassword() {
        return password;
    }
}
