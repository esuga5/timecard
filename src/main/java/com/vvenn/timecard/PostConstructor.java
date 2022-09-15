package com.vvenn.timecard;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import com.vvenn.timecard.form.UserForm;
import com.vvenn.timecard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * アプリケーションのBeanライフサイクルを管理します。
 */
@Component
public class PostConstructor {

    @Autowired
    private UserService userService;

    /**
     * Beanコンテキスト生成後に呼び出され、初回ユーザーをDBに登録します。
     * 
     * @throws ParseException
     */
    @PostConstruct
    public void initAfterStartup() throws ParseException {

        List<UserForm> userForms = new ArrayList<>();

        UserForm userForm = new UserForm();
        userForm.setUsername("test@test.com");
        userForm.setPassword("testpass");
        userForm.setDisplayName("testユーザー");
        userForm.setSectionCode("1000");
        userForm.setJoinedAt("2020-05-02");
        userForm.setAdminFlag(false);

        userForms.add(userForm);

        UserForm adminForm = new UserForm();
        adminForm.setUsername("admin@test.com");
        adminForm.setPassword("testpass");
        adminForm.setDisplayName("adminユーザー");
        adminForm.setSectionCode("2000");
        adminForm.setJoinedAt("2020-05-01");
        adminForm.setAdminFlag(true);

        userForms.add(adminForm);

        Random random = new Random();
        for (int i = 0; i < 100; i++) {

            UserForm dummyForm = new UserForm();

            dummyForm.setUsername("dummy" + i + "@test.com");
            dummyForm.setPassword("testpass");
            dummyForm.setDisplayName("dummyユーザー" + i);
            dummyForm.setSectionCode((random.nextInt(2) + 1) + "000");
            dummyForm.setJoinedAt("2020-06-0" + (random.nextInt(9) + 1));
            dummyForm.setAdminFlag(random.nextBoolean());

            userForms.add(dummyForm);
        }

        // app起動時の処理
        for (int i = 0; i < userForms.size(); i++) {
            try {
                userService.loadUserByUsername(userForms.get(i).getUsername());
            } catch (UsernameNotFoundException e) {
                userService.registerUser(userForms.get(i).getUsername(), userForms.get(i).getPassword(),
                        userForms.get(i).getDisplayName(), userForms.get(i).getSectionCode(),
                        userForms.get(i).getJoinedAt(), userForms.get(i).getAdminFlag(), false);
            }
        }
    }
}